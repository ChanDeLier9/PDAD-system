package com.alan.PDAD_system.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class HealthArticleRequestDTO {
    private String title;
    private String content;
    private String tags;
   private String doctor_name;
}
