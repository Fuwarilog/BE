package com.skuniv.fuwarilog.controller;

import com.skuniv.fuwarilog.dto.UserResponse;
import com.skuniv.fuwarilog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/my-info")
    @Operation(summary = "사용자 정보 조회", description="성공시 사용자 정보 반환")
    public ResponseEntity<UserResponse.UserInfoDTO> getUserInfo(
            @RequestParam(required = true) Long id) {

        UserResponse.UserInfoDTO userInfo = userService.findUserInfo(id);
        return ResponseEntity.ok(userInfo);
    }
}
