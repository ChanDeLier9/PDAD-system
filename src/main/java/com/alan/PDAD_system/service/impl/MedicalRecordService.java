package com.alan.PDAD_system.service.impl;

import com.alan.PDAD_system.dto.DoctorAdviceDTO;
import com.alan.PDAD_system.dto.MedicalRecordRequest;
import com.alan.PDAD_system.entity.MedicalRecord;
import com.alan.PDAD_system.mapper.MedicalRecordMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalRecordService {
    private final MedicalRecordMapper medicalRecordMapper;

    public MedicalRecordService(MedicalRecordMapper medicalRecordMapper) {
        this.medicalRecordMapper = medicalRecordMapper;
    }
    public List<MedicalRecord> findMedicalRecordByPatientIdAndDoctorId(String patientId,String doctorId) {
        // 假设您使用了 MyBatis 或 JPA 进行数据库操作
        // 这里的查询语句可以根据您的数据库设计调整
        return medicalRecordMapper.findByPatientIdAndDoctorId(patientId, doctorId);
    }

    public List<MedicalRecord> findMedicalRecordByPatientId(String patientId) {
        // 假设您使用了 MyBatis 或 JPA 进行数据库操作
        // 这里的查询语句可以根据您的数据库设计调整
        return medicalRecordMapper.findByPatientId(patientId);
    }

    // 根据患者 ID 获取该患者的所有病历记录
    public List<MedicalRecord> getMedicalRecordsByPatientId(String patientId) {
        return medicalRecordMapper.findByPatientId(patientId);
    }


    // 添加患者病历到 medicalrecord 表
    public void addMedicalRecord(String doctorId, MedicalRecordRequest medicalRecordRequest) {
        // 直接插入患者病历信息
        medicalRecordMapper.insertMedicalRecord(medicalRecordRequest.getPatientId(),
                medicalRecordRequest.getPatientName(),
                doctorId);
    }


    public List<DoctorAdviceDTO> getDoctorAdviceByPatientId(String patientId) {
        return medicalRecordMapper.getDoctorAdviceByPatientId(patientId);
    }

}



