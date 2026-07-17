package com.alan.PDAD_system.dto;

import lombok.Data;

@Data
public class PatientDTO {
    private String patientId;       // 患者ID
    private String IdNumber;
    private String patientName;      // 患者姓名
    private String phone; // 电话号码
    private String gender;      // 性别
    private String email;        // 邮箱
    private int age;            // 年龄

}
