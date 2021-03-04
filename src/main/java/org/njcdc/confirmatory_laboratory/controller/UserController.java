package org.njcdc.confirmatory_laboratory.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.njcdc.confirmatory_laboratory.common.dto.LoginDto;
import org.njcdc.confirmatory_laboratory.common.dto.UserDto;
import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.njcdc.confirmatory_laboratory.entity.User;
import org.njcdc.confirmatory_laboratory.service.UserService;
import org.njcdc.confirmatory_laboratory.shiro.AccountProfile;
import org.njcdc.confirmatory_laboratory.util.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dingangui
 * @since 2021-03-01
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    // 创建账户
    @PostMapping("/save")
    public Result save(@Validated @RequestBody User user){

        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        userService.save(user);
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user,userDto);
        return Result.success("账户创建成功",userDto);

    }

    @GetMapping("/selectAll")
    public Result selectAll(){

        List<AccountProfile> accounts = new ArrayList<>();

        List<User> userList = userService.list();

        for (User user : userList) {
            AccountProfile account = new AccountProfile();
            BeanUtils.copyProperties(user, account);
            accounts.add(account);
        }
        return Result.success(accounts);
    }

    /*
        管理员给其他用户授权
       接收的内容：username + 四个权限
     */
    @PostMapping("/authorization")
    public Result authorization(@Validated @RequestBody UserDto userDto){


        // 将用户数据传输对象（dto）拷贝到用户数据访问对象(dao)
        User user = new User();
        BeanUtils.copyProperties(userDto,user);

        // 更新操作，判断更新是否成功
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("username",user.getUsername());

        Assert.isTrue(userService.update(user, updateWrapper),"授权失败");

/*        if(userService.update(user, updateWrapper))
            return Result.success(userDto);
        else
            return Result.fail("授权失败");*/

        return Result.success(userDto);
    }

}
