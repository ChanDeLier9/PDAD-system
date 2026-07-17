package com.alan.PDAD_system.entity;
import lombok.Data;
@Data
public class Question {
    private Integer questionId;
    private String questionText;
    //外键连接 Scale表的scale_id
    private Integer scaleId;
}
