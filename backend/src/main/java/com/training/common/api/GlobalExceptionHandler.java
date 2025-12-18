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

    /**
     * 运行时异常：如果业务方直接抛出了带有明确提示的 RuntimeException（例如“工号已存在”），
     * 则优先将该提示返回给前端，便于前端直接展示。
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException ex) {
        String msg = ex.getMessage();
        log.error("运行时异常: {}", msg, ex);
        if (msg != null && msg.trim().length() > 0) {
            return Result.error(ErrorCode.SERVER_ERROR.getCode(), msg.trim());
        }
        return Result.error(ErrorCode.SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception ex) {
        log.error("系统异常", ex);
        return Result.error(ErrorCode.SERVER_ERROR);
    }
}

