package com.skuniv.fuwarilog.config.exception;

import lombok.Getter;

@Getter
public enum ErrorResponseStatus {

    // 2000
    REQUEST_ERROR(2000, "입력 값을 확인해주세요."),
    INVALID_CURRENCY_CODE(2001, "존재하지않는 통화코드입니다."),
    EXIST_USER_EMAIL(2002, "이미 이메일이 존재합니다."),
    INVALID_SAME_PASSWORD(2003, "이미 존재하는 비밀번호입니다."),
    INVALID_PASSWORD(2004, "비밀번호는 영문, 숫자, 기호로 이루어져야합니다."),
    INVALID_TOKEN(2005, "잘못된 인증 토큰입니다."),
    INVALID_SECRET_FILE(2006, "잘못된 SECRET 파일입니다."),
    ALREADY_EXIST_CONTENT(2007, "이미 존재하는 다이어리 내용입니다."),

    RESPONSE_ERROR(3000, "값을 불러오는데 실패했습니다."),
    SVAE_DIARY_IMAGE_ERROR(3001, "다이어리 이미지 저장 실패"),
    SAVE_PROFILE_IMAGE_ERROR(3002, "프로필 이미지 저장 실패"),
    SAVE_DATA_ERROR(3003, " 값을 저장하는데 실패했습니다."),

    CREDENTIAL_NOT_FOUND(4000, "Credential 파일을 찾을 수 없습니다."),
    SECRET_FILE_NOT_FOUND(4001, "SECRET 파일을 찾을 수 없습니다."),
    USER_NOT_FOUND(4003, "사용자를 찾을 수 없습니다."),
    TRIP_NOT_FOUND(4005, "여행일정을 찾을 수 없습니다."),
    NOT_EXIST_DIARYLIST(4006, "다이어리를 찾을 수 없습니다."),
    NOT_FOUND_LOCATION(4007, "해당 장소를 찾을 수 없습니다."),
    NOT_EXIST_DIARYCONTENT(4008, "다이어리 내용을 찾을 수 없습니다."),
    DIARY_NOT_FOUND(4009, "다이어리 폴더를 찾을 수 없습니다."),
    NOT_EXIST_POST(4010, "게시글을 찾을 수 없습니다.");

    private final int code;
    private final String message;

    private ErrorResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
