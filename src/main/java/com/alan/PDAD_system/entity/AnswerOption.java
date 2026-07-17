package com.alan.PDAD_system.entity;
import lombok.Getter;
import lombok.Data;
import lombok.Setter;

@Setter
@Getter
@Data
public class AnswerOption {
    private Integer optionId;
    private String optionText;
    private int score;
    //外键连接 Question表的question_id
    private Integer questionId;
}
