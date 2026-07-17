package com.alan.PDAD_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Configuration
public class SecurityConfig  {
    //BCryptPasswordEncoder是Spring Security提供的密码加密器，
    // 它使用BCrypt强哈希函数对密码进行加密，
    // 并在存储密码时添加随机盐值，
    // 以增加密码的安全性。
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
