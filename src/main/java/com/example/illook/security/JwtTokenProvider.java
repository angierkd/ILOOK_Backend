package com.example.illook.security;

import com.example.illook.mapper.UserMapper;
import com.example.illook.payload.UserRequestDto.TokenInfo;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private String secretKey = "webfirewood";

    private long ACCESS_TOKEN_EXPIRE_TIME  = 30 * 60 * 1000L; //30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME  = 1000 * 60 * 60 * 24 * 7;  // 7일

    private final CustomUserDetailService userDetailsService;
    private final UserMapper userProfileMapper;

    private final RedisTemplate redisTemplate;


    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public TokenInfo createAllToken(Authentication authentication){
        String name = authentication.getName();
        String authorities = authentication.getAuthorities().toString();

        //AT 생성
        String accessToken = createAccessToken(name, authorities);
        //RT 생성
        String refreshToken = createRefreshToken(name, authorities);

        return TokenInfo.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .refreshTokenExpirationTime(REFRESH_TOKEN_EXPIRE_TIME)
                .build();
    }

    public String createAccessToken(String userEmail, String roles){
        Claims claims = Jwts.claims().setSubject(userEmail);
        claims.put("roles", roles);
        Date now = new Date();

       return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // jwt refresh 토큰 생성
    public String createRefreshToken(String userEmail, String roles) {

        Claims claims = Jwts.claims().setSubject(userEmail);
        claims.put("roles", roles);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    //만료되지 않은 토큰 정보만 꺼낼 수 있었다.
   /* public Authentication getAuthentication(String token){
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
*/
   // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken){
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);
        System.out.println(claims);
        if (claims.get("roles") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("roles").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = userDetailsService.loadUserByUsername(claims.get("sub").toString());
        return new UsernamePasswordAuthenticationToken(principal,"", principal.getAuthorities());
    }

    // Request의 Header에서 AccessToken 값을 가져옵니다. "authorization" : "token'
    public String resolveAccessToken(HttpServletRequest request) {
        if(request.getHeader("accessToken") != null )
            return request.getHeader("accessToken");
        return null;
    }

    // Request의 Header에서 RefreshToken 값을 가져옵니다. "authorization" : "token'
    public String resolveRefreshToken(HttpServletRequest request) {
        if(request.getHeader("refreshToken") != null )
            return request.getHeader("refreshToken");
        return null;
    }

    // 리프레시 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken, HttpServletRequest request) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        }catch (ExpiredJwtException e){
            e.printStackTrace();
            request.setAttribute("exceptionType", "ExpiredJwt");
            return false;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Long getExpiration(String accessToken) {
        // accessToken 남은 유효시간
        Date expiration = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody().getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    private Claims parseClaims(String accessToken){
        try{
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken).getBody();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

    // 어세스 토큰 헤더 설정
    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader("accessToken", "bearer "+ accessToken);
    }

    // 리프레시 토큰 헤더 설정
    public void setHeaderRefreshToken(HttpServletResponse response, String refreshToken) {
        response.setHeader("refreshToken", "bearer "+ refreshToken);
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public String getRoles(String userPK) {
        return userProfileMapper.findEmail(userPK);
    }


    // RefreshToken 존재유무 확인
    public boolean existsRefreshToken(String refreshToken) {
        return userProfileMapper.existRefreshToken(refreshToken);
    }
}
