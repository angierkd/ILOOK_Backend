package com.example.illook.security.OAuth2;

import com.example.illook.mapper.UserMapper;
import com.example.illook.model.User;
import com.example.illook.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper mapper;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        System.out.println(oAuth2User);

        User user = User.builder()
                .id(oAuth2User.getAttribute("sub"))
                .email(oAuth2User.getAttribute("email"))
                .profileImage(oAuth2User.getAttribute("picture"))
                        .build();


        String targetUrl;

        mapper.saveOAuth2User(user);
        
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), "USER");
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail(), "USER");
        jwtTokenProvider.setHeaderAccessToken(response, accessToken);
        jwtTokenProvider.setHeaderRefreshToken(response, refreshToken);

        mapper.saveRefreshToken(refreshToken, user.getEmail());

        System.out.println(accessToken);
        System.out.println(refreshToken);

        targetUrl = UriComponentsBuilder.fromUriString("/home")
                .queryParam("token", "token")
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
    
    
}
