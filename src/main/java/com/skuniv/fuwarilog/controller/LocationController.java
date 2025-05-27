package com.skuniv.fuwarilog.controller;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.dto.Location.LocationRequest;
import com.skuniv.fuwarilog.dto.Location.LocationResponse;
import com.skuniv.fuwarilog.dto.VisitedRouteDocument.VisitedRouteDocumentRequest;
import com.skuniv.fuwarilog.dto.VisitedRouteDocument.VisitedRouteDocumentResponse;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import com.skuniv.fuwarilog.service.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Google Map API", description = "구글맵 데이터 반환")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/maps")
public class LocationController {

    private final LocationService locationService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/location")
    @Operation(summary = "현재 위치 조회 API", description = "요청 시 현재 위치 반환")
    public ResponseEntity<LocationResponse.CurrentLocationDTO> getCurrentLocation(
            @RequestHeader("Authorization") String token,
            @RequestBody LocationRequest.LocationReqDTO dto) {

        // 1. 토큰 확인
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN); }

        // 2. 토큰 -> 사용자 Id 반환
        Long userId = jwtTokenProvider.getUserId(token);
        LocationResponse.CurrentLocationDTO result = locationService.getCurrentLocation(userId, dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    @Operation(summary = "장소 검색 API", description = "검색어 기반 장소 리스트 반환")
    public ResponseEntity<List<LocationResponse.PlaceDTO>> searchPlace(
            @RequestHeader("Authorization") String token,
            @RequestParam String keyword) {

        // 1. 토큰 확인
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN); }

        List<LocationResponse.PlaceDTO> result = locationService.searchPlaces(keyword);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/bookmark")
    @Operation(summary = "북마크 장소 저장 API", description = "북마크 저장")
    public ResponseEntity<LocationResponse.LocationInfoDTO> savePlace(
            @RequestHeader("Authorization") String token,
            @RequestBody LocationRequest.LocationBookmarkReqDTO dto) {
        // 1. 토큰 확인
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN); }

        // 2. 사용자 고유 번호 추출
        Long userId = jwtTokenProvider.getUserId(token);

        LocationResponse.LocationInfoDTO result = locationService.saveBookmark(userId, dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/bookmark/{locationId}")
    @Operation(summary = "북마크 삭제 API", description = "북마크 id 삭제 요청 시 성공 반환")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long locationId,
                                               @RequestHeader("Authorization") String token) {
        // 1. 토큰 확인
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN); }

        // 2. 사용자 고유 번호 추출
        Long userId = jwtTokenProvider.getUserId(token);

        locationService.deleteBookmark(userId, locationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/route")
    @Operation(summary = "경로 탐색 API", description = "출발지, 목적지 입력 기반 경로 정보 반환")
    public ResponseEntity<VisitedRouteDocumentResponse.RouteDTO> saveRoute(
            @RequestHeader("Authorization") String token,
            @RequestBody VisitedRouteDocumentRequest.RouteRequestDTO dto) {
        // 1. 토큰 확인
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN); }
        Long userId = jwtTokenProvider.getUserId(token);

        VisitedRouteDocumentResponse.RouteDTO result = locationService.getRoute(userId, dto);
        return ResponseEntity.ok(result);
    }
}
