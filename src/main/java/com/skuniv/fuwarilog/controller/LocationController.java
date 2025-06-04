package com.skuniv.fuwarilog.controller;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.Location.LocationRequest;
import com.skuniv.fuwarilog.dto.Location.LocationResponse;
import com.skuniv.fuwarilog.dto.VisitedRouteDocument.VisitedRouteDocumentRequest;
import com.skuniv.fuwarilog.dto.VisitedRouteDocument.VisitedRouteDocumentResponse;
import com.skuniv.fuwarilog.repository.UserRepository;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import com.skuniv.fuwarilog.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Google Map API", description = "구글맵 데이터 반환")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/maps")
public class LocationController {

    private final LocationService locationService;
    private final UserRepository userRepository;

    @PostMapping("/location")
    @Operation(summary = "현재 위치 조회 API", description = "요청 시 현재 위치 반환")
    public ResponseEntity<LocationResponse.CurrentLocationDTO> getCurrentLocation(
            Authentication authentication,
            @RequestBody LocationRequest.LocationReqDTO dto) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        LocationResponse.CurrentLocationDTO result = locationService.getCurrentLocation(user.getId(), dto);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/place-detail")
    @Operation(summary = "장소 상세 정보 조회 API", description = "place_id 기반 장소 정보 조회 및 반환")
    public ResponseEntity<LocationResponse.LocationDetailDTO> getLocationDetail(
            Authentication authentication,
            @RequestParam String placeId) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        LocationResponse.LocationDetailDTO result = locationService.getLocationDetail(placeId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/search")
    @Operation(summary = "장소 검색 API", description = "검색어 기반 장소 리스트 반환")
    public ResponseEntity<List<LocationResponse.PlaceDTO>> searchPlace(
            Authentication authentication,
            @RequestBody LocationRequest.LocationSearchDTO dto) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        List<LocationResponse.PlaceDTO> result = locationService.searchPlaces(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/bookmark")
    @Operation(summary = "북마크 장소 저장 API", description = "북마크 저장")
    public ResponseEntity<LocationResponse.LocationInfoDTO> savePlace(
            Authentication authentication,
            @RequestBody LocationRequest.LocationBookmarkReqDTO dto) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        LocationResponse.LocationInfoDTO result = locationService.saveBookmark(user.getId(), dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/bookmark/{locationId}")
    @Operation(summary = "북마크 삭제 API", description = "북마크 id 삭제 요청 시 성공 반환")
    public ResponseEntity<Void> deleteBookmark(
            Authentication authentication,
            @PathVariable Long locationId) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        locationService.deleteBookmark(user.getId(), locationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/route")
    @Operation(summary = "경로 탐색 API", description = "출발지, 목적지 입력 기반 경로 정보 반환")
    public ResponseEntity<VisitedRouteDocumentResponse.RouteDTO> saveRoute(
            Authentication authentication,
            @RequestBody VisitedRouteDocumentRequest.RouteRequestDTO dto) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        VisitedRouteDocumentResponse.RouteDTO result = locationService.getRoute(user.getId(), dto);
        return ResponseEntity.ok(result);
    }
}
