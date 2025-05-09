package com.lec.spring.util;


import ch.qos.logback.core.util.StringUtil;
import com.lec.spring.config.PrincipalDetails;
import com.lec.spring.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

// 네번쨰 클래스
public class U {
    // 현재 request 구하기
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attrs.getRequest();
    }


    // 현재 session 구하기
    public static HttpSession getSession() {
        return getRequest().getSession();
    }


    // 현재 로그인 한 사용자 User구하기
    public static User getLoggedUser(){
        // 현재 로그인 한 사용자
        PrincipalDetails userDetails = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();  // 이해 안갔음*****
        User user = userDetails.getUser();
        return user;
    }

    public static void printFileInfo(MultipartFile file) {
        String originalFilename = file.getOriginalFilename(); // 원본 이름
        if (originalFilename == null || originalFilename.isEmpty()){
            System.out.println("\n 파일이 없음");
            return;
        }
        System.out.println("""
                OriginalFilename: %s
                cleanPath: %s
                fileSize : %s
                mine :%s
                """.formatted(originalFilename, StringUtils.cleanPath(originalFilename), file.getSize() + "bytes", file.getContentType()  ));
        //   ※  cleanPath 는  C:\Users\aaa\bbbb/dsaf/asdfsafd.ddd
//                   "\" -> "/" 로 변경
        // content type (mine type)


        // 이미지 파일인 경우 (try and exception)
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage != null) {
                System.out.printf("이미지 파일 입니다: %d x %d\n" , bufferedImage.getWidth(), bufferedImage.getHeight());
            } // 공공기관에 특정 크기의 이미지 파일 제출하라는거 있잖아. 여기서 필터링 함
            else{
                System.out.println("이미지 아님😒");
            }
            ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


// 첨부파일 정보(MultipartFile) 출력하기
// TODO



}
