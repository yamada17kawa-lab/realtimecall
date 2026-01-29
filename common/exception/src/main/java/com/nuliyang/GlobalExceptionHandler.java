package com.nuliyang;

import com.nuliyang.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BizException.class)
    public Result<?> handleBizException(BizException e) {
        log.warn("业务异常code：{}", e.getCode());
        log.warn("业务异常msg：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();
        return Result.error(ErrorCode.PARAM_ERROR.getCode(), msg);
    }

    /**
     * 参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String msg = e.getAllErrors()
                .get(0)
                .getDefaultMessage();
        return Result.error(ErrorCode.PARAM_ERROR.getCode(), msg);
    }

    /**
     * 3️⃣ Seata / 事务 / 代理异常兜底
     */
    @ExceptionHandler(Throwable.class)
    public Result<?> handleThrowable(Throwable e) {

        Throwable root = unwrap(e);

        // Seata 相关异常
        if (root.getClass().getName().contains("seata")) {
            log.error("Seata事务异常", root);
            return Result.error(500, "系统繁忙，请稍后重试");
        }

        // 数据库唯一键等
        if (root instanceof DuplicateKeyException) {
            return Result.error(10007, "数据已存在");
        }

        log.error("系统异常", root);
        return Result.error(500, "系统异常");
    }

    /**
     * 解包代理异常（非常关键）
     */
    private Throwable unwrap(Throwable e) {
        Throwable cause = e;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }
}
