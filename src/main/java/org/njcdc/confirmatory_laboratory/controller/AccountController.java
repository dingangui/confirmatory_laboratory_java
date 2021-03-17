package org.njcdc.confirmatory_laboratory.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.njcdc.confirmatory_laboratory.common.dto.LoginDto;
import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.njcdc.confirmatory_laboratory.entity.User;
import org.njcdc.confirmatory_laboratory.service.UserService;
import org.njcdc.confirmatory_laboratory.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class AccountController {

    @Autowired
    UserService userService;


    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginDto loginDto, HttpServletResponse response){

        User user = userService.getOne(new QueryWrapper<User>().eq("username",loginDto.getUsername()));


        // 1. 密码不存在
        Assert.notNull(user,"用户不存在");

        // 2. 密码不正确
        if(!user.getPassword().equals(SecureUtil.md5(loginDto.getPassword())))
            return Result.fail("密码不正确");

        // 3. 密码正确
        String jwt = jwtUtils.generateToken(user.getId());

        response.setHeader("Authorization",jwt);
        response.setHeader("Access-control-Expose-Headers","Authorization");
        return Result.success("登录成功",  MapUtil.builder()
                .put("id",user.getId())
                .put("username",user.getUsername())
                .put("admin",user.getAdmin())
                .put("sampleInput",user.getSampleInput())
                .put("detectionDataInput",user.getDetectionDataInput())
                .put("dataVerify",user.getDataVerify())
                .put("formsOutput",user.getFormsOutput())
                .map());
    }

    @RequiresAuthentication
    @GetMapping("/logout")
    public Result logout(){
        System.out.println("logout");
        SecurityUtils.getSubject().logout();
        return Result.success("已退出", null);
    }

}
