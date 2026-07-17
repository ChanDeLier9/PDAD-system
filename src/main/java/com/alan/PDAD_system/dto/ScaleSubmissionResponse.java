package com.alan.PDAD_system.dto;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScaleSubmissionResponse {
    private String message;
    private BigDecimal finalScore;
    private String suggestions;
}