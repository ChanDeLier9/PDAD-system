package com.alan.PDAD_system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerDTO {
    @NotNull(message = "问题 ID 不能为空")
    private Integer questionId;

    @NotNull(message = "选项 ID 不能为空")
    private Integer optionId;
}
