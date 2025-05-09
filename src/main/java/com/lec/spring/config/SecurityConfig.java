package com.lec.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity  // Security 설정에 사용.
// 이 클래스는 보안 설정을 하겠다 선언
public class SecurityConfig {


    // PasswordEncoder 를 bean 으로 IoC 에 등록  => 관리의 책임이 개발자에서 스프링으로 넘어간다는 개념
    // IoC 에 등록된다, IoC 내에선 '어디서든' 가져다가 사용할수 있다.

    //--------------------------------------------------------
// OAuth 로그인
// AuthenciationManager bean 생성

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        System.out.println("Authentication 실행");
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Autowired
    private com.lec.spring.config.oauth.principalOauth2UserService principalOauth2UserService;




    // Security 를 동작시키지 않도록
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return web -> web.ignoring().anyRequest();  // 어떠한 request 도 security 가 무시한.
//    }
    // securityfilterchain 을 bean 으로 등록
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { // 보안 필터들 줄줄이 세워놓기

        // ※ 만약 기본동작으로 진행케 하려면?
//        return http
//                .csrf(Customizer.withDefaults())
//                .formLogin(Customizer.withDefaults())
//                .httpBasic(Customizer.withDefaults())
//                .build();

        return http
                .csrf(csrf->csrf.disable())  // CSRF 비활성화 (이게 보안이라고?***)

                /**********************************************
                 * ① request URL 에 대한 접근 권한 세팅  : authorizeHttpRequests()
                 * .authorizeHttpRequests( AuthorizationManagerRequestMatcherRegistry)
                 **********************************************/
                .authorizeHttpRequests(auth -> auth
                        // URL 과 접근권한 세팅(들)
                        // ↓ /board/detail/** URL로 들어오는 요청은 '인증'만 필요.
                        .requestMatchers("/board/detail/**").authenticated()  // 403 에러, 즉 회원가입 없이는 접근 안됨 (requestMatchers 는 어떤 과정으로 처리해야 이 문을 들어오게 주겠다는 의미
                        // ↓ "/board/write/**", "/board/update/**", "/board/delete/**" URL로 들어오는 요청은 '인증' 뿐 아니라 ROLE_MEMBER 나 ROLE_ADMIN 권한을 갖고 있어야 한다. ('인가')
                        .requestMatchers("/board/write/**", "/board/delete/**", "/board/update/**").hasAnyRole("MEMBER", "ADMIN") // 관리자만 사용할 수 있는 권한
                        // 그 외 다른 요청들은 모두 허용 => 즉 로그인을 하지 않아도 모든 창에 들어갈 수 있는 권한을 줌
                        .anyRequest().permitAll()
                )
                /********************************************
                 * ② 폼 로그인 설정
                 * .formLogin(HttpSecurityFormLoginConfigurer)
                 *  form 기반 인증 페이지 활성화.
                 *  만약 .loginPage(url) 가 세팅되어 있지 않으면 '디폴트 로그인' form 페이지가 활성화 된다
                 ********************************************/

                .formLogin(form -> form
                        .loginPage("/user/login") // 로그인 필요한 상황 발생시 매개변수의 url (로그인 폼) 으로 request 발생
                        .loginProcessingUrl("/user/login") // "/user/login" url 로 POST request 가 들어오면 시큐리티가 낚아채서 처리, 대신 로그인을 진행해준다(인증).
                        // 이와 같이 하면 Controller 에서 /user/login (POST) 를 굳이 만들지 않아도 된다!
                        // 위 요청이 오면 자동으로 UserDetailsService 타입 빈객체의  () 가 실행되어 인증여부 확인진행 <- 이를 제공해주어야 한다. *** 이해 안됨
                        // 쉽게 설명해서 내가 원래 가고자 했던 곳이 로그인을 해야 되는 곳이라면 로그인을 한뒤 바로 내가 선택한 창으로 간다는 뜻

                        .defaultSuccessUrl("/") // home 창으로 다로 리다이렉트 함
                        // '직접 /login' → /login(post) 에서 성공하면 "/" 로 이동시키기
                        // 만약 다른 특정페이지에 진입하려다 로그인 하여 성공하면 해당 페이지로 이동 (너무 편리!)

                        // 로그인 성공직후 수행할코드
                        //.successHandler(AuthenticationSuccessHandler)  // 로그인 성공후 수행할 코드.
                        .successHandler(new CucstomLoginSuccessHandler("/home"))
                        // 로그인 실패하면 수행할 코드
                        // .failureHandler(AuthenticationFailureHandler)
                        .failureHandler(new CustomLoginFaailureHandler())

                        //.usernameParameter()   기본 name="username" 이어햐 함.
                        //.passwordParameter()   기본 name="password" 이어야 함  (그렇지 않으면 기본값)



                )
                /********************************************
                * ③ 로그아웃 설정
                * .logout(LogoutConfigurer)
                ********************************************/
                // ※ 아래 설정 없이도 기본적으로 /logout 으로 로그아웃 된다

                .logout(httpSecurity -> httpSecurity
                        .logoutUrl("/user/logout")
                        .invalidateHttpSession(false)  // 시간 재야 하니깐 로그아웃 해도 지우지 말아라
//                        .logoutSuccessUrl("/user/login?logout")) // 로그아웃 성공후 redirect
                        // 이따가 CustomLogoutSuccessHandler 에서 꺼낼 정보가 있기 때문에
                        // false 로 세팅한다
//                                .deleteCookies("JSESSIONID") // 쿠키 세션 제거
                                .logoutSuccessHandler(new CustomLogoutSuccessHandler())

                )

                /********************************************
                 * ④ 예외처리 설정
                 * .exceptionHandling(ExceptionHandlingConfigure)
                 ********************************************/

                .exceptionHandling(httpSecurity -> httpSecurity
                        // 권한(Authorization) 오류 발생시 수행할 코드
                        // .accessDeniedHandler(AccessDeniedHandler)
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                /********************************************
                 * OAuth2 로그인
                 * .oauth2Login(OAuth2LoginConfigurer)
                 ********************************************/
                .oauth2Login(httpSecurity-> httpSecurity
                        .loginPage("/user/login")  // 로그인 페이지를 동일한 url로 지정
                        // code 를 받아오는것이 아니라, 'AccessToken' 과 사용자 '프로필 정보' 를 한번에 받아온다. (편리함)
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                // 인증 서버의 UserInfo EndPoint (후처리) 설정  (userService 에 제공을 해 줘야함
                                // 후처리: 회원가입 + 로그인 진행.
                                .userService(principalOauth2UserService))  // oauth2UserService<OAuth2UserRequest , OAuth2User>

                )







                .build();

    }

}



