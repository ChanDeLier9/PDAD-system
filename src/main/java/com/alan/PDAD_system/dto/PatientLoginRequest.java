package com.alan.PDAD_system.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class PatientLoginRequest {
    @NotBlank(message = "账号不能为空")
    private String patientId;

    @NotBlank(message = "密码不能为空")
    private String password;
}
