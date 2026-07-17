package com.alan.PDAD_system.service.impl;

import com.alan.PDAD_system.dto.*;
import com.alan.PDAD_system.entity.Doctor;
import com.alan.PDAD_system.entity.ImageAnalysisResult;
import com.alan.PDAD_system.entity.MedicalRecord;
import com.alan.PDAD_system.mapper.*;
import com.alan.PDAD_system.service.DoctorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Service   //把当前的类注册到容器里
@Slf4j // 使用 Lombok 提供的日志功能
public class DoctorServiceImpl implements DoctorService {
    //因为在server层会用到Mapper层的方法操作数据库，因此这里
    // 定义一个DoctorMapper类的对象doctorMapper，后续可以通过
    // 该对象实现对数据库的操作。
    private final DoctorMapper doctorMapper;
    private final UserMapper userMapper;
    private final HealthArticleMapper healthArticleMapper;
    private final ImageAnalysisResultMapper imageAnalysisResultMapper;
    private final MedicalRecordMapper medicalRecordMapper;
    public DoctorServiceImpl(DoctorMapper doctorMapper, UserMapper userMapper, HealthArticleMapper healthArticleMapper, MedicalRecordMapper medicalRecordMapper,ImageAnalysisResultMapper imageAnalysisResultMapper ) {
        this.doctorMapper = doctorMapper;
        this.userMapper = userMapper;
        this.healthArticleMapper = healthArticleMapper;
        this.medicalRecordMapper = medicalRecordMapper ;
        this.imageAnalysisResultMapper = imageAnalysisResultMapper;

    }


    @Override
    public Doctor findDoctorById(String doctorId) {
        return doctorMapper.findByDoctorId(doctorId);
    }

    @Override
    @Transactional // 确保更新操作是原子性的
    public DoctorDTO getDoctorInfoById(String doctorId) {
        // 使用 MyBatis 查询医生信息
        return doctorMapper.getDoctorInfoById(doctorId);
    }


    @Override
    @Transactional
    public boolean updateDoctorInfo(String doctorId, DoctorDTO doctorDetails) {
        // Step 1: 查找医生是否存在
        Doctor existingDoctor = doctorMapper.findByDoctorId(doctorId);
        if (existingDoctor == null) {
            System.out.println("医生不存在");
            throw new IllegalArgumentException("医生不存在，无法更新");
        }

        // Step 2: 更新 user 表
        int userRows = userMapper.updateUser(
                doctorId,
                doctorDetails.getIdNumber(),
                doctorDetails.getEmail()
        );
        System.out.println("更新 user 表影响的行数: " + userRows);

        // Step 3: 更新 doctor 表
        int doctorRows = doctorMapper.updateDoctor(
                doctorId,
                doctorDetails.getDoctorName(),
                doctorDetails.getPhone(),
                doctorDetails.getExperience()
        );

        System.out.println("更新 doctor 表影响的行数: " + doctorRows);

        // Step 4: 返回更新结果
        return userRows > 0 && doctorRows > 0;
    }

    // 查询医生治疗过的患者，并根据患者名字进行筛选 从患者表里查询
    @Transactional
    @Override
    public List<PatientDTO> getPatientsByDoctorIdAndName( String patientName) {
        // 使用 doctorId 查找所有治疗过的患者，然后根据 patientName 进行筛选
        return doctorMapper.findPatientsByName(patientName);
    }
//从mr表中查询特定患者
@Transactional
    @Override
    public List<MedicalRecordDTO> getMedicalRecordByName(String patientName) {
        // 调用 Mapper 查询是否有该患者的记录
        return doctorMapper.findByPatientName(patientName);
    }
//从mr表查询所有患者
@Transactional
    @Override
    public List<MedicalRecordDTO> getPatientsByDoctorId(String doctorId) {
        // 调用 Mapper 查询与医生相关的患者记录
        return doctorMapper.findPatientsByDoctorId(doctorId);
    }

    // 删除患者病历
    public void deleteMedicalRecord(String patientId) {
        // 删除指定患者的病历记录
        medicalRecordMapper.deleteMedicalRecordByPatientId(patientId);
    }


    // 更新病历信息
    public void updateMedicalRecord(String doctorId, String patientId,
                                    MedicalRecordUpdateRequest medicalRecordUpdateRequest) {
        // 调用 Mapper 层更新病历记录
        medicalRecordMapper.updateDiagnosisAndTreatment(doctorId, patientId,
                medicalRecordUpdateRequest.getDiagnosis(),
                medicalRecordUpdateRequest.getTreatment());
    }


    @Transactional
    @Override
    public PatientDTO getPatientInfoByDoctor(String doctorId, String patientId) {
        try {
            // 查询患者信息
            return doctorMapper.findPatientByDoctorAndPatientId(doctorId, patientId);
        } catch (Exception e) {
            // 记录日志并处理异常
            System.err.println("Error occurred while fetching patient information: " + e.getMessage());
            return null;
        }
    }
    // 根据 doctorId 和 patientId 获取病历记录
    @Transactional
    @Override
    public MedicalRecord getMedicalRecordByDoctorIdAndPatientId(String doctorId, String patientId) {
        return medicalRecordMapper.findMedicalRecordByDoctorIdAndPatientId(doctorId, patientId);
    }


//查询脑部图像结果
    @Transactional
    @Override
    public ImageAnalysisResult getPatientImageAnalysisByDoctor( String patientId) {

        return imageAnalysisResultMapper.findLatestByPatientId(patientId);
    }

    @Transactional
    @Override
    public List<TestResultDTO> getPatientTestResultsByDoctor(String doctorId, String patientId) {
        try {
            // 查询患者的测试结果
            List<TestResultDTO> testResults = doctorMapper.findTestResultsByDoctorAndPatientId(doctorId, patientId);
            return testResults != null ? testResults : new ArrayList<>();
        } catch (Exception e) {
            // 记录日志并处理异常
            System.err.println("Error occurred while fetching patient test results: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void publishArticle(String doctorId, HealthArticleRequestDTO articleDTO) {
        healthArticleMapper.insertHealthArticle(
                doctorId,
                articleDTO.getTitle(),
                articleDTO.getContent(),
                articleDTO.getTags(),
                articleDTO.getDoctor_name()
        );
    }


}
