package com.example.illook.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

                String accessToken = jwtTokenProvider.resolveAccessToken((HttpServletRequest) request);

                //aceess 토큰 유효성 체크
                if(accessToken != null) {
                    if (jwtTokenProvider.validateToken(accessToken, (HttpServletRequest) request)) {
                        //Redis에 해당 accessToken logout 여부를 확인
                        String isLogout = (String) redisTemplate.opsForValue().get(accessToken);
                        //토큰이 유효하면 SecurityContextHolder에 저장
                        if (ObjectUtils.isEmpty(isLogout)) {
                            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }


        chain.doFilter(request, response);
    }

}
