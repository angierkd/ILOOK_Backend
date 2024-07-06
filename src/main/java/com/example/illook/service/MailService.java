package com.example.illook.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "git1000@naver.com";
    private final RedisTemplate<String, String> redisTemplate;
    // private final UserMapper mapper;

    public void mailSend(HttpSession session, String email) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            message.setFrom(new InternetAddress(FROM_ADDRESS));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));

            String inputCode = randomNumber();
            message.setSubject("인증번호");
            message.setText("인증번호는 "+ inputCode +" 입니다.");
            mailSender.send(message);
            //session.setAttribute(""+email, inputCode);
            //System.out.println(session.getAttribute(email));
            System.out.println(inputCode);
            redisTemplate.opsForValue().set(inputCode, email,  5L, TimeUnit.MINUTES);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    public String randomNumber(){
        Random random = new Random();
        String code = String.valueOf(random.nextInt(888888) + 111111);
        return code;
    }
}
