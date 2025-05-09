package com.lec.spring.service;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.User;

import java.util.List;

public interface UserService {

    // username(회원 아이디) 의 User 정보 읽어오기 (사용자에 대한 정보를 가져올때 )
    User findByUsername(String username);

    // 특정 username(회원 아이디) 의 회원이 존재하는지 확인  (회원 가입 할때 이미 존재하는 아이디인지 종복 확인할 떄 )
    boolean isExist(String username);

    // 신규 회원 등록
    int register(User user);

    // 특정 사용자(id)의 authority(들)
    List<Authority> selectAuthoritiesById(Long id);

}












