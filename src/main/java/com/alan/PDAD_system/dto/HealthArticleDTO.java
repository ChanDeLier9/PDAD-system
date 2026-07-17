package com.alan.PDAD_system.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HealthArticleDTO {
    private Integer articleId;
    private String doctorId;
    private String doctorName;
    private String title;
    private String content;
    private String tags;
    private String createdAt;
}

