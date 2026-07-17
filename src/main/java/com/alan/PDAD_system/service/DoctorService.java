package com.alan.PDAD_system.service;

import com.alan.PDAD_system.dto.*;
import com.alan.PDAD_system.entity.*;

import java.util.List;

public interface DoctorService {

    Doctor findDoctorById(String doctorId);
    DoctorDTO getDoctorInfoById(String doctorId);
    // 更新医生的职业生涯等信息
    boolean updateDoctorInfo(String doctorId, DoctorDTO doctorDetails);
    // 获取医生治疗过的所有患者及其相关的就诊和治疗信息
    List<PatientDTO> getPatientsByDoctorIdAndName(String patientName);

    List<MedicalRecordDTO> getMedicalRecordByName(String patientName);

    List<MedicalRecordDTO> getPatientsByDoctorId(String doctorId);

    void deleteMedicalRecord(String patientId);

    void updateMedicalRecord(String doctorId, String patientId,
                             MedicalRecordUpdateRequest medicalRecordUpdateRequest);

    // 根据医生 ID 和患者 ID 获取患者基本信息
    PatientDTO getPatientInfoByDoctor(String doctorId, String patientId);


    MedicalRecord getMedicalRecordByDoctorIdAndPatientId(String doctorId, String patientId);
    // 根据医生 ID 和患者 ID 获取患者的头部影像分析结果
    ImageAnalysisResult getPatientImageAnalysisByDoctor( String patientId);

    // 根据医生 ID 和患者 ID 获取患者的测试结果
    List<TestResultDTO> getPatientTestResultsByDoctor(String doctorId, String patientId);



    void publishArticle(String doctorId, HealthArticleRequestDTO articleDTO);

}
