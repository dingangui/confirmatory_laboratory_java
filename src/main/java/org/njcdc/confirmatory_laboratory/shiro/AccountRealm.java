package org.njcdc.confirmatory_laboratory.shiro;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.njcdc.confirmatory_laboratory.entity.User;
import org.njcdc.confirmatory_laboratory.service.UserService;
import org.njcdc.confirmatory_laboratory.util.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 开发者自定义的模块，根据项目的需求，验证和授权的逻辑全部写在 Realm 中
 */
@SuppressWarnings("ALL")
@Component
public class AccountRealm extends AuthorizingRealm{
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    // 告诉realm支持的token类型是AuthenticationToken，即jwt继承的token类型
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    /**
     * 授权
     * 获取用户权限，将权限信息封装成info返回给shiro
     *
     * @param principalCollection
     * @return
     */

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * 认证
     * 身份验证，获取token进行密码校验等，验证成功后返回基本信息
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        //之所以可以强转，是因为上面的support表明了支持这种类型的token
        JwtToken jwtToken = (JwtToken) token;

        /**
         *   getPrincipal就是返回token
         *   返回值是Object类型的数据，因此需要转换为String类型
         *
         *   getClaimByToken返回的是Claim类型的数据
         *   其中userId保存在SUBJECT当中，因此通过getSubject可以获取userId
         */

        // 获取用户ID
        String userId = jwtUtils.getClaimByToken((String)jwtToken.getPrincipal()).getSubject();

        // 根据ID获取user
        User account = userService.getOne(new QueryWrapper<User>().eq("id",Long.valueOf(userId)));

        if (account == null) {
            throw new UnknownAccountException("账户不存在");
        }
        if (account.getAdmin()) {
            throw new LockedAccountException("账户不是管理员");
        }

        System.out.println(jwtToken);

        /*
         * 确定账户登录可用后，将可以公开的账户信息返回给shiro
         * shiro通过subject工具获取用户信息
         */
        AccountProfile accountProfile = new AccountProfile();
        BeanUtils.copyProperties(account, accountProfile);

        return new SimpleAuthenticationInfo(accountProfile, jwtToken.getCredentials(), getName());
    }
}
