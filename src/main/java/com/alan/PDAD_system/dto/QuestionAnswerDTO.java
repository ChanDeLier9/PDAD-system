package com.alan.PDAD_system.dto;

import lombok.Data;

@Data
public class QuestionAnswerDTO {
    private String optionScaleName;
    private Integer questionId;       // 问题ID
    private String questionContent;  // 问题内容
    private Integer optionId;        // 选项ID
    private String optionContent;    // 选项内容
    private Integer optionScore;     // 选项得分
}