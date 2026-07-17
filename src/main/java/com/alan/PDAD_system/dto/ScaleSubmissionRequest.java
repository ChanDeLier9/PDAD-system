package com.alan.PDAD_system.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScaleSubmissionRequest {
    private String patientId; // 提交答案的患者ID
    private Integer scaleId; // 也可以在此处包含, 用于校验路径参数与请求体一致性
    private List<AnswerDTO> answers; // 患者的答案列表

}