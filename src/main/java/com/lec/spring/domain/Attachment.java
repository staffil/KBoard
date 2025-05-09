package com.lec.spring.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attachment {
    private Long id;
    private Long post_id; // 어느글의 첨부파일인가?
    private String sourcename; // 원본 파일명
    private String filename;  // 저장된 파일명 (rename 된 파일명)

    // 이미지 여부
    private boolean isImage; // isImage 가 이상해서 이해 안됨****  근데 html 에서는 image 로 가능 왜인지 들었는데 이해는 안감

}
