package com.training.common.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理，将业务异常转换为统一响应结构
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException ex) {
        ErrorCode code = ex.getErrorCode();
        log.warn("业务异常: {}", ex.getMessage());
        return Result.error(code.getCode(), ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<?> handleValidateException(Exception ex) {
        String message = ErrorCode.VALIDATE_FAILED.getMessage();
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) ex;
            message = e.getBindingResult().getFieldErrors().stream()
                    .findFirst()
                    .map(err -> err.getDefaultMessage())
                    .orElse(message);
        } else if (ex instanceof BindException) {
            BindException e = (BindException) ex;
            message = e.getBindingResult().getFieldErrors().stream()
                    .findFirst()
                    .map(err -> err.getDefaultMessage())
                    .orElse(message);
        }
        return Result.error(ErrorCode.VALIDATE_FAILED.getCode(), message);
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception ex) {
        log.error("系统异常", ex);
        return Result.error(ErrorCode.SERVER_ERROR);
    }
}

