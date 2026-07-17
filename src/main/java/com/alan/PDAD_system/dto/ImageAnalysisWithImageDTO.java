package com.alan.PDAD_system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ImageAnalysisWithImageDTO {

    // 基本信息
    private Integer imageId;
    private String patientId;
    private LocalDateTime updateTime;
    private String analysisResult;

    // 图片相关
    private String imageBase64;      // 图片的 Base64 字符串
    private String imageContentType; // 图片类型，比如 "image/tiff"、"image/png"
}
