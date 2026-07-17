package com.alan.PDAD_system.entity;

import java.time.LocalDateTime;
import lombok.Data;
@Data


public class Patient {
    private String patientName;//患者名字
    private String patientId;//患者主键ID
    private String email;//邮箱
    private String phone;//电话号码
    private String gender;//性别
    private int age;//年龄
    private byte[] imageData;  // 图片数据（存储为二进制）
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间

}
