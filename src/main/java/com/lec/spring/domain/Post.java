package com.lec.spring.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 다섯번쨰 파일 (수정)
// Model 객체 (domain)

/**
 * DTO 객체
 *  : Data Transfer Object 라고도 함.
 *
 *  객체 -> DB
 *  DB -> 객체
 *  request -> 객체
 *  객체 -> response
 *  ..
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    private Long id;
//    private String user;
    private String subject;
    private String content;
    private LocalDateTime regDate;
    private long viewCnt;

    private User user;  // 글작성자 (FK) 포린키가 뭐였지****

    // TODO: 첨부파일 정보

    // 웹개발시...
    // 가능한, 다음 3가지는 이름을 일치시켜주는게 좋습니다.
    // 클래스 필드명 = DB 필드명 = form의 name명

    // 첨부 파일 정보
    @ToString.Exclude  // 이건 뭐임? ****** 이해 안됨
    @Builder.Default  // 아래와 같이 초깃값 주어진 경우 bulider 제공안함
    private List<Attachment> fileList = new ArrayList<>();
}








