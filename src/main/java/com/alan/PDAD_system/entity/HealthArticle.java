package com.alan.PDAD_system.entity;



import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
@Setter
@Getter
public class HealthArticle {
    private Integer articleId;
    private String doctorId;
    private String title;
    private String content;
    private String tags;
    private Timestamp createdAt;
    private String doctorName;
    // Getter & Setter 省略，可通过 IDE 快速生成
}
