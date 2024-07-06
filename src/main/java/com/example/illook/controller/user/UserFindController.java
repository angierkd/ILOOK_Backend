package com.example.illook.controller.user;

import com.example.illook.mapper.UserMapper;
import com.example.illook.payload.Response.ApiResponse;
import com.example.illook.payload.UserRequestDto.EmailRequest;
import com.example.illook.payload.UserRequestDto.PasswordRequest;
import com.example.illook.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.NoSuchElementException;

import static com.example.illook.util.mybatisEmpty.empty;

@RestController
@RequiredArgsConstructor
public class UserFindController {

    private final UserMapper mapper;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    //아이디, 비밀번호 찾기 할 때
    //공통 이메일 인증
    @PostMapping("/user/help/mailConfirm")
    public ApiResponse find(@Valid @RequestBody EmailRequest emailRequest, HttpServletRequest httpServletRequest){

        //등록된 이메일인지 체크
        if(empty(mapper.checkEmailDuplicate(emailRequest.getEmail()))){
            throw new NoSuchElementException("존재하지 않는 이메일입니다");
        }

        //이메일로 인증코드 전송
        mailService.mailSend(httpServletRequest.getSession(), emailRequest.getEmail());

        return ApiResponse.createSuccessWithNoContent();
    }

    //아이디 찾기
    @PostMapping("/user/help/idInquiry")
    public ApiResponse checkCode(@RequestBody EmailRequest emailRequest, @RequestParam("inputCode") String inputCode){
        /*HttpSession session = request.getSession();
        if(!session.getAttribute(emailRequest.getEmail()).equals(inputCode)){
            throw new IllegalStateException("인증번호가 일치하지 않습니다");
        };*/

        String key = redisTemplate.opsForValue().get(inputCode);
        if(!key.equals(emailRequest.getEmail())){
            throw new IllegalStateException("인증번호가 일치하지 않습니다");
        }

        //일치하면 사용자 id return
        return ApiResponse.createSuccess(mapper.findId(emailRequest.getEmail()));
    }

    //비밀번호 인증번호 확인
    @PostMapping("/user/help/pwInquiry")
    public ApiResponse findPwd(@RequestBody EmailRequest emailRequest, @RequestParam("id") String id, @RequestParam("inputCode") String inputCode){

        String key = redisTemplate.opsForValue().get(inputCode);
        if(!key.equals(emailRequest.getEmail())){
            throw new IllegalStateException("인증번호가 일치하지 않습니다");
        }

        if(empty(mapper.checkUser(emailRequest.getEmail(), id))){
            throw new NoSuchElementException("등록된 사용자가 없습니다");
        }
        return ApiResponse.createSuccessWithNoContent();
    }

    //비밀번호 변경
    @PatchMapping("/user/help/reset/password/{id}")
    public ApiResponse changePwd(@PathVariable String id, @Valid @RequestBody PasswordRequest passwordRequest, BindingResult bindingResult) throws MethodArgumentNotValidException {

        if(!passwordRequest.getPassword().equals(passwordRequest.getPassword2())){
            bindingResult.rejectValue("password2","range","비밀번호가 같지 않습니다");
        }

        if(bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }

        //비밀번호 변경
        mapper.changePwd(passwordEncoder.encode(passwordRequest.getPassword()), id);
        return ApiResponse.createSuccess("비밀번호가 변경되었습니다");
    }

}
