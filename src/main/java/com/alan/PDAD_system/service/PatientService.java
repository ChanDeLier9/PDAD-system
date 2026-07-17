package com.alan.PDAD_system.service;

import com.alan.PDAD_system.dto.MedicalRecordDTO;
import com.alan.PDAD_system.dto.PatientDTO;
import com.alan.PDAD_system.dto.PredictResultDTO;
import com.alan.PDAD_system.dto.QuestionAnswerDTO;
import com.alan.PDAD_system.entity.HealthArticle;
import com.alan.PDAD_system.entity.ImageAnalysisResult;
import com.alan.PDAD_system.entity.Patient;

import java.math.BigDecimal;
import java.util.List;

public interface PatientService {
    boolean saveDiagnosisAndScore(String patientId, BigDecimal finalScore, String finalResult);
     PatientDTO getPatientInfoById(String PatientId);//获取患者信息
    boolean updatePatientInfo(String patientId, PatientDTO patientDetails);//更新患者信息
    String getPatientNameById(String patientId);

    List<QuestionAnswerDTO> getPatientAnswersByScale(String patientId, Integer scaleId);
    Patient findPatientById(String patientId);

    List<MedicalRecordDTO> getMedicalRecordByNameAndId(String patientId,String patientName);


    // 【新增】获取所有医疗健康文章的方法声明
    List<HealthArticle> getAllHealthArticles(); //

    void saveAnswer(String patientId,  Integer scaleId,Integer questionId, Integer optionId);
    void startAnswer(String patientId, Integer scaleId);

    /**
     * 保存患者一次影像分析结果到 image_results 表
     */
    void saveImageAnalysisResult(String patientId, byte[] imageData, PredictResultDTO predictResult);
    ImageAnalysisResult getLatestImageAnalysis(String patientId);
}


