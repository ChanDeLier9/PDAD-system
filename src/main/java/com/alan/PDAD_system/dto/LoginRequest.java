package com.alan.PDAD_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    @NotBlank(message = "账号不能为空")
    private String doctorId;

    @NotBlank(message = "密码不能为空")
    private String password;

}
