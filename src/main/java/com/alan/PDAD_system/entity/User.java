package com.alan.PDAD_system.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Data;
import lombok.Setter;

@Setter
@Getter
@Data
public class User {
    private String userId;//账号
    @JsonIgnore
    private String idNumber;//身份证号码
    private String password;//密码
    private String email;//邮箱
    private String role;//身份
    private String status;//状态 pending approved rejected
    private int age;
    private String gender;
    public User() {
        this.password = password;
        this.email = email;
        this.role = role;
        this.status = status;
    }


}