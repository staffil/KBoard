package com.lec.spring.config.oauth;

import com.lec.spring.config.PrincipalDetails;
import com.lec.spring.config.oauth.provider.GoogleUserInfo;
import com.lec.spring.config.oauth.provider.NaverUserInfo;
import com.lec.spring.config.oauth.provider.OAuth2UserInfo;
import com.lec.spring.domain.User;
import com.lec.spring.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * OAuth2UserService<OAuth2UserRequest, OAuth2User>(I)
 *  └─ DefaultOAuth2UserService
 */
@Service
public class principalOauth2UserService extends DefaultOAuth2UserService {
    @Value("${app.oauth2.password}")
    private String oauth2Password;
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public principalOauth2UserService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // 여기서 인증후 '후처리' 를 해주어야 한다
    // 인증 직후 loadUser()는 구글로 부터 받은 userRequest 데이터에 대한 후처리 진행.
    // OAuth2User 를 리턴하면 이를 가지고 인증진행해야 한다. (UserDetail 이 아닌 서로 다른것 )  => 구글로 부터 받아온 정보
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("호출 되었음");
        OAuth2User oAuth2User = super.loadUser(userRequest);  // 사용자 프로필 정보 가져오기

        // 확인 (oauth reqeust 에 어떤 정보가 담겨 있는 것인가?
        System.out.println("""  
                🎄[loadUser() 호출]
                     ClientRegistration: %s
                     RegistrationId: %s
                     AccessToken: %s
                     OAuth2User Attributes : %s
                """.formatted(userRequest.getClientRegistration()  // ClientRegistration
                , userRequest.getClientRegistration().getRegistrationId()  // String
                , userRequest.getAccessToken().getTokenValue()  // String
                , oAuth2User.getAttributes()    // Map<String, Object>  <- 사용자 프로필 정보다 담겨온다.
        ));

        // 후처리: 회원가입 진행
        String provider = userRequest.getClientRegistration().getRegistrationId();  // google
        OAuth2UserInfo oAuth2UserInfo = switch (provider.toLowerCase()){
            case "google" -> new GoogleUserInfo(oAuth2User.getAttributes());
            case "naver" -> new NaverUserInfo(oAuth2User.getAttributes());
            // case "facebook"
            default -> null;
        };
        String providerId = oAuth2UserInfo.getProviderId();
// username은 중복되지 않도록 만들어야 한다
        String username = provider + "_" + providerId; // "ex) google_xxxxxxxx"
        String password = oauth2Password;
        String email = oAuth2UserInfo.getEmail();
        String name = oAuth2UserInfo.getName();
        // 회원 가입 진행하기 전에
        // 이미 가입한 회원인지, 혹은 비가입자인지 체크하여야 한다

        User newUser = User.builder()
                .username(username)
                .name(name)
                .email(email)
                .password(password)
                .provider(provider)
                .providerId(providerId)
                .build();
        User user = userService.findByUsername(username);
        if (user == null){ // 가입하지 않은 사람만 회원 가입
            user = newUser;
            int cnt = userService.register(user);
            if (cnt >0){
                System.out.println("가입 성공");
                user = userService.findByUsername(username); // 다시 db 에서 읽어와야 한다. regdate 등의 정보
            }else{
                System.out.println("가입 실패");
            }
        }else{
            System.out.println("이미 가입 했음");
        }
        // 이 리턴값이 authentication의 principal로 들어가는 것이다
        PrincipalDetails principalDetails = new PrincipalDetails(user, oAuth2User.getAttributes()); // 이 부분은 이해 안감
        principalDetails.setUserService(userService);
        return principalDetails;  // oauth 를 principal 안에 넣어서 userdetail, oauth 따로 만들지 않도록 하기 위해서 이렇게 만든 거구만.
//        return oAuth2User;
    }
}

