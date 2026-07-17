package com.alan.PDAD_system.mapper;

import com.alan.PDAD_system.dto.DoctorAdviceDTO;
import com.alan.PDAD_system.entity.MedicalRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MedicalRecordMapper {


    // 根据患者 ID 获取所有病历记录
    @Select("SELECT * FROM medicalrecord WHERE patientId = #{patientId}")
    List<MedicalRecord> findByPatientId(@Param("patientId") String patientId);

    @Select("SELECT * FROM medicalrecord WHERE patientId = #{patientId} AND doctorId = #{doctorId}")
    List<MedicalRecord> findByPatientIdAndDoctorId(@Param("patientId") String patientId, @Param("doctorId") String doctorId);

    // 根据 doctorId 和 patientId 查询病历记录
    @Select("SELECT diagnosis, treatment FROM medicalrecord WHERE doctorId = #{doctorId} AND patientId = #{patientId}")
    MedicalRecord findMedicalRecordByDoctorIdAndPatientId(@Param("doctorId") String doctorId, @Param("patientId") String patientId);


    @Insert("INSERT INTO medicalrecord (patientId, patientName, doctorId, created_at) " +
            "VALUES (#{patientId}, #{patientName}, #{doctorId}, NOW())")
    void insertMedicalRecord(@Param("patientId") String patientId,
                             @Param("patientName") String patientName,
                             @Param("doctorId") String doctorId);

    @Delete("DELETE FROM medicalrecord WHERE patientId = #{patientId}")
    void deleteMedicalRecordByPatientId(@Param("patientId") String patientId);

    @Update("UPDATE medicalrecord " +
            "SET diagnosis = #{diagnosis}, treatment = #{treatment}, updatedTime = NOW() " +
            "WHERE doctorId = #{doctorId} AND patientId = #{patientId}")
    void updateDiagnosisAndTreatment(@Param("doctorId") String doctorId,
                                     @Param("patientId") String patientId,
                                     @Param("diagnosis") String diagnosis,
                                     @Param("treatment") String treatment);


    @Select("""
    SELECT 
        d.doctorName,
        mr.updatedTime AS time,
        mr.diagnosis,
        mr.treatment
    FROM medicalrecord mr
    JOIN doctor d ON mr.doctorId = d.doctorId
    WHERE mr.patientId = #{patientId}
    ORDER BY mr.updatedTime DESC
    """)
    List<DoctorAdviceDTO> getDoctorAdviceByPatientId(@Param("patientId") String patientId);














}
