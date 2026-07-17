package com.alan.PDAD_system.entity;

import java.time.LocalDateTime;

public class Admin {
    private Integer admin_id;//管理员主键ID
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间

    //外键连接 user表的userid
    private Integer user_id;
}
