package com.atguigu.gulimall.auth.controller;


import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.ThirdPartFeignService;
import com.atguigu.gulimall.auth.vo.UserRegisterVo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
         String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
         long l = Long.parseLong(redisCode.split("_")[1]);
         if(System.currentTimeMillis()-l<60000){
             //60秒内不能再发
             return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(),BizCodeEnum.SMS_CODE_EXCEPTION.getMessage());

         }



        //2.验证码再次校验，存在redis
        String code = UUID.randomUUID().toString().substring(0,5)+"_"+System.currentTimeMillis();
        //redis缓存验证码
        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX+phone,code,10, TimeUnit.MINUTES);



        thirdPartFeignService.sendCode(phone,code);
        return R.ok();
    }


    @PostMapping(value = "/register")
    public String register(@Valid UserRegisterVo vos, BindingResult result,
                           RedirectAttributes attributes) {

        //如果有错误回到注册页面
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            attributes.addFlashAttribute("errors",errors);

            //效验出错回到注册页面
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        //1、效验验证码
        String code = vos.getCode();

        //获取存入Redis里的验证码
        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vos.getPhone());
        if (!StringUtils.isEmpty(redisCode)) {
            //截取字符串
            if (code.equals(redisCode.split("_")[0])) {
                //删除验证码;令牌机制
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX+vos.getPhone());
                //验证码通过，真正注册，调用远程服务进行注册
                R register = memberFeignService.register(vos);
                if (register.getCode() == 0) {
                    //成功
                    return "redirect:http://auth.gulimall.com/login.html";
                } else {
                    //失败
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", register.getData("msg",new TypeReference<String>(){}));
                    attributes.addFlashAttribute("errors",errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }


            } else {
                //效验出错回到注册页面
                Map<String, String> errors = new HashMap<>();
                errors.put("code","验证码错误");
                attributes.addFlashAttribute("errors",errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        } else {
            //效验出错回到注册页面
            Map<String, String> errors = new HashMap<>();
            errors.put("code","验证码错误");
            attributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }
    }



}
