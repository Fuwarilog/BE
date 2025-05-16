package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.Location;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.LocationRequest;
import com.skuniv.fuwarilog.dto.LocationResponse;
import com.skuniv.fuwarilog.repository.LocationRepository;
import com.skuniv.fuwarilog.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

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
                places.add(new LocationResponse.PlaceDTO(name, lat, lng));
            }
        }
        return places;
    }

    /**
     * @implSpec 장소 북마크로 저장
     * @param userId 사용자 고유 번호
     * @param dto 북마크 요청 DTO
     */
    public void savePlace(Long userId, LocationRequest.MapBookmarkReqDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        Location location = Location.builder()
                .user(user)
                .placeName(dto.getName())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .bookmarked(dto.isBookmarked())
                .build();
        locationRepository.save(location);
    }

    /**
     * @implSpec 경로 탐색 결과 반환
     * @param userId 사용자 고유 번호
     * @param dto 경로 탐색 요청 DTO
     * @return duration, distance, instructions 경로 탐색 값 반환
     */
    // 다이어리에 넣을지 보류
    public LocationResponse.RouteDTO getRoute(Long userId, LocationRequest.RouteReqDTO dto) {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://maps.googleapis.com/maps/api/directions/json")
                .queryParam("origin", dto.getOrigin())
                .queryParam("destination", dto.getDestination())
                .queryParam("key", apiKey)
                .build()
                .toUri();

        Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
        List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");

        if (routes != null && !routes.isEmpty()) {
            Map<String, Object> firstRoute = routes.get(0);
            List<Map<String, Object>> legs = (List<Map<String, Object>>) firstRoute.get("legs");
            if (!legs.isEmpty()) {
                Map<String, Object> leg = legs.get(0);
                String duration = ((Map<String, Object>) leg.get("duration")).get("text").toString();
                String distance = ((Map<String, Object>) leg.get("distance")).get("text").toString();

                List<Map<String, Object>> steps = (List<Map<String, Object>>) leg.get("steps");
                List<String> instructions = new ArrayList<>();
                for (Map<String, Object> step : steps) {
                    instructions.add((String) step.get("html_instructions"));
                }

                return new LocationResponse.RouteDTO(duration, distance, instructions);
            }
        }

        return new LocationResponse.RouteDTO("N/A", "N/A", Collections.emptyList());
    }
}
