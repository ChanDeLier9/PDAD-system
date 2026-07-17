package com.alan.PDAD_system.entity;

import lombok.Data;

@Data
public class ScoreSuggestion {
    private Integer id;
    private Integer minScore;
    private Integer maxScore;
    private String suggestion;
}
