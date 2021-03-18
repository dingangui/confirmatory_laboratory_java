package org.njcdc.confirmatory_laboratory.shiro;


import cn.hutool.json.JSONUtil;
import org.njcdc.confirmatory_laboratory.common.lang.Result;
import org.njcdc.confirmatory_laboratory.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends AuthenticatingFilter {

    @Autowired
    JwtUtils jwtUtils;

    // shiro 默认是 username + password 的 token，由下面的方法创建
    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        // 登录完成后的jwt放在header中的Authorization字段，所以jwt通过下面的方法获得
        String jwt = httpServletRequest.getHeader("Authorization");

        if (StringUtils.isEmpty(jwt)) {
            return null;
        }

        return new JwtToken(jwt);
    }

    // 拦截
    // 判断用户获取jwt是否过期、凭证是否正确的校验
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String jwt = httpServletRequest.getHeader("Authorization");

        // 如果jwt为空，直接返回null
        // 否则，校验jwt
        if (StringUtils.isEmpty(jwt)) {
            return true;
        } else {

            // 校验jwt

            Claims claims = jwtUtils.getClaimByToken(jwt);

            // claims == null 校验异常
            // jwtUtils.isTokenExpired(claims.getExpiration()) token过期

            if (claims == null || jwtUtils.isTokenExpired(claims.getExpiration())) {
                throw new ExpiredCredentialsException("token已失效，请重新登录");
            }

/*          执行登录
            登录可能成功，也可能会失败
            成功：onLoginSuccess(token, subject, request, response);
            失败：onLoginFailure(token, e, request, response);

             */

            return executeLogin(request, response);

        }

    }

    /* 登录异常的处理逻辑，重写方法 */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {


        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        // e.getCause为空，直接返回，否则返回内容
        Throwable throwable = e.getCause() == null ? e : e.getCause();

        // 统一封装错误原因
        Result result = Result.fail(throwable.getMessage());

        // 错误原因封装成json数据
        String json = JSONUtil.toJsonStr(result);

        // 通过response工具将json数据返回
        try {
            httpServletResponse.getWriter().print(json);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return false;
    }

    /**
     * 跨域处理，没有JwtFilter，跨域直接作用于Controller之上
     * 现在引入JwtFilter后，过滤器在Controller前会拦截，因为对Filter也需要进行跨域处理
     *
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个OPTIONS请求，这里我们给OPTIONS请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(org.springframework.http.HttpStatus.OK.value());
            return false;
        }

        return super.preHandle(request, response);
    }

}
