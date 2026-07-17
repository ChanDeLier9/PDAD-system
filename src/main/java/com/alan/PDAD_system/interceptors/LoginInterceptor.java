package com.alan.PDAD_system.interceptors;

import com.alan.PDAD_system.utils.JwtUtil;
import com.alan.PDAD_system.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/*@component 把普通pojo实例化到spring容器中，泛指各种组件，
就是当定义的类不属于各种归类的时候（不属于@Controller、
@Services等的时候），我们就可以使用@Component来标注这个类。
*/
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的 JWT
        String token = request.getHeader("Authorization");

        // 如果没有携带 token，直接返回 401
        if (token == null || token.isEmpty()) {
            response.setStatus(401);
            response.getWriter().write("未授权，请重新登录");
            return false;
        }

        try {

            // 去掉 "Bearer " 前缀
            token = token.replace("Bearer ", "");

            // 验证并解析 JWT（JwtUtil.parseToken 是你用来解析 token 的工具类）
            Map<String, Object> claims = JwtUtil.parseToken(token);
            // 将用户信息存储到 ThreadLocal 中，供后续业务逻辑使用
            ThreadLocalUtil.set(claims);

            return true;  // 放行请求
        } catch (Exception e) {
            // 如果 token 验证失败，返回 401
            response.setStatus(401);
            response.getWriter().write("令牌无效或已过期，请重新登录");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理 ThreadLocal 中的数据
        ThreadLocalUtil.remove();
    }
}
