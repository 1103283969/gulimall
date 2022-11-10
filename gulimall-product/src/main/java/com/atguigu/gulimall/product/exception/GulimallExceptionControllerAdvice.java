package com.atguigu.gulimall.product.exception;


import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


//集中处理所有异常
@Slf4j
//@ResponseBody
//@ControllerAdvice(basePackages = "com.atguigu.gulimall.product.controller")
@RestControllerAdvice(basePackages = "com.atguigu.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public R handleVailException(Exception e){
        log.error("数据校验出现问题{}，异常类型：{}",e.getMessage(),e.getClass());
        return R.error();
    }
}
