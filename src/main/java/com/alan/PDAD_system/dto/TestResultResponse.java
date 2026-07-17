package com.alan.PDAD_system.dto;

import com.alan.PDAD_system.entity.PatientAnswer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TestResultResponse {
    private Integer resultId;
    private BigDecimal score;  // 总得分
    private String suggestions;  // 测试建议
    private List<PatientAnswer> patientAnswers;
}
