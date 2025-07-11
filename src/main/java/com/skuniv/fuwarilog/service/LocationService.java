package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.*;
import com.skuniv.fuwarilog.dto.Location.LocationRequest;
import com.skuniv.fuwarilog.dto.Location.LocationResponse;
import com.skuniv.fuwarilog.dto.VisitedRouteDocument.VisitedRouteDocumentRequest;
import com.skuniv.fuwarilog.dto.VisitedRouteDocument.VisitedRouteDocumentResponse;
import com.skuniv.fuwarilog.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final DiaryContentRepository diaryContentRepository;
    private final DiaryListRepository diaryListRepository;
    private final VisitedRouteRepository visitedRouteRepository;

    @Value("${google.maps.key}")
    private String apiKey;

    /**
     * @implSpec 사용자 현재 위치 반환
     * @param userId 사용자 고유 번호
     * @param locateDto 위치 요청 값(위도, 경도)
     * @return address 주소, locateDto.getLatitude() 위도, locateDto.getLongitude()) 경도
     */
    public LocationResponse.CurrentLocationDTO getCurrentLocation(Long userId, LocationRequest.LocationReqDTO locateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        URI uri = UriComponentsBuilder.fromUriString("https://maps.googleapis.com/maps/api/geocode/json")
                .queryParam("latlng", locateDto.getLatitude() + "," + locateDto.getLongitude())
                .queryParam("key", apiKey)
                .build()
                .toUri();

        log.info(uri.toString());

        Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        log.info("results: " + results);

        if(results != null && !results.isEmpty()) {
            String address = (String) results.get(0).get("formatted_address");
            return new LocationResponse.CurrentLocationDTO(address, locateDto.getLatitude(), locateDto.getLongitude());
        } else {
            return new LocationResponse.CurrentLocationDTO("Unkown", locateDto.getLatitude(), locateDto.getLongitude());
        }
    }

    /**
     * @implSpec 검색어에 따른 장소 리스트 반환
     * @param dto 검색 단어
     * @return places 장소명, 주소, 위치
     */
    public List<LocationResponse.PlaceDTO> searchPlaces(LocationRequest.LocationSearchDTO dto) {

        // 1. 위치 정보 검증
        validatedLocationRequest(dto);

        double currentLat = dto.getLatitude();
        double currentLng = dto.getLongitude();
        int radius = dto.getRadius() != null ? dto.getRadius() : 3000;

        URI uri = UriComponentsBuilder.fromUriString("https://maps.googleapis.com/maps/api/place/textsearch/json")
                .queryParam("query", dto.getKeyword())
                .queryParam("locationbias", "circle:" + radius + "@" +  currentLat + "," + currentLng)
                .queryParam("fields", "name,formatted_address,geometry,rating,price_level")
                .queryParam("key", apiKey)
                .build()
                .toUri();

        log.info(uri.toString());

        try {
            Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
            if (response == null) {
                log.error("Google Places API returned null");
                return new ArrayList<>();
            }

            String status = (String) response.get("status");
            if (!"OK".equals(status) && !"ZERO_RESULTS".equals(status)) {
                log.error("Google Places API error: {}", status);
                throw new RuntimeException("Google Places API returned status " + status);
            }

            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            List<LocationResponse.PlaceDTO> places = new ArrayList<>();

            if (results != null) {
                for (Map<String, Object> place : results) {
                    String name = (String) place.get("name");
                    String address = (String) place.get("formatted_address");

                    Map<String, Object> geometry = (Map<String, Object>) place.get("geometry");
                    Map<String, Object> location = (Map<String, Object>) geometry.get("location");

                    double lat = ((Number) location.get("lat")).doubleValue();
                    double lng = ((Number) location.get("lng")).doubleValue();
                    String placeId = (String) place.get("place_id");

                    places.add(new LocationResponse.PlaceDTO(name, address, lat, lng, placeId));
                }
            }
            return places;
        } catch (Exception e) {
            log.error("Exception: ", e);
            throw new BadRequestException(ErrorResponseStatus.RESPONSE_ERROR);
        }
    }

    private void validatedLocationRequest(LocationRequest.LocationSearchDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO is null");
        }

        if (dto.getKeyword() == null || dto.getKeyword().trim().isEmpty()) {
            throw new IllegalArgumentException("Keyword is null or empty");
        }

        if (dto.getLongitude() == null || dto.getLatitude() == null) {
            throw new IllegalArgumentException("Location is null or empty");
        }

        if (dto.getLatitude() < -90 || dto.getLatitude() > 90) {
            throw new IllegalArgumentException("Latitude is invalid range(-90 ~ 90)");
        }

        if (dto.getLongitude() < -180 || dto.getLongitude() > 180) {
            throw new IllegalArgumentException("Longitude is invalid range(-180 ~ 180)");
        }

        if (dto.getRadius() != null && (dto.getRadius() < 100 || dto.getRadius() > 50000)) {
            throw new IllegalArgumentException("Location is out of range");
        }
    }

    /**
     * @implSpec 장소 북마크로 저장
     * @param userId 사용자 고유 번호
     * @param dto 북마크 요청 DTO
     */
    public LocationResponse.LocationInfoDTO saveBookmark(Long userId, LocationRequest.LocationBookmarkReqDTO dto) {
        try {
            // 1. 사용자 확인
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

            Location location = locationRepository.findByUserIdAndPlaceId(userId, dto.getPlaceId());

            if (location == null) {

                // 2. 북마크 장소 추가
                location = Location.builder()
                        .user(user)
                        .placeId(dto.getPlaceId())
                        .placeName(dto.getName())
                        .latitude(dto.getLatitude())
                        .longitude(dto.getLongitude())
                        .bookmarked(true)
                        .bookmarkedAt(LocalDateTime.now())
                        .build();
                locationRepository.save(location);
            } else {
                throw new BadRequestException(ErrorResponseStatus.ALREADY_EXIST_LOCATION);
            }

            // 3. 다이어리 내용에 태그 삽입
            List<DiaryContent> contentOpt = diaryContentRepository.findByUserIdAndTripDate(userId, LocalDate.now());
            Optional<DiaryContent> optionalContent = contentOpt.stream().findFirst();

            if (optionalContent.isPresent()) {
                DiaryContent content = optionalContent.get();

                DiaryList list = diaryListRepository.findById(content.getDiaryListId())
                        .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYLIST));

                // 이미 존재하는 태그인지 확인
                String tagText = "#" + location.getPlaceName().replaceAll("\\s+", "");
                boolean tagExists = content.getTags() != null && content.getTags().stream()
                        .anyMatch(tag -> tag.getTagText().equals(tagText));

                if (!tagExists) {
                    LocationTag tag = LocationTag.builder()
                            .placeName(location.getPlaceName())
                            .placeUrl(location.getPlaceUrl())
                            .address(location.getAddress())
                            .latitude(location.getLatitude())
                            .longitude(location.getLongitude())
                            .tagText(tagText)
                            .build();

                    if (content.getTags() == null) {
                        content.setTags(new ArrayList<>());
                    }
                    content.getTags().add(tag);
                    list.setUpdatedAt(LocalDateTime.now()); // diarycontent가 아닌 DiaryList의 uodatedAt을 업데이트 해야한다.
                    diaryContentRepository.save(content);
                }
            }

            return LocationResponse.LocationInfoDTO.from(location);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.SAVE_DATA_ERROR);
        }
    }

    public void deleteBookmark(Long userId, Long locationId) {
        Location location = locationRepository.findByIdAndUserId(locationId, userId);

        locationRepository.delete(location);

        // 다이어리에서 태그 제거
        List<DiaryContent> contentOpt = diaryContentRepository.findByUserIdAndTripDate(userId, LocalDate.now());
        DiaryContent content = contentOpt.stream().findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYCONTENT));

        DiaryList list = diaryListRepository.findById(content.getDiaryListId())
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYLIST));

        String tagToRemove = "#" + location.getPlaceName().replaceAll("\\s+", "");

        String currentContent = Optional.ofNullable(content.getContent()).orElse("");
        String updatedContent = currentContent.replace(tagToRemove, "").replaceAll("(?m)^\\s*$[\r\n]+", ""); // 빈 줄 정리
        content.setContent(updatedContent.trim());

        if (content.getTags() != null) {
            content.getTags().removeIf(tag -> tag.getTagText().equals(tagToRemove));
        }

        list.setUpdatedAt(LocalDateTime.now());
        diaryContentRepository.save(content);
    }

    /**
     * @implSpec 경로 탐색 결과 반환
     * @param userId 사용자 고유 번호
     * @param dto 경로 탐색 요청 DTO
     * @return duration, distance, instructions 경로 탐색 값 반환
     */
    public VisitedRouteDocumentResponse.RouteDTO getRoute(Long userId, VisitedRouteDocumentRequest.RouteRequestDTO dto) {
        String originPlaceId = getPlaceId(dto.getOrigin());
        String destinationPlaceId = getPlaceId(dto.getDestination());

        // 1) google map 경로 탐색 조건 설정 url 생성
        // - mode: transit → 대중교통 기반 경로 요청
        // - transit_mode: subway|bus → 지하철/버스만 탐색 (도보, 기차 제외)
        // - departure_time: now → 현재 시각 기준으로 소요 시간 계산
        // - traffic_model: best_guess → 교통 상황 예측 기반 시간 추정
        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/directions/json")
                .queryParam("origin", "place_id:" + originPlaceId)
                .queryParam("destination", "place_id:" + destinationPlaceId)
                .queryParam("mode", "transit")
                .queryParam("transit_mode", "subway|bus")
                .queryParam("departure_time", "now")
                .queryParam("traffic_model", "best_guess")
                .queryParam("key", apiKey)
                .build()
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !"OK".equals(response.get("status"))) {
            throw new RuntimeException("Google Directions API 호출 실패: " + response);
        }

        // 2) 경로 중 첫번째 경로 반환 - 가장 추천되는 경로 먼저 조회
        List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
        Map<String, Object> route = routes.get(0);
        Map<String, Object> leg = ((List<Map<String, Object>>) route.get("legs")).get(0);

        // 3) 거리 및 소요 시간 정보 파싱 - 반환되는 duration은 "도보 + 대중교통" 전체 소요 시간을 의미함.
        String distanceText = ((Map<String, Object>) leg.get("distance")).get("text").toString();
        int distanceValue = (int) ((Map<String, Object>) leg.get("distance")).get("value");
        String durationText = ((Map<String, Object>) leg.get("duration")).get("text").toString();
        int durationValue = (int) ((Map<String, Object>) leg.get("duration")).get("value");

        // MongoDB 저장
        VisitedRouteDocument routeDoc = VisitedRouteDocument.builder()
                .userId(userId)
                .origin(dto.getOrigin())
                .destination(dto.getDestination())
                .distanceText(distanceText)
                .distanceValue(distanceValue)
                .durationText(durationText)
                .durationValue(durationValue)
                .tripDate(dto.getTripDate())
                .build();

        visitedRouteRepository.save(routeDoc);
        return new VisitedRouteDocumentResponse.RouteDTO(distanceText, durationText, dto.getOrigin(), dto.getDestination());
    }

    private String getPlaceId(String placeName) {
        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/place/findplacefromtext/json")
                .queryParam("input", placeName)
                .queryParam("inputtype", "textquery")
                .queryParam("fields", "place_id")
                .queryParam("key", apiKey)
                .build()
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !"OK".equals(response.get("status"))) {
            throw new RuntimeException("Google Places API 호출 실패: " + response);
        }

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (candidates.isEmpty()) {
            throw new RuntimeException("장소 검색 결과 없음: " + placeName);
        }

        return candidates.get(0).get("place_id").toString();
    }


    public LocationResponse.LocationDetailDTO getLocationDetail(String placeId) {

        URI uri = UriComponentsBuilder.fromUriString("https://maps.googleapis.com/maps/api/place/details/json")
                .queryParam("place_id", placeId)
                .queryParam("fields", "name,formatted_phone_number,rating,opening_hours,formatted_address,geometry")
                .queryParam("key", apiKey)
                .build()
                .toUri();

        log.info("Google Place Details API URI: {}", uri);

        try {
            Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
            if (response == null || !"OK".equals(response.get("status"))) {
                throw new RuntimeException("Google Place Details API 호출 실패");
            }

            Map<String, Object> result = (Map<String, Object>) response.get("result");

            String name = (String) result.get("name");
            String phone = (String) result.get("formatted_phone_number");
            String address = (String) result.get("formatted_address");
            Double rating = result.get("rating") != null ? ((Number) result.get("rating")).doubleValue() : null;

            Map<String, Object> geometry = (Map<String, Object>) result.get("geometry");
            Map<String, Object> location = (Map<String, Object>) geometry.get("location");
            double latitude = ((Number) location.get("lat")).doubleValue();
            double longitude = ((Number) location.get("lng")).doubleValue();

            List<String> weekdayText = null;
            if (result.containsKey("opening_hours")) {
                Map<String, Object> openingHours = (Map<String, Object>) result.get("opening_hours");
                weekdayText = (List<String>) openingHours.get("weekday_text");
            }

            return new LocationResponse.LocationDetailDTO(name, phone, address, rating, latitude, longitude, weekdayText);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.SAVE_DATA_ERROR);
        }
    }
}
