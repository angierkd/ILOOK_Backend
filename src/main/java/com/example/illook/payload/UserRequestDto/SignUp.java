package com.example.illook.payload.UserRequestDto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignUp {

    @NotBlank(message = "필수 항목입니다")
    String id;

    @NotBlank
    @Email
    String email;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,}$", message = "숫자, 문자, 특수문자 포함 8자 이상이여야 합니다.")
    @NotBlank(message = "필수 항목입니다")
    String password;

    String password2;

    @NotBlank(message = "필수 항목입니다")
    String nickname;

}
