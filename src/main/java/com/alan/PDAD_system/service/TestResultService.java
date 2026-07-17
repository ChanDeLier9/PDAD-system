package com.alan.PDAD_system.service;
import com.alan.PDAD_system.dto.TestResultResponse;

import java.math.BigDecimal;

public interface TestResultService {
    BigDecimal getTestScoreByScaleId(String patientId, Integer scaleId);

    boolean updateDiagnosis(String patientId, Integer scaleId, String diagnosis);




    TestResultResponse getTestResultAndSuggestions(Integer patientId, Integer resultId);
}
