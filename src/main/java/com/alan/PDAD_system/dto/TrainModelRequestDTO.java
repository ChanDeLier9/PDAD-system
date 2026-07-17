package com.alan.PDAD_system.dto;

import lombok.Data;
import java.util.Map;
/* 训练请求 DTO（前端 → Java） */
@Data
public class TrainModelRequestDTO {
    /**
     * "logreg" | "rf" | "xgb" | "lgbm"
     */
    private String modelType;

    /**
     * 模型超参数，如：
     * {
     *   "n_estimators": 200,
     *   "max_depth": 4,
     *   "learning_rate": 0.05
     * }
     */
    private Map<String, Object> params;

    /**
     * 训练集比例，默认 0.8
     */
    private Double trainRatio;

    /**
     * 随机种子，默认 2025
     */
    private Integer randomState;
}
