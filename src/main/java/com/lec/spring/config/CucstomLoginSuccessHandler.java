package com.lec.spring.config;
// 오늘 첫 페이지
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
//AuthenticationSuccessHandler(I)
// └─ SavedRequestAwareAuthenticationSuccessHandler
//    https://docs.spring.io/spring-security/site/docs/4.0.x/apidocs/org/springframework/security/web/authentication/SavedRequestAwareAuthenticationSuccessHandler.html

public class CucstomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {  // 내가 가고자 하는 url을 기억하고 있다가 인증을 성공하면 그 url 로 다이렉트 함
    public CucstomLoginSuccessHandler(String defaultTargetUrl) {
        // SavedRequestAwareAuthenticationSuccessHandler#setDefaultTargetUrl()
// 로그인후 특별히 redirect 할 url 이 없는경우 기본적으로 redirect 할 url
        setDefaultTargetUrl(defaultTargetUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        System.out.println("😍😍 로그인 성공 : onAuthenticationSuccess 함");
        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("""
                접속IP: %s
                username : %S
                password : %s
                authentication: %s
                """.formatted(getClientIp(request)
                , userDetails.getUsername()
        , userDetails.getPassword()
        , authentication.getAuthorities()));
        // 로그인 시간을 세션에 저장하기 (이게 돼? ) (Logout 예제에서 사용)
        LocalDateTime loginTime = LocalDateTime.now();
        System.out.println("로그인 시간 :" + loginTime);
        request.getSession().setAttribute("loginTime", loginTime);  // 이거 이해 안됨****


        // 로그인 직전url 을 redirect 시킴
        super.onAuthenticationSuccess(request, response, authentication);
    }
    // request 를 한 client ip 가져오기
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }





}
