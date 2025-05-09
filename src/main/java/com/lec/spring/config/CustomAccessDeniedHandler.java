package com.lec.spring.config;
// 네번째 클래스.....
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    // 권한이 없는 url 접근 할떄 호출
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        System.out.println("😒😒😒😒들어오지 마셈요 : CustomAccessDeniedHandler():" + request.getRequestURI() + " 👌 ");  // 어떤 접근 권한에 접근했는지
        response.sendRedirect(request.getContextPath() + "/user/rejectAuth");

    }
}
