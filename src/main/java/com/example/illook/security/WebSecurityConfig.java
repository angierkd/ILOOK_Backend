package com.example.illook.security;

import com.example.illook.mapper.UserMapper;
import com.example.illook.payload.Response.ApiResponse;
import com.example.illook.security.OAuth2.OAuth2SuccessHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final CustomUserDetailService customUserDetailService;
    private final RedisTemplate redisTemplate;
    private final UserMapper userMapper;

    // 암호화에 필요한 PasswordEncoder 를 Bean 등록합니다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 권한 부여
        http
                .httpBasic().disable() // rest api 만을 고려하여 기본 설정은 해제하겠습니다.
                .csrf().disable() // csrf 보안 토큰 disable처리.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 역시 사용하지 않습니다.
                .and()
                .authorizeRequests() // 요청에 대한 사용권한 체크
                //.antMatchers("/user/help/**", "/user/check/**","/user/mailConfirm", "/user/login","/user/reissue","/pictures","/user/reissue").permitAll()
                //.antMatchers(HttpMethod.POST, "/user").permitAll()
                //.anyRequest().hasRole("USER") // 그외 나머지 요청은 누구나 접근 가능
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate),
                        UsernamePasswordAuthenticationFilter.class);

        // 에러 헨들링
                http.exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {

                    String exception = (String) request.getAttribute("exceptionType");

                    if(exception != null && exception.equals("ExpiredJwt")){
                        response(response, "JWT 토큰이 만료되었습니다.");
                    }else{
                        response(response, "로그인 오류가 발생하였습니다.");
                    }
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response(response, "권한이 없는 사용자입니다.");
                });

    }

    public void response(HttpServletResponse response, String message) throws IOException {
        response.reset();
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(new ObjectMapper().writeValueAsString(ApiResponse.createError(message)));
    }

    public static HashMap<String, String> getPayloadByToken(String token) {
        try {
            String[] splitJwt = token.split("\\.");

            Base64.Decoder decoder = Base64.getDecoder();
            String payload = new String(decoder.decode(splitJwt[1] .getBytes()));
            return new ObjectMapper().readValue(payload, HashMap.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

}
