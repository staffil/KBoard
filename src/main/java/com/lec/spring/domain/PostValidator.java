package com.lec.spring.domain;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class PostValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        System.out.println("🐷supports(" + clazz.getName() + ") 호출");

        // ↓ 검증할 객체의 클래스 타입인지 확인 : Post = clazz; 가능 여부
        boolean result = Post.class.isAssignableFrom(clazz);
        System.out.println(result);
        return result;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Post post = (Post) target;

        System.out.println("🐬validate() 호출 : " + post);

        // 이미 글 작성하고 있는 사람은 권한이 있기 때문에 굳이 필요 없음
//        String user = post.getUser();
//        if(user == null || user.trim().isEmpty()){
//            errors.rejectValue("user", "작성자는 필수입니다");
//        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject", "글 제목은 필수입니다");
    }
}















