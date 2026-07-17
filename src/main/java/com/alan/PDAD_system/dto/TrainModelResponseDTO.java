package com.alan.PDAD_system.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/* 训练结果 DTO（Python → Java → 前端） */
@Data
public class TrainModelResponseDTO {
    private String model_type;
    private Map<String, Object> params_used;
    private Integer n_train;
    private Integer n_val;
    private Map<String, Double> final_metrics;
    private List<TrainingStepDTO> curve;
}
