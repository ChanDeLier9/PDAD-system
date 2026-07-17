package com.alan.PDAD_system.dto;

import lombok.Data;

@Data
public class PatientAnswerRequest {
    private Integer questionId;  // 问题ID
    private Integer optionId;    // 选择的答案选项ID
}