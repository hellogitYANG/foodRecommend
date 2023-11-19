package com.example.foodrecommend.interceptor;

import com.example.foodrecommend.utils.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class CheckTokenInterceptor implements HandlerInterceptor {
    //ThreadLocal存取token,拦截的接口都能使用这个常量
    private static ThreadLocal<String> tokenThreadLocal = new ThreadLocal<>();

    public static String getToken() {
        return tokenThreadLocal.get();
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        if("OPTIONS".equalsIgnoreCase(method)){
            return true;
        }
        String token = request.getHeader("token");
        if(token == null){
            R r = new R(1001, "请先登录！", null);
            doResponse(response,r);
        }else{
            try {
                JwtParser parser = Jwts.parser();
                parser.setSigningKey("tuijian666"); //解析token的SigningKey必须和生成token时设置密码一致
                //如果token正确（密码正确，有效期内）则正常执行，否则抛出异常
                Jws<Claims> claimsJws = parser.parseClaimsJws(token);

                // 在拦截器中将token存入ThreadLocal
                tokenThreadLocal.set(token);

                return true;
            }catch (ExpiredJwtException e){
                R r = new R(1002, "登录过期，请重新登录！", null);
                doResponse(response,r);
            }catch (UnsupportedJwtException e){
                R r = new R(1001, "Token不合法，请自重！", null);
                doResponse(response,r);
            }catch (Exception e){
                R r = new R(1001, "请先登录！", null);
                doResponse(response,r);
            }
        }
        return false;
    }

    private void doResponse(HttpServletResponse response,R r) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        String s = new ObjectMapper().writeValueAsString(r);
        out.print(s);
        out.flush();
        out.close();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除ThreadLocal中的token，确保不会发生内存泄漏
        tokenThreadLocal.remove();
    }

}
