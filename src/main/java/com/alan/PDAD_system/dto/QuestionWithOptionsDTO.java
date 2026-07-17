package com.alan.PDAD_system.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionWithOptionsDTO {
    private Integer question_id;   // 问题ID
    private String questionText;  // 问题内容
    private List<OptionDTO> options;  // 该问题的选项

    @Data
    public static class OptionDTO {
        private Integer option_id;   // 选项ID
        private String optionText;  // 选项内容
        private Integer score;      // 选项对应的得分
    }
}
