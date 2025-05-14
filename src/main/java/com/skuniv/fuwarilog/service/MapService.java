package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.MapRequest;
import com.skuniv.fuwarilog.dto.MapResponse;
import com.skuniv.fuwarilog.repository.MapRepository;
import com.skuniv.fuwarilog.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class MapService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private MapRepository mapRepository;
    private MapRequest mapRequest;
    private MapResponse mapResponse;

    @Value("${google.map.api-key}")
    private String apiKey;

    public MapResponse.CurrentLocationDTO getCurrentLocation(Long userId, MapRequest.LocationReqDTO locateDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        URI uri = UriComponentsBuilder.fromUriString("https://maps.googleapis.com/maps/api/geocode/json")
                .queryParam("latlng", locateDto.getLatitude() + "," + locateDto.getLongitude())
                .queryParam("key", apiKey)
                .build()
                .toUri();

        Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        if(results != null && !results.isEmpty()) {
            String address = (String) results.get(0).get("formatted_address");
            return new MapResponse.CurrentLocationDTO(address, locateDto.getLatitude(), locateDto.getLongitude());
        } else {
            return new MapResponse.CurrentLocationDTO("Unkown", locateDto.getLatitude(), locateDto.getLongitude());
        }
    }

    public List<MapResponse.PlaceDTO> searchPlaces(String keyword) {
        URI uri = UriComponentsBuilder.fromUriString("https://maps.googleapis.com/maps/api/geocode/json")
                .queryParam("query")
                .queryParam("key", apiKey)
                .build()
                .toUri();

        Map<String, Object> response = restTemplate.getForObject(uri, Map.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        List<MapResponse.PlaceDTO> places = new ArrayList<>();
        if(results != null) {
            for(Map<String, Object> place : results) {
                String name = (String) place.get("name");
                Map<String, Object> geometry = (Map<String, Object>) place.get("geometry");
                Map<String, Object> location = (Map<String, Object>) geometry.get("location");
                double lat = ((Number) location.get("lat")).doubleValue();
                double lng = ((Number) location.get("lng")).doubleValue();
                places.add(new MapResponse.PlaceDTO(name, lat, lng));
            }
        }
        return places;
    }

    public void savePlace(Long userId, MapRequest.MapBookmarkReqDTO dto) {
    }

    public MapResponse.RouteDTO getRoute(Long userId, MapRequest.RouteReqDTO dto) {
    }
}
