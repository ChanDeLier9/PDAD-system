package com.alan.PDAD_system.dto;

import lombok.Data;

@Data
public class MedicalRecordUpdateRequest {
    private String diagnosis;    // 诊断信息
    private String treatment;    // 治疗建议
}
