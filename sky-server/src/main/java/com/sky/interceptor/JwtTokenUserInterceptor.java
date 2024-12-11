package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //Determine whether the currently intercepted method is the Controller method or other resources
        if (!(handler instanceof HandlerMethod)) {
            //The method currently intercepted is not a dynamic method, so it is released directly.
            return true;
        }

        //1、Get the token from the request header
        String token = request.getHeader(jwtProperties.getUserTokenName());

        //2、Verify Token
        try {
            log.info("jwt verification:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("Current user id：", userId);
            BaseContext.setCurrentId(userId);
            //3、Pass, release
            return true;
        } catch (Exception ex) {
            //4、If not passed, the response will be 401 status code
            response.setStatus(401);
            return false;
        }
    }
}
