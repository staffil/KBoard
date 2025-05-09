package com.lec.spring.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long id;

    private String username;
    @JsonIgnore  // json 변환 무시
    private String password;

    @ToString.Exclude
    @JsonIgnore
    private String re_password;  // 비밀번호 확인 입력.

    private String name;
    private String email;

    @JsonIgnore  // 이 필드는 생략 ****
    private LocalDateTime regDate;


    //OAuth client
    private String provider;
    private String providerId;

}
