package com.lec.spring.config;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.User;
import com.lec.spring.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

//시큐리티가 /user/login (POST) 주소요청이 오면 낚아채서 로그인을 진행시킨다.
//로그인(인증) 진행이 완료되면 '시큐리티 session' 에 넣어주게 된다.
//우리가 익히 알고 있는 같은 session 공간이긴 한데..
//시큐리티가 자신이 사용하기 위한 공간을 가집니다.
//=> Security ContextHolder 라는 키값에다가 session 정보를 저장합니다.
//여기에 들어갈수 있는 객체는 Authentication 객체이어야 한다.
//Authentication 안에 User 정보가 있어야 됨.
//User 정보 객체는 ==> UserDetails 타입 객체이어야 한다.

//따라서 로그인한 User 정보를 꺼내려면
//Security Session 에서
//   => Authentication 객체를 꺼내고, 그 안에서
//        => UserDetails 정보를 꺼내면 된다.

public class PrincipalDetails implements UserDetails , OAuth2User {  // userDetails 는 스프링 secrity 가 사용자 인증 정보를 다룰떄 필요한 인터페이스

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // 로그인한 사용자 정보
    private User user;
    public User getUser() {
        return user;
    }

    public PrincipalDetails(User user){ // 일반 로그인용 새엇ㅇ자
        System.out.println("🐬UserDetails(user) 생성: " + user);
        this.user = user;
    }

    // OAuth2 로그인용 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes){
        System.out.println("""
                userOauth2 생성
                user : %s
                attributes : %s
                """.formatted(user, attributes));
        this.user = user;
        this.attributes = attributes;  // attribute 는 여기서 생성됨 (근데 이걸 어떻게 하냐...)
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {  //사용자의 권한을 확인해 로그인 후 어떤 작업을 할 수 있는지 체크하함
        System.out.println("🧶getAuthorities() 호출"); // 수시로 호출 함

        Collection<GrantedAuthority> collect = new ArrayList<>();

        List<Authority> list = userService.selectAuthoritiesById(user.getId()); // DB 에서 user 의 권한(들) 읽어오기
        for (Authority auth: list){
//            collect.add(() -> auth.getName()); // GrantedAuthority : 사용자의 권한을 확인하는데 사용됨
            collect.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {  // 매개변수 없고 String 리턴
                    return auth.getName();
                }

                // 타임리프에서 활용할 문자열.   데이타 베이스에서가 아니니 Authentication 에서 오 녀석을 불러옴
                @Override
                public String toString() {
                    return auth.getName();
                }
            });
        }

        return collect;
        // granteAuthority 는 이 관리자가 권한을 가진 자인지 확인하는 거고 getAuthoruty 는 그것이 관리자읹지 확인하기 위해 것
    }

    //_______________________________________

    // 활성화 되었는지 (일정 기간 지나면 휴먼 계정 되는거)
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 계정, 아이디 를 기억해 놓는것(credationdㅁl 이 만료된건 아닌가?)
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 계정이 잠긴건 아닌지? (계정이 lock 이 걸려 있는지)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 기간 만료된 계정인지?
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // OAuth2 를 implement 하게 되면 구현할 메소드
    private Map<String, Object> attributes; // < - OAuth2User 의 getAttributes() 의 값

    // OAuth2User 을 implement
    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
    @Override
    public String getName() {
        return null;  // 이번에는 사용안함
    }
}
