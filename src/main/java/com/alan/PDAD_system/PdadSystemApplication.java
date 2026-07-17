package com.alan.PDAD_system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("com.alan.PDAD_system.mapper")
@SpringBootApplication(scanBasePackages = "com.alan.PDAD_system")
public class PdadSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(PdadSystemApplication.class, args);
	}
}
