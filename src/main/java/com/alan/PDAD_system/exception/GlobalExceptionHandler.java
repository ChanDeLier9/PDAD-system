package com.alan.PDAD_system.exception;

import com.alan.PDAD_system.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.BindException;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理参数校验失败异常（如 @Valid 校验失败）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationException(MethodArgumentNotValidException e) {
        String errorMessage = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        logger.warn("参数校验失败: {}", errorMessage);
        return Result.error(errorMessage);
    }

    /**
     * 处理绑定异常（如数据类型转换失败）
     */
    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException e) {
        String errorMessage = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        logger.warn("参数绑定失败: {}", errorMessage);
        return Result.error("请求参数格式错误：" + errorMessage);
    }

    /**
     * 处理数据库约束异常（如违反唯一约束）
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        logger.error("数据库操作失败: {}", e.getMessage());
        return Result.error("操作失败，可能存在数据重复或其他约束冲突！");
    }

    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        // 记录错误日志
        logger.error("系统异常: ", e);

        // 返回通用错误信息
        return Result.error("操作失败，请联系管理员！");
    }
}
