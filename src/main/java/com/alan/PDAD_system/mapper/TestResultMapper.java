package com.alan.PDAD_system.mapper;
import com.alan.PDAD_system.entity.*;
import org.apache.ibatis.annotations.*;

@Mapper
public interface TestResultMapper {
    // 查询某个测试结果
    @Select("SELECT * FROM test_result WHERE result_id = #{resultId}")
    TestResult findById(Integer resultId);

    // 插入新的测试结果
    @Insert("INSERT INTO test_result (patientId, scaleId, scores) " +
            "VALUES (#{patientId}, #{scaleId}, #{score})")
    @Options(useGeneratedKeys = true, keyProperty = "resultId")
    void insert(TestResult testResult);

    // 更新测试结果
    @Update("UPDATE test_result SET scores = #{score} WHERE result_id = #{resultId}")
    void update(TestResult testResult);




}
