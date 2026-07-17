package com.alan.PDAD_system.dto;

import lombok.Data;

import java.util.Map;

/* Python 返回的原始结构（保持字段名一致） */
@Data
public  class PredictFmriResponseDTO {
    private Integer label;
    private String result;
    private Double probability;
    private Double reliability;
    private Double threshold;
    private String diagnosis;
    private String risk_level;
    private Map<String, Object> model_info;
    private Map<String, Object> debug;

}
