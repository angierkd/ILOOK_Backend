package com.example.illook.security;

import com.example.illook.mapper.UserMapper;
import com.example.illook.security.OAuth2.OAuth2Attribute;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService, OAuth2UserService<OAuth2UserRequest, OAuth2User> {


    private final UserMapper userProfileMapper;


    //사용자를 찾아서 userDetails로
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (userProfileMapper.findById(username) == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        return userProfileMapper.findById(username);
    }



    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //DefaultOAuth2UserService 객체를 성공정보를 바탕으로 만든다
        OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();

        //생성된 Service 객체ㅔ로 부터 User를 받는다
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);

        //받은 User로부터 user 정보를 받는다
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // SuccessHandler가 사용할 수 있도록 등록해준다
        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        var memberAttribute = oAuth2Attribute.convertToMap();

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                        memberAttribute, "email");
        }
}
