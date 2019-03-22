package com.atguigu.gmall.admin.config;


import com.atguigu.gmall.to.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Slf4j
@Aspect //说明这是一个切面
@Component
public class GmallValidatorAspect {

    @Around("execution(* com.atguigu.gmall..controller..*.* (..))")
    public Object aroud(ProceedingJoinPoint joinPoint) throws Throwable {
        //前置通知
        log.info("11111111111111111");
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if(arg instanceof BindingResult){
                int errorCount = ((BindingResult) arg).getErrorCount();
                if(errorCount>0){
                    CommonResult commonResult = new CommonResult().validateFailed((BindingResult) arg);
                    return commonResult;
                }
            }
        }
        Object proceed = joinPoint.proceed();//放行
        //后置通知
        return proceed;
    }

}
