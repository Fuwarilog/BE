package com.skuniv.fuwarilog.domain.enums;

import com.skuniv.fuwarilog.config.exception.BadRequestException;
import com.skuniv.fuwarilog.config.exception.ErrorResponseStatus;
import lombok.Getter;

@Getter
public enum CurrencyCode {
    KRW(0), USD(1), JPY(2), CNY(3);

    private final int code;

    CurrencyCode(int code) { this.code = code; }

    public static CurrencyCode fromCode(int code) {
            return switch (code) {
            case 0 -> KRW;
            case 1 -> USD;
            case 2 -> JPY;
            case 3 -> CNY;
            default -> throw new BadRequestException(ErrorResponseStatus.INVALID_CURRENCY_CODE);
            };
    }
}
