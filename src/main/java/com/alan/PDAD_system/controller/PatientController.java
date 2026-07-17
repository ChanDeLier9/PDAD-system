package com.alan.PDAD_system.controller;

import com.alan.PDAD_system.dto.*;
import com.alan.PDAD_system.entity.HealthArticle;
import com.alan.PDAD_system.entity.ImageAnalysisResult;
import com.alan.PDAD_system.entity.Patient;
import com.alan.PDAD_system.entity.User;
import com.alan.PDAD_system.service.PatientService;
import com.alan.PDAD_system.service.TestResultService;
import com.alan.PDAD_system.service.UserService;
import com.alan.PDAD_system.service.impl.MedicalRecordService;
import com.alan.PDAD_system.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*@RequestMapping("/user")注解表示该控制器处理所有以
  /user为前缀的URL请求。*/
@RestController
@RequestMapping("/patients")  //用户功能的基本路径为：/patients
@Validated
public class PatientController {
    private final TestResultService testResultService;
    private final PatientService patientService;
    private final MedicalRecordService medicalRecordService;
    private final UserService userService;

    public PatientController(UserService userservice,  TestResultService testResultService, PatientService patientService, MedicalRecordService medicalRecordService) {
        this.userService = userservice;
        this.testResultService = testResultService;
        this.patientService = patientService;
        this.medicalRecordService = medicalRecordService;
    }

    // 患者登录接口
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Valid PatientLoginRequest patientLoginRequest) {
        // 检查用户ID是否存在于患者表中
        Patient patient = patientService.findPatientById(patientLoginRequest.getPatientId());
        if (patient == null) {
            return Result.error("用户ID不存在或用户不是患者身份！");
        }

        // 查询用户是否存在
        User loginUser = userService.findById(patientLoginRequest.getPatientId());
        if (loginUser == null) {
            return Result.error("用户不存在！");
        }

        // 校验密码是否正确
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(patientLoginRequest.getPassword(), loginUser.getPassword())) {
            return Result.error("密码错误！");
        }

        // 登录成功，生成 JWT Token
        String token = generateJwtToken(loginUser);

        // 返回 token 和 doctorId
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("doctorId", loginUser.getUserId());

        return Result.success(response);
    }

    private String generateJwtToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("role", user.getRole());
        return JwtUtil.genToken(claims, 30); // 生成30分钟有效期的JWT Token
    }

    //获取患者信息
    @GetMapping("/{patientId}/info")
    public ResponseEntity<Result<Object>> getPatientInfo(@PathVariable String patientId) {
        System.out.println(STR."接收到患者 ID: \{patientId}");
        PatientDTO doctorInfo = patientService.getPatientInfoById(patientId);
        if (doctorInfo != null) {
            return ResponseEntity.ok(Result.success(doctorInfo)); // 返回 HTTP 200 和详细信息
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error("患者信息不存在")); // 返回 HTTP 404
        }
    }

    // 更新患者的职业生涯等信息
    @PutMapping("/{patientId}/info")
    public Result updatePatientInfo(@PathVariable String patientId, @RequestBody PatientDTO patientDetails) {
        boolean success = patientService.updatePatientInfo(patientId, patientDetails);
        if (success) {
            return Result.success("患者信息更新成功！");
        } else {
            return Result.error("更新失败，请稍后重试！");
        }
    }

    //获取某张量表患者的得分
    @GetMapping("/{patientId}/test-score/{scaleId}")
    public Result<BigDecimal> getTestScore(
            @PathVariable String patientId,
            @PathVariable Integer scaleId) {
        try {
            // 验证 scaleId 是否是合法的范围
            List<Integer> validScaleIds = List.of(2, 3, 5); // 假设支持的 scaleId 列表
            if (!validScaleIds.contains(scaleId)) {
                return Result.error(STR."无效的量表 ID，仅支持：\{validScaleIds}");
            }

            // 调用服务层获取分数
            BigDecimal score = testResultService.getTestScoreByScaleId(patientId, scaleId);
            if (score != null) {
                return Result.success("获取分数成功", score);
            } else {
                return Result.error("未找到测试分数！");
            }
        } catch (Exception e) {
            return Result.error(STR."获取分数时发生错误：\{e.getMessage()}");
        }
    }

    //获取某张量表的诊断结论（该结论为量表的诊断结论）
    @GetMapping("/{patientId}/diagnosis/{scaleId}")
    public Result<String> getDiagnosis(
            @PathVariable String patientId,
            @PathVariable Integer scaleId) {
        try {

            // 1. 从服务层获取分数
            BigDecimal score = testResultService.getTestScoreByScaleId(patientId, scaleId);
            if (score == null) {
                return Result.error("未找到测试分数！");
            }

            // 2. 根据分数生成结论
            String diagnosis = getDiagnosisByScore(score,scaleId);

            // 3. 将结论存入 test_result 表
            boolean isUpdated = testResultService.updateDiagnosis(patientId, scaleId, diagnosis);
            if (!isUpdated) {
                return Result.error("更新诊断结论失败！");
            }

            // 4. 返回结论
            return Result.success("诊断成功", diagnosis);
        } catch (Exception e) {
            return Result.error(STR."获取诊断时发生错误：\{e.getMessage()}");
        }
    }

    //获取患者最后的融合得分
    @GetMapping("/{patientId}/final-diagnosis")
    public Result<Map<String, Object>> getFinalDiagnosis(@PathVariable String patientId) {
        try {

            // 1. 获取各量表的原始分数
            BigDecimal score2 = testResultService.getTestScoreByScaleId(patientId, 2);
            BigDecimal score3 = testResultService.getTestScoreByScaleId(patientId, 3);
            BigDecimal score5 = testResultService.getTestScoreByScaleId(patientId, 5);
            if (score2 == null || score3 == null || score5 == null) {
                return Result.error("未找到所有量表的测试分数！");
            }

            // 2. 计算融合分数
            BigDecimal finalScore = calculateFinalScore(score2, score3, score5);

            // 3. 获取诊断结论
            String finalResult = getUnifiedDiagnosis(finalScore);

            // 4. 获取患者姓名（假设 patientService 可以根据 patientId 获取患者信息）
            String patientName = patientService.getPatientNameById(patientId);
            if (patientName == null) {
                return Result.error("未找到患者姓名！");
            }

            // 5. 将分数和诊断存入患者表
            boolean isSaved = patientService.saveDiagnosisAndScore(patientId, finalScore, finalResult);
            if (!isSaved) {
                return Result.error("保存诊断结果失败！");
            }

            // 6. 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("finalScore", finalScore);
            result.put("finalResult", finalResult);
            result.put("patientName", patientName);  // 添加患者姓名
            System.out.println("患者姓名: " + patientName);
            System.out.println("总分: " + finalScore);
            System.out.println("结论: " + finalResult);
            return Result.success("诊断成功", result);
        } catch (Exception e) {
            return Result.error(STR."获取最终诊断时发生错误：\{e.getMessage()}");
        }
    }

    @org.jetbrains.annotations.NotNull
    private String getDiagnosisByScore(BigDecimal score, Integer scaleId) {
        if (scaleId == 2) {
            // Scale ID 2 的规则
            if (score.compareTo(BigDecimal.valueOf(20)) >= 0 && score.compareTo(BigDecimal.valueOf(35)) < 0) {
                return "无抑郁症状";
            } else if (score.compareTo(BigDecimal.valueOf(35)) >= 0 && score.compareTo(BigDecimal.valueOf(39)) <= 0) {
                return "可能有抑郁症状";
            } else if (score.compareTo(BigDecimal.valueOf(40)) >= 0) {
                return "肯定有抑郁症状，建议看心理医生";
            }
        } else if (scaleId == 3) {
            // Scale ID 3 的规则
            if (score.compareTo(BigDecimal.valueOf(9)) <= 0) {
                return "没有忧郁症 (注意自我保重)";
            } else if (score.compareTo(BigDecimal.valueOf(10)) >= 0 && score.compareTo(BigDecimal.valueOf(19)) <= 0) {
                return "可能有中度忧郁症 (最好咨询心理医生或心理医学工作者)";
            } else if (score.compareTo(BigDecimal.valueOf(20)) >= 0) {
                return "可能有重度忧郁症 (一定要看心理医生或精神科医生)";
            }
        } else if (scaleId == 5) {
            // Scale ID 5 的规则
            if (score.compareTo(BigDecimal.valueOf(16)) <= 0) {
                return "无抑郁症状";
            } else if (score.compareTo(BigDecimal.valueOf(17)) >= 0 && score.compareTo(BigDecimal.valueOf(24)) <= 0) {
                return "中度抑郁";
            } else if (score.compareTo(BigDecimal.valueOf(25)) >= 0) {
                return "重度抑郁";
            }
        }
        return "无法诊断 (未知的量表 ID 或无效的分数范围)";
    }

    private BigDecimal calculateFinalScore(BigDecimal score2, BigDecimal score3, BigDecimal score5) {

        // 1. 标准化分数
        BigDecimal normalizedScore2 = score2.subtract(BigDecimal.valueOf(20))  // 标准化 id=2
                .divide(BigDecimal.valueOf(80 - 20), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        BigDecimal normalizedScore3 = score3.subtract(BigDecimal.ZERO)         // 标准化 id=3
                .divide(BigDecimal.valueOf(27), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        BigDecimal normalizedScore5 = score5.subtract(BigDecimal.ZERO)         // 标准化 id=5
                .divide(BigDecimal.valueOf(68), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        // 2. 加权平均
        // 权重 0.4
        // 权重 0.3
        // 权重 0.3
        return normalizedScore2.multiply(BigDecimal.valueOf(0.4))  // 权重 0.4
                .add(normalizedScore3.multiply(BigDecimal.valueOf(0.3)))            // 权重 0.3
                .add(normalizedScore5.multiply(BigDecimal.valueOf(0.3)));
    }

    private String getUnifiedDiagnosis(BigDecimal finalScore) {
        if (finalScore.compareTo(BigDecimal.valueOf(25)) <= 0) {
            return "无抑郁症状";
        } else if (finalScore.compareTo(BigDecimal.valueOf(50)) <= 0) {
            return "轻度抑郁症状（建议适当自我调节）";
        } else if (finalScore.compareTo(BigDecimal.valueOf(75)) <= 0) {
            return "中度抑郁症状（建议咨询心理医生）";
        } else {
            return "重度抑郁症状（强烈建议看心理医生）";
        }
    }

    //获取特定量表的每道题选项和患者的答案
    @GetMapping("/{patientId}/scale/{scaleId}/answers")
    public Result<List<QuestionAnswerDTO>> getPatientScaleAnswers(
            @PathVariable String patientId,
            @PathVariable Integer scaleId) {
        try {
            // 调用 Service 层获取患者回答详情
            List<QuestionAnswerDTO> answers = patientService.getPatientAnswersByScale(patientId, scaleId);
            if (answers != null && !answers.isEmpty()) {
                return Result.success(answers);
            } else {
                return Result.error("未找到该患者的回答详情！");
            }
        } catch (Exception e) {
            return Result.error(STR."获取回答详情时发生错误：\{e.getMessage()}");
        }
    
    }

    //查询患者自己的相关病例
    @GetMapping("/{patientId}/medical-records/search")
    public Result<List<MedicalRecordDTO>> searchMedicalRecordsByName(@PathVariable String patientId,
                                                                     @RequestParam String patientName) {
        // 调用 Service 层查询病历
        List<MedicalRecordDTO> medicalRecord = patientService.getMedicalRecordByNameAndId(patientId, patientName);

        if (medicalRecord != null) {
            // 返回查询到的病历信息
            return Result.success(medicalRecord);
        } else {
            // 如果没有找到病历记录，返回患者不存在的提示
            return Result.error("患者不存在");
        }
    }

    //患者查询医生的诊断结论和治疗建议
    @GetMapping("/{patientId}/doctor-advice")
    public Result<List<DoctorAdviceDTO>> getDoctorAdvice(@PathVariable String patientId) {
        try {
            List<DoctorAdviceDTO> adviceList = medicalRecordService.getDoctorAdviceByPatientId(patientId);


            if (adviceList != null && !adviceList.isEmpty()) {
                System.out.println("获取的医生建议列表：");
                for (DoctorAdviceDTO advice : adviceList) {
                    System.out.println(advice);
                }

                return Result.success(adviceList);
            } else {
                return Result.error("未找到医生建议记录！");
            }
        } catch (Exception e) {
            return Result.error(STR."获取医生建议时发生错误：\{e.getMessage()}");
        }
    }

    //获取文章
    @GetMapping("/articles")
    public Result<List<HealthArticle>> getAllHealthArticles() {
        try {
            List<HealthArticle> articles = patientService.getAllHealthArticles();
            if (articles != null && !articles.isEmpty()) {
                for (HealthArticle article : articles) {
                    System.out.println(article);
                }
                return Result.success(articles);
            } else {
                return Result.error("未找到医疗知识文章。");
            }
        } catch (Exception e) {
            return Result.error(STR."获取医疗知识文章时发生错误：\{e.getMessage()}");
        }
    }

    // 清除旧数据：前端点击“开始答题”按钮时调用
    @PostMapping("/start")
    public Result<String> startAnswer(@RequestBody StartAnswerRequest request) {
        String patientId = request.getPatientId();
        Integer scaleId = request.getScaleId();
        System.out.println("成功调用接口 ，清除原有数据");
        patientService.startAnswer(patientId, scaleId);
        return Result.success("已清除旧记录，准备开始答题");
    }

    //患者做题接口
    @PostMapping("/submitScale/{scaleId}")
    public Result<String> submitScale(
            @PathVariable Integer scaleId,
            @RequestBody ScaleSubmissionRequest request) {

        // ===== 1. 验证 scaleId 是否是合法的范围 =====
        List<Integer> validScaleIds = List.of(2, 3, 5); // 可提取为常量或从数据库配置
        if (!validScaleIds.contains(scaleId)) {
            return Result.error(STR."无效的量表 ID，仅支持：\{validScaleIds}");
        }
        System.out.println("接收到的 scaleId: " + scaleId); // 控制台输出

        // ===== 2. 校验请求体中的 scaleId 是否为空或与路径参数不一致 =====
        if (request.getScaleId() == null || !scaleId.equals(request.getScaleId())) {
            return Result.error("请求体中的 scaleId 缺失或与路径参数不一致");
        }

        // ===== 3. 校验 patientId 是否为空 =====
        if (request.getPatientId() == null || request.getPatientId().isBlank()) {
            return Result.error("患者 ID 不能为空");
        }

        // ===== 4. 校验答案列表是否为空 =====
        if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
            return Result.error("答案列表不能为空");
        }

        // ===== 5. 校验每个答案项的字段完整性，并插入答案 =====
        for (AnswerDTO answer : request.getAnswers()) {
            if (answer.getQuestionId() == null) {
                return Result.error("存在未填写问题 ID 的答案项");
            }
            if (answer.getOptionId() == null) {
                return Result.error("存在未选择选项的答案项");
            }

            // 插入答案（调用 service 层）
            patientService.saveAnswer(request.getPatientId(), scaleId, answer.getQuestionId(), answer.getOptionId());
        }

        return Result.success("量表提交成功");
    }


    /**
     * 患者查看自己最新一次头部影像分析结果（图片 + 文本）
     * GET /patients/{patientId}/image-analysis
     */
    @GetMapping("/{patientId}/image-analysis")
    public Result<ImageAnalysisWithImageDTO> getMyLatestImageAnalysis(
            @PathVariable String patientId) {
        try {
            // 1) 调用 service，通过 mapper 查 DB
            ImageAnalysisResult entity = patientService.getLatestImageAnalysis(patientId);
            if (entity == null) {
                return Result.error("未找到该患者的头部影像分析结果");
            }

            // 2) 转成 DTO：文字结果 + Base64 图片
            ImageAnalysisWithImageDTO dto = new ImageAnalysisWithImageDTO();
            dto.setImageId(entity.getImageId());
            dto.setPatientId(entity.getPatientId());
            dto.setUpdateTime(entity.getUpdateTime());
            dto.setAnalysisResult(entity.getAnalysisResult());

            // 3) BLOB -> Base64
            if (entity.getImageData() != null && entity.getImageData().length > 0) {
                String base64 = Base64.getEncoder().encodeToString(entity.getImageData());
                dto.setImageBase64(base64);
                // 你现在存的是 TIF，可以先写死这个类型
                dto.setImageContentType("image/tiff");
            }

            return Result.success(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取影像分析结果时发生错误：" + e.getMessage());
        }
    }


















}





