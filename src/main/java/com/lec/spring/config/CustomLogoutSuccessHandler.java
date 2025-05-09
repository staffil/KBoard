package com.lec.spring.config;
// 오늘 세번째 클래스
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    // 로그아웃 진행후 호출되는 메소드
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("👌👌👌👌 로그아웃 됨.: onLogoutSuccess() 호출");

        // 로그아웃 시간 남기기
        LocalDateTime logoutTime = LocalDateTime.now();
        System.out.println("로그아웃 시간 :" + logoutTime);

        // 사용시간(로그인 ~ 로그아웃 ) 게산하기
        LocalDateTime loginTime = (LocalDateTime) request.getSession().getAttribute("loginTime");
        // 이미 클라이언트 로그아웃을 하면 세션에 있던게 사라짐, 결국 null 값이 호출 됨
        if (loginTime != null) {
            Long second = loginTime.until(logoutTime, ChronoUnit.SECONDS);
            System.out.println("사용시간: " + second);
        }
        // 그리고 세션 invalidate  **** 이거 이해 못함
        request.getSession().invalidate();

        String redirectUrl = "/user/login?logoutHandler";

        // url parameter 에 ret_url 이 있는 경우 logout 하고 해당 url 로 redirect **** 이것도 이해 못함
        if (request.getParameter("ret_url") != null) {
            redirectUrl = request.getParameter("ret_url");
        }

        // redirect
        response.sendRedirect(redirectUrl);
    }
}
