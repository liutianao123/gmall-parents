package com.atguigu.gmall.admin.config;

import com.atguigu.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 处理系统所有的抛出的异常
 */
@Slf4j
//@ResponseBody
//@ControllerAdvice //这是一个统一异常处理类
@RestControllerAdvice
public class GmallGlobalExceptionHandler {
    @ExceptionHandler(ArithmeticException.class)
    public Object number(Exception e) {
        log.error("数字异常");
        return new CommonResult().failed().validateFailed(e.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public Object exception(Exception e){
        return new CommonResult().failed().validateFailed("cw");
    }
}
