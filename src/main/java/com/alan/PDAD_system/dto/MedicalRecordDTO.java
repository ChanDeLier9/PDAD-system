package com.alan.PDAD_system.dto;

import lombok.Data;

@Data
public class MedicalRecordDTO {
    private String patientId;      // 患者ID
    private String patientName;     // 患者姓名
    private String diagnosis;       // 诊断信息
    private String treatment;
}
