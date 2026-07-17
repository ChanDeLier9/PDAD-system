package com.alan.PDAD_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @Pattern(regexp = "^[A-Za-z\\d]{3,6}$", message = "用户ID必须为3到6位的字母和数字")
    private String userId;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,15}$",
            message = "密码必须包含至少一个大写字母、一个小写字母、一个数字，长度为8到15个字符")
    private String password;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^[0-9]{15,18}$", message = "身份证号格式不正确")
    private String idNumber;

    private String role;

    @Min(value = 10, message = "年龄必须大于或等于10岁")
    @Max(value = 100, message = "年龄必须小于或等于100岁")
    private int age;

    private String gender;
}
