package com.lec.spring.controller;


import com.lec.spring.domain.Attachment;
import com.lec.spring.service.AttachmentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController // 컨트롤러, 바디 합친거 (파일 다운로드, 데이터를 reponse 하기 위해 사용)
public class AttachmentController {
    @Value("${app.upload.path}")  // 이게 뭐였더라
    private String uploadDir;
    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    // 파일 다운로드
// id: 첨부파일의 id
// ResponseEntity<T> 를 사용하여
// '직접' Response data 를 구성
    @RequestMapping("/board/download")
    public ResponseEntity<Object> download(Long id) {
            if (id == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }// 400 아이디가 없는 상태라면 400 에러를 내겠다? 맞나?
            Attachment file = attachmentService.findById(id);
            if (file == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);  // 404 에러 파일이 없는 상태라면 404 에러는 내겠다

            }
            String sourceName = file.getFilename();
            String fileName = file.getFilename();

            // 저장된 파일의 절대 경로
            String path = new File(uploadDir, fileName).getAbsolutePath();

            try{
                // 파일 유형(MINE 타입을 추출) **** 이 부분 전체적으로 이해 안됨
                String mimeType = Files.probeContentType(Paths.get(path));
                // 유형이 지정되지 않은 경우 처리 => 해당 파일이 알려진 유형이 아니면 문제
                // 일련의 8bit 스트림 타입.  유형이 알려지지 않은 파일에 대한 형식 지정
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                // resonse body  준비
                Path filePath = Paths.get(path);

                // Resource <- inputStream <- 저장된 파일
                Resource resource = new InputStreamResource(Files.newInputStream(filePath));

                // response header 준비
                HttpHeaders headers = new HttpHeaders();
                // ↓ 원본 파일 이름(sourceName) 으로 다운로드 하게 하기위한 세팅
                //   반.드.시 URL 인코딩해야 함

                // ex) Content-Disposition: attachment; filename="filename.jpg"
                //  브라우저에서는 어떤 관계와 상괸없이 filename.jpg로 다운 but 한글로는 다운이 안됨
                headers.setContentDisposition(ContentDisposition.builder("attachment").filename(URLEncoder.encode(sourceName, "utf-8")).build());
                // 뭔데 길어.. 이해 안됨******
                headers.setCacheControl("no-cache");  // 캐시 쓰지 마셈
                headers.setContentType(MediaType.parseMediaType(mimeType)); // 파일 유형을 지원



                // response Entity<> 리턴 (body, header, status)
                return new ResponseEntity<>(resource, headers, HttpStatus.OK); // 정상적으로 response (200)
            }catch (IOException ex){
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);  // 500 에러
            }
    }
}
