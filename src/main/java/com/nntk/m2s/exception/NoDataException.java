package com.nntk.m2s.exception;

import lombok.Getter;

@Getter
public class NoDataException extends RuntimeException {


    public NoDataException(String message) {
        super(message);
    }


}
