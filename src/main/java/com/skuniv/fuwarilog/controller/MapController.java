package com.skuniv.fuwarilog.controller;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.dto.MapRequest;
import com.skuniv.fuwarilog.dto.MapResponse;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import com.skuniv.fuwarilog.service.MapService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Google Map API", description = "구글맵 데이터 반환")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/maps")
public class MapController {

    private final MapService mapService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/location")
    @Operation(summary = "현재 위치 조회 API", description = "요청 시 현재 위치 반환")
    public ResponseEntity<MapResponse.CurrentLocationDTO> getCurrentLocation(
            @RequestHeader("Authorization") String token,
            @RequestBody MapRequest.LocationReqDTO dto) {

        // 1. 토큰 확인
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN); }

        // 2. 토큰 -> 사용자 Id 반환
        Long userId = jwtTokenProvider.getUserId(token);
        MapResponse.CurrentLocationDTO result = mapService.getCurrentLocation(userId, dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/search")
    @Operation(summary = "장소 검색 API", description = "검색어 기반 장소 리스트 반환")
    public ResponseEntity<List<MapResponse.MapSearchResDTO>> searchPlace(
            @RequestHeader("Authorization") String token,
            @RequestParam String keyword) {

        // 1. 토큰 확인
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN); }

        List<MapResponse.MapSearchResDTO> result = mapService.searchPlaces(keyword);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/bookmark")
    @Operation(summary = "검색/북마크 장소 저장 API", description = "장소 기록 및 북마크 저장")
    public ResponseEntity<Void> savePlace(
            @RequestHeader("Authorization") String token,
            @RequestBody MapRequest.MapBookmarkReqDTO dto) {
        // 1. 토큰 확인
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN); }
        Long userId = jwtTokenProvider.getUserId(token);

        mapService.savePlace(userId, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/route")
    @Operation(summary = "경로 탐색 API", description = "출발지, 목적지 입력 기반 경로 정보 반환")
    public ResponseEntity<MapResponse.RouteResDTO> saveRoute(
            @RequestHeader("Authorization") String token,
            @RequestBody MapRequest.RouteReqDTO dto) {
        // 1. 토큰 확인
        if(!jwtTokenProvider.validateToken(token)) { throw new BadRequestException(ErrorResponseStatus.INVALID_TOKEN); }
        Long userId = jwtTokenProvider.getUserId(token);

        MapResponse.RouteResDTO result = mapService.getRoute(userId, dto);
        return ResponseEntity.ok(result);
    }
}
