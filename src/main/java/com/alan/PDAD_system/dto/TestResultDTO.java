package com.alan.PDAD_system.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TestResultDTO {
    private Integer patientId;

    private Integer resultId;
    private LocalDateTime evaluationDate;
    private BigDecimal scores;
    private String suggestions;
    //外键连接 scale表的scale_id
    private Integer scaleId;
    private String scaleName;
}
