package com.skuniv.fuwarilog.controller;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.User.UserRequest;
import com.skuniv.fuwarilog.dto.User.UserResponse;
import com.skuniv.fuwarilog.repository.UserRepository;
import com.skuniv.fuwarilog.security.jwt.JwtTokenProvider;
import com.skuniv.fuwarilog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "User API", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @GetMapping("/my-info")
    @Operation(summary = "사용자 정보 조회", description="성공시 사용자 정보 반환")
    public ResponseEntity<UserResponse.UserInfoDTO> getUserInfo(
            Authentication authentication) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));


        UserResponse.UserInfoDTO userInfo = userService.findUserInfo(user.getId());
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping(value = "/my-info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "내 정보 수정", description = "성공시 사용자 업데이트 정보 반환")
    public ResponseEntity<UserResponse.UserInfoDTO> updateUserInfo(
            Authentication authentication,
            @RequestPart(value = "userDto") @Valid UserRequest.UserInfoDTO userDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        String email = (String) authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException(ErrorResponseStatus.USER_NOT_FOUND));

        UserResponse.UserInfoDTO updateUser = userService.editUserInfo(user.getId(), userDto, image);
        return ResponseEntity.ok(updateUser);
    }
}
