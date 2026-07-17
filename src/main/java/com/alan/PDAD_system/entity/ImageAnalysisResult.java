package com.alan.PDAD_system.entity;

import java.time.LocalDateTime;
// 使用 Lombok 简化 Getter, Setter 等代码
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class ImageAnalysisResult {
    private Integer imageId;//头部影像主键id
    private byte[] imageData;  // 图片数据（存储为二进制）
    private LocalDateTime updateTime;//更新时间
    private String analysisResult;     // 影像分析结果的文本描述
    //外键连接 patient表的patient_id
    private String patientId;

}
