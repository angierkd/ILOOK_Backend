package com.example.illook.controller;

import com.example.illook.mapper.UserProfileMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MailControllerTest {


    /*
*   //1. 이메일 중복 체크, 인증번호 전송
    @PostMapping("/email/auth")
    public ResponseEntity<ApiPostResponse> mailAuth(@Valid @RequestBody EmailRequest emailRequest, HttpServletRequest request){

        mailService.checkEmail(emailRequest.getEmail());
        //비즈니스 로직
        HttpSession session = request.getSession();
        mailService.mailSend(session, emailRequest.getEmail());
        return new ResponseEntity<>(new ApiPostResponse(200, "사용 가능한 이메일입니다"), HttpStatus.OK);
    }

    //3. 인증번호 일치여부
    @PostMapping("/mail/check/auth")
    public boolean checkAuth(HttpServletRequest request, EmailRequest emailRequest, @RequestParam("inputCode") String inputCode){
        HttpSession session = request.getSession();
        return mailService.checkAuth(session, emailRequest.getEmail(), inputCode);
    }
* */

    @Autowired
    private MockMvc mockMvc;
    private static UserProfileMapper userProfileMapper;
}