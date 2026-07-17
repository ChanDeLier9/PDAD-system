package com.alan.PDAD_system.service.impl;

import com.alan.PDAD_system.dto.MedicalRecordDTO;
import com.alan.PDAD_system.dto.PatientDTO;
import com.alan.PDAD_system.dto.PredictResultDTO;
import com.alan.PDAD_system.dto.QuestionAnswerDTO;
import com.alan.PDAD_system.entity.HealthArticle;
import com.alan.PDAD_system.entity.ImageAnalysisResult;
import com.alan.PDAD_system.entity.Patient;
import com.alan.PDAD_system.mapper.HealthArticleMapper;
import com.alan.PDAD_system.mapper.ImageAnalysisResultMapper;
import com.alan.PDAD_system.mapper.PatientMapper;
import com.alan.PDAD_system.mapper.UserMapper;
import com.alan.PDAD_system.service.PatientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service   //把当前的类注册到容器里

public class PatientServiceImpl implements PatientService {
    private final PatientMapper patientMapper;
    private final UserMapper userMapper;
    private final HealthArticleMapper healthArticleMapper;

    private final ImageAnalysisResultMapper imageAnalysisResultMapper;

    public PatientServiceImpl(PatientMapper patientMapper, UserMapper userMapper, HealthArticleMapper healthArticleMapper,ImageAnalysisResultMapper imageAnalysisResultMapper) {
        this.patientMapper = patientMapper;
        this.userMapper = userMapper;
        this.healthArticleMapper = healthArticleMapper;
        this.imageAnalysisResultMapper = imageAnalysisResultMapper;
    }

    @Transactional
    @Override
    public boolean saveDiagnosisAndScore(String patientId, BigDecimal finalScore, String finalResult) {
        // 调用 Mapper 层保存分数和诊断结果
        int rowsAffected = patientMapper.updatePatientDiagnosisAndScore(patientId, finalScore, finalResult);
        return rowsAffected > 0;
    }
    //获取患者信息
    @Transactional
    @Override
    public  PatientDTO getPatientInfoById(String  patientId)
    {
        // 使用 MyBatis 查询医生信息
        return patientMapper.getPatientInfoById(patientId);
    }
    @Transactional
    @Override
    public boolean updatePatientInfo(String patientId, PatientDTO patientDetails) {
        // Step 1: 查找患者是否存在
        Patient existingPatient = patientMapper.findByPatientId(patientId);
        if (existingPatient == null) {
            System.out.println("患者不存在");
            throw new IllegalArgumentException("患者不存在，无法更新");
        }

        // Step 2: 更新 user 表
        int userRows = userMapper.updateUser(
                patientId,
                patientDetails.getIdNumber(),
                patientDetails.getEmail()
        );
        System.out.println("更新 user 表影响的行数: " + userRows);

        // Step 3: 更新 patient 表
        int patientRows = patientMapper.updatePatient(
                patientId,
                patientDetails.getPatientName(),
                patientDetails.getPhone(),
                patientDetails.getAge()
        );

        System.out.println("更新 patient 表影响的行数: " + patientRows);

        // Step 4: 返回更新结果
        return userRows > 0 && patientRows > 0;
    }

    @Transactional
    @Override
    public List<QuestionAnswerDTO> getPatientAnswersByScale(String patientId, Integer scaleId) {
        // 查询患者答案详情
        return patientMapper.findAnswersByPatientAndScale(patientId, scaleId);
    }

    @Transactional
    @Override
    public String getPatientNameById(String patientId) {
        // 假设这里是通过 patientId 查询患者姓名的逻辑
        // 你可以使用 patientMapper 或其他服务来查询数据库
        return patientMapper.findPatientById(patientId);
    }

    @Override
    public Patient findPatientById(String patientId) {
        return patientMapper.findByPatientId(patientId);
    }
   @Override
   public List<MedicalRecordDTO> getMedicalRecordByNameAndId(String patientId, String patientName){
        return patientMapper.findByNameAndId(patientId,patientName);
   }
    // 【新增】获取所有医疗健康文章的方法实现
    @Transactional // 通常读取操作不是必须 @Transactional，但加上也无妨
    @Override
    public List<HealthArticle> getAllHealthArticles() {
        try {
            return healthArticleMapper.findAllArticles(); //
        } catch (Exception e) {
            System.err.println("Error fetching health articles: " + e.getMessage()); //
            return new ArrayList<>(); //
        }
    }
    // 标记当前用户对某量表是否已清除过（避免重复删除）
    @Transactional
    @Override
    public void saveAnswer(String patientId, Integer scaleId,Integer questionId, Integer optionId) {
        patientMapper.insertAnswer(patientId, questionId, optionId);
    }
    @Transactional
    @Override
    // 开始答题时清除该量表原有记录
    public void startAnswer(String patientId, Integer scaleId) {
        System.out.println("删除前参数 => patientId: " + patientId + ", scaleId: " + scaleId);
        patientMapper.deleteAnswersByPatientAndScale(patientId, scaleId);
    }
    @Override
    @Transactional
    public void saveImageAnalysisResult(String patientId, byte[] imageData, PredictResultDTO predictResult) {
        ImageAnalysisResult entity = new ImageAnalysisResult();
        entity.setPatientId(patientId);
        entity.setImageData(imageData);
        entity.setUpdateTime(LocalDateTime.now());

        // 这里可以把 AI 结果序列化成一段可读字符串存到 analysisResult
        String analysisText = String.format(
                "label=%d, result=%s, probability=%.4f, reliability=%.4f, threshold=%.4f, riskLevel=%s, diagnosis=%s",
                predictResult.getLabel(),
                predictResult.getResult(),
                predictResult.getProbability(),
                predictResult.getReliability(),
                predictResult.getThreshold(),
                predictResult.getRiskLevel(),
                predictResult.getDiagnosis()
        );
        entity.setAnalysisResult(analysisText);

        // 插入数据库
        imageAnalysisResultMapper.insertImageAnalysisResult(entity);
    }

    @Override
    public ImageAnalysisResult getLatestImageAnalysis(String patientId) {
        // 直接调用你给的 mapper 方法
        return imageAnalysisResultMapper.findLatestByPatientId(patientId);
    }









}
