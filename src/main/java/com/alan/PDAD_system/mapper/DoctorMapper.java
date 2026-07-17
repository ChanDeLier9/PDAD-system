package com.alan.PDAD_system.mapper;
import com.alan.PDAD_system.dto.*;
import com.alan.PDAD_system.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DoctorMapper {

    @Select("SELECT " +
            "u.email AS email, " +
            "u.id_number AS idNumber, " +
            "u.age AS age, " +
            "u.gender AS gender, " +
            "d.doctorName As doctorName,"+
            "d.phone AS phone, " +
            "d.experience AS experience " +
            "FROM user u " +
            "JOIN doctor d ON u.userId = d.doctorId " +
            "WHERE d.doctorId = #{doctorId}")
    DoctorDTO getDoctorInfoById(@Param("doctorId") String doctorId);


    // 根据 doctorId 查找医生
    @Select("SELECT * FROM doctor WHERE doctorId = #{doctorId}")
    Doctor findByDoctorId(@Param("doctorId") String doctorId);


    @Update("UPDATE doctor " +
            "SET doctorName=#{doctorName},phone = #{phone}, experience = #{experience}, updated_at = now() " +
            "WHERE doctorId = #{userId}")
    int updateDoctor(@Param("userId") String userId,
                     @Param("doctorName") String doctorName,
                     @Param("phone") String phone,
                     @Param("experience") String experience);


    @Select("SELECT p.patientId, p.patientName, p.phone,p.age ," +
            "u.gender,  u.email " +
            "FROM patient p " +
            "LEFT JOIN user u ON p.patientId = u.userId " +
            "WHERE p.patientName LIKE CONCAT('%', #{patientName}, '%')")
    List<PatientDTO> findPatientsByName(@Param("patientName") String patientName);


    @Select("SELECT " +
            "patientId , " +
            "patientName," +
            "diagnosis, " +
            "treatment " +
            "FROM medicalrecord " +
            "WHERE patientName LIKE CONCAT('%', #{patientName}, '%')")
    List<MedicalRecordDTO> findByPatientName(@Param("patientName") String patientName);

    // 查询与医生相关的所有患者
    @Select("SELECT " +
            "mr.patientId AS patientId, " +
            "mr.patientName AS patientName, " +
            "mr.diagnosis AS diagnosis, " +
            "mr.treatment AS treatment " +
            "FROM medicalrecord mr " +
            "WHERE mr.doctorId = #{doctorId}")
    List<MedicalRecordDTO> findPatientsByDoctorId(@Param("doctorId") String doctorId);



    @Select("SELECT " +
            "p.patientName, " +
            "p.age, " +
            "p.phone, " +
            "u.gender, " +
            "u.email " +
            "FROM patient p " +
            "JOIN user u ON p.patientId = u.userId " +
            "WHERE p.patientId = #{patientId}")
    PatientDTO findPatientByDoctorAndPatientId(@Param("doctorId") String doctorId, @Param("patientId") String patientId);





    @Select("SELECT tr.patientId, tr.result_id, tr.scores, tr.suggestions, tr.scaleID, s.ScaleName "+
            "FROM test_result tr " +
            "JOIN scales s ON tr.scaleId = s.scale_id " +
            "WHERE tr.patientId = #{patientId} ")
    List<TestResultDTO> findTestResultsByDoctorAndPatientId(@Param("doctorId") String doctorId, @Param("patientId") String patientId);

    @Select("SELECT doctorId AS userId, doctorName AS username FROM doctor WHERE doctorName = #{name}")
    UserSimpleDTO findDoctorByExactName(@Param("name") String name);


}
