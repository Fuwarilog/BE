package com.skuniv.fuwarilog.config.exception;

import lombok.Getter;

@Getter
public enum ErrorResponseStatus {

    // 2000
    REQUEST_ERROR(2000, "입력 값을 확인해주세요."),
    FCM_SEND_FAIL(2001, "FCM 전송에 실패했습니다."),
    INVALID_CURRENCY_CODE(2002, "잘못된 통화 코드입니다."),
    EXIST_USER_EMAIL(2003, "이메일이 존재합니다."),

    RESPONSE_ERROR(3000, "값을 불러오는데 실패했습니다."),

    SECRET_FILE_NOT_FOUND(4001, "파일을 찾을 수 없습니다."),
    INVALID_SECRET_FILE(4002, "잘못된 파일입니다."),
    USER_NOT_FOUND(4003, "사용자를 찾을 수 없습니다."),
    INVALID_TOKEN(4004, "잘못된 토큰입니다.");

    private final int code;
    private final String message;

    private ErrorResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
