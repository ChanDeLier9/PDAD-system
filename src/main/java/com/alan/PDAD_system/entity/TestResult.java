package com.alan.PDAD_system.entity;


import java.time.LocalDateTime;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Data;
import lombok.Setter;

@Setter
@Getter
@Data
public class TestResult {
    private Integer resultId;
    private LocalDateTime evaluationDate;
    private BigDecimal scores;
    private String suggestions;
    //外键连接 Patient表的patient_id
    private String patientId;
    //外键连接 scale表的scale_id
    private Integer scaleId;
}
