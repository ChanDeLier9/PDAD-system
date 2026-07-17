package com.alan.PDAD_system.dto;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class DoctorDTO {
    private String email;
    private String doctorName;
    private String idNumber;
    private Integer age;
    private String gender;
    private String phone;
    private String experience;
    private String doctorId;
}
