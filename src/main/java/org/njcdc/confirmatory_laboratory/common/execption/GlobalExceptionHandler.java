package org.njcdc.confirmatory_laboratory.common.execption;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */

@SuppressWarnings("ALL")
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     *     捕获全局异常
     *     运行时发生的异常会继承于RuntimeException
     */

    /**
     * 捕获shiro相关异常
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = ShiroException.class)
    public Result handler(ShiroException e) {
        log.error("Shiro异常：-------------------------{}", e);
        return Result.fail(401, e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handler(MethodArgumentNotValidException e) {
        log.error("实体校验异常：-------------------------{}", e);

        // 默认的e.getMessage会显示所有的错误，下面的操作是简化显示的内容，只显示一个错误
        BindingResult bindingResult = e.getBindingResult();
        ObjectError error = bindingResult.getAllErrors().stream().findFirst().get();
        return Result.fail(error.getDefaultMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result handler(IllegalArgumentException e) {
        log.error("Assert异常：-------------------------{}", e);
        return Result.fail(e.getMessage());
    }

    // @ResponseStatus，返回状态码给前端
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // @ExceptionHandler，表明捕获的是运行时异常
    @ExceptionHandler(value = RuntimeException.class)
    public Result handler(RuntimeException e) {
        log.error("运行时异常：-------------------------{}", e);

        return Result.fail(e.getMessage());
    }


}
