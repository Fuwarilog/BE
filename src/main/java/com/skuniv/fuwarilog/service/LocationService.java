package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponse;
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
     * @param keyword 검색 단어
     * @return places 장소명, 주소, 위치
     */
    public List<LocationResponse.PlaceDTO> searchPlaces(String keyword) {
        URI uri = UriComponentsBuilder.fromUriString("https://maps.googleapis.com/maps/api/place/textsearch/json")
                .queryParam("query", keyword)
                .queryParam("key", apiKey)
                .build()
                .toUri();

        log.info(uri.toString());

        Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        List<LocationResponse.PlaceDTO> places = new ArrayList<>();
        if(results != null) {
            for(Map<String, Object> place : results) {
                String name = (String) place.get("name");
                Map<String, Object> geometry = (Map<String, Object>) place.get("geometry");
                Map<String, Object> location = (Map<String, Object>) geometry.get("location");
                double lat = ((Number) location.get("lat")).doubleValue();
                double lng = ((Number) location.get("lng")).doubleValue();
                String placeId = (String) place.get("place_id");
                places.add(new LocationResponse.PlaceDTO(name, lat, lng, placeId));
            }
        }
        return places;
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
            DiaryContent content = contentOpt.stream().findFirst()
                    .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYCONTENT));

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

                String currentContent = Optional.ofNullable(content.getContent()).orElse("");
                log.info(currentContent);
                if (!currentContent.contains(tagText)) {
                    String updatedContent = tagText + "\n" + currentContent;
                    log.info(updatedContent);
                    content.setContent(updatedContent);
                }
                list.setUpdatedAt(LocalDateTime.now()); // diarycontent가 아닌 DiaryList의 uodatedAt을 업데이트 해야한다.
                diaryContentRepository.save(content);
            }

            return LocationResponse.LocationInfoDTO.from(location);

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BadRequestException(ErrorResponseStatus.SAVE_DATA_ERROR);
        }
    }

    public void deleteBookmark(Long userId, Long locationId) {
        Location location = locationRepository.findByIdAndUserId(locationId, userId);

        location.setBookmarked(false);
        location.setBookmarkedAt(null);
        locationRepository.save(location);

        // 다이어리에서 태그 제거
        List<DiaryContent> contentOpt = diaryContentRepository.findByUserIdAndTripDate(userId, LocalDate.now());
        DiaryContent content = contentOpt.stream().findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYCONTENT));

        String currentContent = content.getContent();
        DiaryList list = diaryListRepository.findById(content.getDiaryListId())
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.NOT_EXIST_DIARYLIST));

        String tagToRemove = "#" + location.getPlaceName().replaceAll("\\s+", "");
        String updatedContent = currentContent.replace(tagToRemove, "").replaceAll("(?m)^\\s*$[\r\n]+", ""); // 빈 줄 정리
        content.setContent(updatedContent.trim());
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
        String url = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/directions/json")
                .queryParam("origin", dto.getOrigin())
                .queryParam("destination", dto.getDestination())
                .queryParam("mode", "transit")
                .queryParam("key", apiKey)
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !"OK".equals(response.get("status"))) {
            throw new RuntimeException("Google Directions API 호출 실패: " + response);
        }

        List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
        Map<String, Object> route = routes.get(0);
        Map<String, Object> leg = ((List<Map<String, Object>>) route.get("legs")).get(0);

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
}
