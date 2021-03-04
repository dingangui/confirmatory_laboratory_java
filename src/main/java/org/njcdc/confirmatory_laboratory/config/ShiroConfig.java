package org.njcdc.confirmatory_laboratory.config;

import org.njcdc.confirmatory_laboratory.shiro.AccountRealm;
import org.njcdc.confirmatory_laboratory.shiro.JwtFilter;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * shiro启用注解拦截控制器
 */



@SuppressWarnings("ALL")
@Configuration
public class ShiroConfig {

    @Autowired
    JwtFilter jwtFilter;

    // 引入RedisSessionDAO和RedisCacheManager，为了解决shiro的权限数据和会话信息能保存到redis中，实现会话共享。

    @Bean
    public SessionManager sessionManager(RedisSessionDAO redisSessionDAO) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();

        // inject redisSessionDAO
        sessionManager.setSessionDAO(redisSessionDAO);

        // other stuff...

        return sessionManager;
    }

    /**
     * SessionsSecurityManager 安全管理器，
     * 开发者自定义的 Realm 需要注入到 DefaultWebSecurityManager 进行管理才能生效
     *
     * @param realms
     * @param sessionManager
     * @param redisCacheManager
     * @return
     */
    @Bean
    public SessionsSecurityManager securityManager(
            AccountRealm realms
            , SessionManager sessionManager
            , RedisCacheManager redisCacheManager) {

        // 开发者自定义的 Realm 注入到 DefaultWebSecurityManager 进行管理
        SessionsSecurityManager securityManager = new DefaultWebSecurityManager(realms);

        //inject sessionManager
        securityManager.setSessionManager(sessionManager);

        // inject redisCacheManager
        securityManager.setCacheManager(redisCacheManager);

        // other stuff...

        return securityManager;
    }


    /* shiro过滤器链定义，定义哪些链接需要过滤 */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {

        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();

        Map<String, String> filterMap = new LinkedHashMap<>();

        // /** 表示所有连接都需要过滤

        filterMap.put("/**", "jwt"); // 主要通过注解方式校验权限

        chainDefinition.addPathDefinitions(filterMap);

        return chainDefinition;
    }

    /**
     * ShiroFilterFactoryBean  过滤器工厂
     * Shiro 的基本运行机制是开发者定制规则，Shiro 去执行
     * 具体的执行操作就是由 ShiroFilterFactoryBean 创建的一个个 Filter 对象来完成。
     *
     * @param securityManager
     * @param shiroFilterChainDefinition
     * @return
     */
    @Bean("shiroFilterFactoryBean")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager,
                                                         ShiroFilterChainDefinition shiroFilterChainDefinition) {
        // 实例化一个过滤器工厂bean
        // 添加安全管理器
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        // 添加过滤器
        Map<String, Filter> filters = new HashMap<>();
        filters.put("jwt", jwtFilter);
        shiroFilter.setFilters(filters);

        Map<String, String> filterMap = shiroFilterChainDefinition.getFilterChainMap();

        shiroFilter.setFilterChainDefinitionMap(filterMap);
        return shiroFilter;

    }



}
