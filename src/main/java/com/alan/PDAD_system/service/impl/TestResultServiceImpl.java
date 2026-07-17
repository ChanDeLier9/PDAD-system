package com.alan.PDAD_system.service.impl;

import com.alan.PDAD_system.dto.TestResultResponse;
import com.alan.PDAD_system.entity.TestResult;
import com.alan.PDAD_system.mapper.PatientMapper;
import com.alan.PDAD_system.mapper.TestResultMapper;
import com.alan.PDAD_system.service.TestResultService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Mapper

@Service
public class TestResultServiceImpl implements TestResultService {
    private final TestResultMapper testResultMapper;
    private final PatientMapper patientMapper;
    @Autowired
    public TestResultServiceImpl(PatientMapper patientMapper, TestResultMapper testResultMapper) {
        this.patientMapper = patientMapper;
        this.testResultMapper = testResultMapper;}

    @Override
    public BigDecimal getTestScoreByScaleId(String patientId, Integer scaleId) {
        return patientMapper.getTestScoreByPatientIdAndScaleId(patientId, scaleId);
    }
    @Override
    public boolean updateDiagnosis(String patientId, Integer scaleId, String diagnosis) {
        return patientMapper.updateTestDiagnosis(patientId, scaleId, diagnosis) > 0;
    }























    // 获取测试结果并生成建议
    public TestResultResponse getTestResultAndSuggestions(Integer patientId, Integer resultId) {
        // 获取测试结果
        TestResult testResult = testResultMapper.findById(resultId);

        // 判断测试结果是否存在且是当前患者的结果

        // 返回 null 表示未找到符合条件的测试结果
        return null;
    }

    // 根据分数生成建议
    private String generateSuggestion(BigDecimal score) {
        int scoreInt = score.intValue();  // 转换为整数进行区间匹配

        // 查找适合该分数的建议
      //  ScoreSuggestion scoreSuggestion = scoreSuggestionMapper.findSuggestionByScore(scoreInt);
        /*if (scoreSuggestion != null) {
            return scoreSuggestion.getSuggestion();
        }
*/
        // 默认建议
        return "无建议，具体情况请咨询医生。";
    }

}
