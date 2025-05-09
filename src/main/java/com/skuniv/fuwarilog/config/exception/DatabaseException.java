package com.skuniv.fuwarilog.config.exception;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.Getter;

@Getter
public class DatabaseException extends RuntimeException{
    private final ErrorResponseStatus status;

    public DatabaseException(ErrorResponseStatus status) {
        this.status = status;
    }

}
