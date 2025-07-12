package com.nntk.m2s.exception.handler;


import com.nntk.m2s.exception.NoDataException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
public class NoDataExceptionHandler {

    @ExceptionHandler(value = NoDataException.class)
    public Object handlerException(NoDataException exception) {

        return null;
    }
}
