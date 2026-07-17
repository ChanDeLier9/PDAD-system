package com.alan.PDAD_system.dto;

import lombok.Data;

@Data
public class UserSimpleDTO {
    private String userId;     // 用户唯一ID
    private String username;   // 用户显示名（患者姓名 / 医生姓名）
}

