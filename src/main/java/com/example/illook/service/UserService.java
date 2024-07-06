package com.example.illook.service;

import com.example.illook.mapper.PostMapper;
import com.example.illook.mapper.UserMapper;
import com.example.illook.model.Image;
import com.example.illook.model.User;
import com.example.illook.payload.UserRequestDto.Login;
import com.example.illook.payload.UserRequestDto.SignUp;
import com.example.illook.payload.UserRequestDto.TokenInfo;
import com.example.illook.security.JwtTokenProvider;
import com.example.illook.util.FileHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final FileHandler fileHandler;
    private final PostMapper postMapper;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;




    //유저 저장
    public void saveUser(SignUp signUp){
        String[] items = {"dog-g036b63d18_1920.jpg","girl-g996e72491_1920.jpg","tiktok-ga428cefdb_1920.jpg","woman-ga0cc40122_1920.jpg"};
        Random rand = new Random();

        User user = User.builder()
                .id(signUp.getId())
                .email(signUp.getEmail())
                .password(passwordEncoder.encode(signUp.getPassword()))
                .nickname(signUp.getNickname())
                .profileImage("images"+ File.separator +"basic"+ File.separator +items[rand.nextInt(4)])
                .role("ROLE_USER")
                .build();

        userMapper.saveUser(user);
    }

    //유저 수정
    // @Aspect 사용할라고 리턴 값 받음
    public void updateUser(List<MultipartFile> files, String nickname, User user) throws Exception {
        List<Image> imageList = fileHandler.parseFileInfo(files, null);
        userMapper.updateUser(nickname, imageList.get(0).getPath(), Integer.parseInt(user.getUserIdx()));

    }

    //로그인
    public TokenInfo login(Login login){
        //사용자 인증
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(login.getId(), login.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("User is {}", authentication);

        //토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.createAllToken(authentication);
        //리프레시 토큰 redis에 저장(expirationTime 설정을 통해 자동 삭제 처리)
        redisTemplate.opsForValue().set("RT:"+authentication.getName(),
                tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MICROSECONDS);
        return tokenInfo;
    }

    //토큰 재발급을 위한 reissue()
    public TokenInfo reissue(String AT, String RT , HttpServletRequest request){

        // refresh token 검증
        if(!jwtTokenProvider.validateToken(RT, request)){
            throw new IllegalArgumentException("Refresh Token 정보가 유효하지 않습니다.");
        }

        // Access Token 에서 User email 를 가져옴
        Authentication authentication = jwtTokenProvider.getAuthentication(AT);

        // Redis 에서 User email 을 기반으로 저장된 Refresh Token 값을 가져옴
        String refreshToken = redisTemplate.opsForValue().get("RT:"+authentication.getName());

        if(refreshToken != null && !refreshToken.equals(RT)){
            throw new IllegalStateException("Refresh Token 정보가 일치하지 않습니다");
        }
        // 새로운 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.createAllToken(authentication);

        // RefreshToken Redis 업데이트
        redisTemplate.opsForValue().set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(),
                        tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return tokenInfo;
    }

    public void logout(HttpServletRequest request){

        String accessToken = jwtTokenProvider.resolveAccessToken((HttpServletRequest) request);
        // 로그아웃 하고 싶은 토큰이 유효한 지 먼저 검증하기
        if (!jwtTokenProvider.validateToken(accessToken, request)){
            throw new IllegalArgumentException("로그아웃 : 유효하지 않은 토큰입니다.");
        }

        // Access Token에서 User email을 가져온다
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        // Redis에서 해당 User email로 저장된 Refresh Token 이 있는지 여부를 확인 후에 있을 경우 삭제를 한다.
        if (redisTemplate.opsForValue().get("RT:"+authentication.getName())!=null){
            // Refresh Token을 삭제
            redisTemplate.delete("RT:"+authentication.getName());
        }

        // 해당 Access Token 유효시간을 가지고 와서 BlackList에 저장하기
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue().set(accessToken,"logout",expiration, TimeUnit.MILLISECONDS);
    }

    @Transactional
    public Map getProfile(int userIdx) {
        List<Map> images = postMapper.getImage(userIdx);
        Map data = userMapper.getUserProfile(userIdx);

        data.put("images",images);
        return data;
    }


}
