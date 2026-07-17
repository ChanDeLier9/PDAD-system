package com.alan.PDAD_system.dto;

import lombok.Data;

@Data
public class MedicalRecordRequest {
    private String patientId;     // 患者ID
    private String patientName;   // 患者姓名
    private String doctorId;      // 医生ID
    private String diagnosis;     // 诊断信息
    private String treatment;
}
