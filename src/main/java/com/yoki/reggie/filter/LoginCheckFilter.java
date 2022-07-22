package com.yoki.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.yoki.reggie.common.BaseContext;
import com.yoki.reggie.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @program: reggie_take_out
 * @author: yoki
 * @create: 2022-07-11 21:15
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        log.info("当前线程：" + Thread.currentThread().getId());

        String requestURI = request.getRequestURI();
        log.info("拦截到请求：" + requestURI + ", 请求方式：" + request.getMethod());

        String[] excludeURLs = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        if (check(excludeURLs, requestURI)) {
            log.info("本次请求不需处理" + requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        Long empId = (Long) request.getSession().getAttribute("employee");
        if (empId != null) {
            log.info("员工用户已登录，用户id：" + empId);
            //使用ThreadLocal设置当前线程的局部变量id
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }

        Long userId = (Long) request.getSession().getAttribute("user");
        if (userId != null) {
            log.info("普通用户已登录，用户id：" + userId);
            //使用ThreadLocal设置当前线程的局部变量id
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
    }

    public boolean check(String[] urlPatterns, String requestURI) {
        for (String urlPattern : urlPatterns) {
            boolean match = PATH_MATCHER.match(urlPattern, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
