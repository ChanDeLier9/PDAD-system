package com.alan.PDAD_system.entity;

import java.time.LocalDateTime;
import lombok.Data;
@Data
public class Doctor {
    private String doctorName;//医生姓名
    private String doctorId;//医生主键ID
    private String phone;//电话号码
    private String experience;//工作时间
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间
}
