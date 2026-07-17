package com.alan.PDAD_system.mapper;

import com.alan.PDAD_system.dto.MedicalRecordDTO;
import com.alan.PDAD_system.dto.PatientDTO;
import com.alan.PDAD_system.dto.QuestionAnswerDTO;
import com.alan.PDAD_system.dto.UserSimpleDTO;
import com.alan.PDAD_system.entity.Patient;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface PatientMapper {
    // 根据 patientId 查询患者姓名
    @Select("SELECT patientName FROM patient WHERE patientId = #{patientId}")
    String findPatientById(@Param("patientId") String patientId);


    @Select("SELECT " +
            "u.email AS email, " +
            "p.age AS age, " +
            "u.id_number AS IdNumber, " +
            "u.gender AS gender, " +
            "p.patientName AS patientName, " +
            "p.phone AS phone " +  // ← 这里的逗号不能要！
            "FROM user u " +
            "JOIN patient p ON u.userId = p.patientId " +
            "WHERE p.patientId = #{patientId}")
    PatientDTO getPatientInfoById(@Param("patientId") String patientId);


    @Update("""
    UPDATE patient p
    JOIN user u ON p.patientId = u.userId
    SET p.patientName = #{patientName},
        p.phone = #{phone},
        p.age = #{age},
        u.age = #{age},
        p.updatedTime = NOW()
    WHERE p.patientId = #{userId}
""")
    int updatePatient(@Param("userId") String userId,
                      @Param("patientName") String patientName,
                      @Param("phone") String phone,
                      @Param("age") int age);


    @Select("SELECT scores " +
            "FROM test_result " +
            "WHERE patientId = #{patientId} AND scaleId = #{scaleId}")
    BigDecimal getTestScoreByPatientIdAndScaleId(@Param("patientId") String patientId, @Param("scaleId") Integer scaleId);



    @Update("UPDATE test_result " +
            "SET suggestions = #{diagnosis} " +
            "WHERE patientId = #{patientId} AND scaleId = #{scaleId}")
    int updateTestDiagnosis(
            @Param("patientId") String patientId,
            @Param("scaleId") Integer scaleId,
            @Param("diagnosis") String diagnosis);

    @Update("UPDATE patient " +
            "SET finalScore = #{finalScore}, finalResult = #{finalResult} " +
            "WHERE patientId = #{patientId}")
    int updatePatientDiagnosisAndScore(@Param("patientId") String patientId,
                                       @Param("finalScore") BigDecimal finalScore,
                                       @Param("finalResult") String finalResult);

    @Select("""
            SELECT
                q.question_id AS questionId,
                q.question_text AS questionContent,
                ao.option_id AS optionId,
                ao.option_text AS optionContent,
                ao.score AS optionScore,
                s.ScaleName AS optionScaleName
            FROM
                patient_answer pa
            JOIN
                 scales s ON s.scale_id = #{scaleId}
            JOIN
                questions q ON pa.question_id = q.question_id AND q.scale_id = #{scaleId}
            JOIN
                answer_option ao ON pa.question_id = ao.question_id AND pa.option_id = ao.option_id
            WHERE
                pa.patient_id = #{patientId}
            """)
    List<QuestionAnswerDTO> findAnswersByPatientAndScale(
            @Param("patientId") String patientId,
            @Param("scaleId") Integer scaleId);

    @Select("SELECT " +
            "patientId , " +
            "patientName," +
            "diagnosis, " +
            "treatment " +
            "FROM medicalrecord " +
            "WHERE patientName LIKE CONCAT('%', #{patientName}, '%')")
    List<MedicalRecordDTO> findByPatientName(@Param("patientName") String patientName);


    @Select("SELECT * FROM patient WHERE patientId = #{patientId}")
    Patient findByPatientId(@Param("patientId") String patientId);
    @Select("""
        SELECT
            patientId,
            patientName,
            diagnosis,
            treatment
        FROM medicalrecord
        WHERE patientId = #{patientId}
          AND patientName LIKE CONCAT('%', #{patientName}, '%')
        """)
    List<MedicalRecordDTO> findByNameAndId(@Param("patientId") String patientId,
                                           @Param("patientName") String patientName);





    @Select("SELECT patientId AS userId, patientName AS username FROM patient WHERE patientName = #{name}")
    UserSimpleDTO findPatientByExactName(@Param("name") String name);


    @Insert("""
    REPLACE INTO patient_answer (patient_id, question_id, option_id)
    VALUES (#{patientId}, #{questionId}, #{optionId})
""")
    void insertAnswer(@Param("patientId") String patientId,
                       @Param("questionId") Integer questionId,
                       @Param("optionId") Integer optionId);
    // 删除某用户在某张量表上的所有答题记录
    @Delete("""
        DELETE FROM patient_answer
        WHERE patient_id = #{patientId}
          AND question_id IN (
              SELECT question_id FROM questions WHERE scale_id = #{scaleId}
          )
    """)
    void deleteAnswersByPatientAndScale(@Param("patientId") String patientId,
                                        @Param("scaleId") Integer scaleId);
}
