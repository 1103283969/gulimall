package com.atguigu.gulimall.auth.controller;


import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.ThirdPartFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Controller
public class LoginController {

    @Autowired
    ThirdPartFeignService thirdPartFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;




    @GetMapping("/login.html")
    public String loginPage(){
        return "login";
    }
    @GetMapping("/reg.html")
    public String regPage(){
        return "reg";
    }

    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone){

        //1.接口防刷

        //2.验证码再次校验，存在redis
        String code = UUID.randomUUID().toString().substring(0,5);



        thirdPartFeignService.sendCode(phone,code);
        return R.ok();
    }


}
