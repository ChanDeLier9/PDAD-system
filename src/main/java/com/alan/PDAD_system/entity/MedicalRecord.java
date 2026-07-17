package com.alan.PDAD_system.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MedicalRecord {
    private Integer recordId;//患者主键ID
    private String diagnosis;//患者分析结果
    private String treatment;//治疗建议
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间
    //外键连接 patient表的patient_id
    private String patientId;
    //外键连接 doctor表的doctorId
    private String doctorId;
}
