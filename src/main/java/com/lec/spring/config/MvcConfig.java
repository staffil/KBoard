package com.lec.spring.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//  /upload/** URL 로 request 가 들어오면
// upload/ 경로의 static resource 가 동작케 함.
// IntelliJ 의 경우 이 경로를 module 이 아닌 project 이하에 생성해야 한다.

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Bean
    public PasswordEncoder encoder(){
        System.out.println("PasswordEncoder bean 생성");
        return new BCryptPasswordEncoder(); // => 비밀번호를 우리가 아는 방식에서 보안을 철저히 하기 위해 여러가지 섞어서 만듦
    }

    @Value("${app.upload.path}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        System.out.println("😘😘😘😘 MvcConfig.addResourceHandlers 호출 되었음");
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
