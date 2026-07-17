package com.alan.PDAD_system.dto;

import lombok.Data;
/* 训练曲线的一个点 */
@Data
public class TrainingStepDTO {
    private Integer step;
    private String metric_name;   // "logloss" / "error" 等
    private Double train_metric;
    private Double val_metric;
}
