package com.alan.PDAD_system.entity;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class PatientAnswer {
    private Integer answerId;
    //外键连接 result表的result_id
    private Integer resultId;
    //外键连接 question表的question_id
    private Integer questionId;
    //外键连接option表的option_id
    private Integer optionId;
}
