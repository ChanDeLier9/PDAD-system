package com.alan.PDAD_system.dto;

import lombok.Data;
import java.util.Map;
/*返回给前端的结果 DTO（驼峰）*/
@Data
public class PredictResultDTO {
    private Integer label;
    private String result;
    private Double probability;
    private Double reliability;
    private Double threshold;
    private String diagnosis;
    private String riskLevel;          // 驼峰
    private Map<String, Object> modelInfo;
}
