package com.alan.PDAD_system.controller;

import com.alan.PDAD_system.dto.*;
import com.alan.PDAD_system.entity.ImageAnalysisResult;
import com.alan.PDAD_system.entity.MedicalRecord;
import com.alan.PDAD_system.service.DoctorService;
import com.alan.PDAD_system.service.impl.MedicalRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*@RequestMapping("/user")注解表示该控制器处理所有以
  /user为前缀的URL请求。*/
@RestController
@RequestMapping("/doctors")  //用户功能的基本路径为：/doctors
@Validated
public class DoctorController {
    private final DoctorService doctorService;
    private final MedicalRecordService medicalRecordService;
    public DoctorController(DoctorService doctorService, MedicalRecordService medicalRecordService) {
        this.doctorService = doctorService;
         this.medicalRecordService = medicalRecordService;
    }
    //获取医生的职业信息
    @GetMapping("/{doctorId}/info")
    public ResponseEntity<Result<Object>> getDoctorInfo(@PathVariable String doctorId) {
        System.out.println("接收到的医生 ID: " + doctorId);
        DoctorDTO doctorInfo = doctorService.getDoctorInfoById(doctorId);
        if (doctorInfo != null) {
            return ResponseEntity.ok(Result.success(doctorInfo)); // 返回 HTTP 200 和详细信息
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error("医生信息不存在")); // 返回 HTTP 404
        }
    }


    // 更新医生的职业生涯等信息
    @PutMapping("/{doctorId}/info")
    public Result updateDoctorInfo(@PathVariable String doctorId, @RequestBody DoctorDTO doctorDetails) {
        boolean success = doctorService.updateDoctorInfo(doctorId, doctorDetails);
        if (success) {
            return Result.success("医生信息更新成功！");
        } else {
            return Result.error("更新失败，请稍后重试！");
        }
    }

    // 医生按名字从患者表中查找患者
    @GetMapping("/{doctorId}/patients/search")
    public Result<List<PatientDTO>> searchPatientsByName(@PathVariable String doctorId, @RequestParam String patientName) {
        // 调用 Service 层获取患者列表
        List<PatientDTO> patients = doctorService.getPatientsByDoctorIdAndName(patientName);

        // 检查查询结果
        if (patients != null && !patients.isEmpty()) {
            return Result.success(patients);  // 返回成功查询的患者列表
        } else {
            return Result.error("未找到相关患者信息！");  // 返回查询失败的错误信息
        }
    }
    // 根据患者名字从mr表查询病历信息
    @GetMapping("/{doctorId}/patients/medical-record")
    public Result<List<MedicalRecordDTO>> getMedicalRecord(@PathVariable String doctorId,
                                                     @RequestParam String patientName) {
        // 调用 Service 层查询病历
        List<MedicalRecordDTO> medicalRecord = doctorService.getMedicalRecordByName(patientName);

        if (medicalRecord != null) {
            // 返回查询到的病历信息
            return Result.success(medicalRecord);
        } else {
            // 如果没有找到病历记录，返回患者不存在的提示
            return Result.error("患者不存在");
        }
    }
    // 查询与医生相关的所有患者
    @GetMapping("/{doctorId}/patients")
    public Result<List<MedicalRecordDTO>> getPatientsWithRecords(@PathVariable String doctorId) {
        // 调用 Service 层查询与医生相关的患者
        List<MedicalRecordDTO> patientsWithRecords = doctorService.getPatientsByDoctorId(doctorId);

        // 检查查询结果是否为空
        if (patientsWithRecords != null && !patientsWithRecords.isEmpty()) {
            return Result.success(patientsWithRecords);
        } else {
            return Result.error("未找到相关患者信息！");
        }
    }

    // 添加患者病历
    @PostMapping("/{doctorId}/patients/medical-record")
    public Result addMedicalRecord(@PathVariable String doctorId,
                                   @RequestBody MedicalRecordRequest medicalRecordRequest) {
        try {
            // 获取患者 ID
            String patientId = medicalRecordRequest.getPatientId();
            System.out.println("医生 ID: " + doctorId);
            System.out.println("患者 ID: " + patientId);
            // 检查患者是否已经有病历
            List<MedicalRecord>  existingRecord = medicalRecordService.findMedicalRecordByPatientIdAndDoctorId(patientId, doctorId);

            if (existingRecord != null && !existingRecord.isEmpty()) {
                return Result.error("该患者已经有病历，无法重复添加！");
            }

            // 直接使用请求参数中的患者信息，跳过查询步骤
            // 这里我们假设 medicalRecordRequest 中包含了 patientId 和患者的基本信息

            // 调用 Service 层将患者病历信息插入到 medicalrecord 表
            medicalRecordService.addMedicalRecord(doctorId, medicalRecordRequest);

            return Result.success("患者病历添加成功！");
        } catch (Exception e) {
            return Result.error("添加病历失败，" + e.getMessage());
        }
    }
//删除患者病历
    @DeleteMapping("/{doctorId}/patients/medical-record")
    public Result deleteMedicalRecord(@PathVariable String doctorId,
                                      @RequestParam String patientId) {
        try {
            // 调用 Service 层删除该患者的病历记录
            doctorService.deleteMedicalRecord(patientId);
            return Result.success("患者病历删除成功！");
        } catch (Exception e) {
            return Result.error("删除病历失败：" + e.getMessage());
        }
    }

    @GetMapping("/{doctorId}/patients/{patientId}/medical-record")
    public Result<Map<String, Object>> getOldMedicalRecord(@PathVariable String doctorId, @PathVariable String patientId) {

        try {
            // 调用 Service 层获取诊断和治疗建议
            MedicalRecord medicalRecord = doctorService.getMedicalRecordByDoctorIdAndPatientId(doctorId, patientId);

            if (medicalRecord == null) {
                return Result.error("未找到该患者的病历记录");
            }

            // 将结果封装到 Map 中
            Map<String, Object> result = new HashMap<>();
            result.put("diagnosis", medicalRecord.getDiagnosis());
            result.put("treatment", medicalRecord.getTreatment());

            return Result.success("病历查询成功", result);
        } catch (Exception e) {
            return Result.error("获取病历失败：" + e.getMessage());
        }
    }

    //填写治疗建议
    @PostMapping("/{doctorId}/patients/{patientId}/medical-record/diagnosis")
    public Result updateMedicalRecord(@PathVariable String doctorId,
                                      @PathVariable String patientId,  // 使用 patientId 进行唯一标识
                                      @RequestBody MedicalRecordUpdateRequest medicalRecordUpdateRequest) {
        try {
            // 调用 Service 层更新病历记录
            doctorService.updateMedicalRecord(doctorId, patientId, medicalRecordUpdateRequest);

            return Result.success("病历更新成功！");
        } catch (Exception e) {
            return Result.error("更新病历失败：" + e.getMessage());
        }

    }




    //获取特定患者的测试结果
    @GetMapping("/{doctorId}/patients/{patientId}/test-results")
    public Result getPatientTestResults(@PathVariable String doctorId, @PathVariable String patientId) {
        try {
            // 调用 Service 层获取患者的测试结果
            List<TestResultDTO> testResults = doctorService.getPatientTestResultsByDoctor(doctorId, patientId);
            if (testResults != null && !testResults.isEmpty()) {
                return Result.success(testResults);
            } else {
                return Result.error("未找到该患者的测试结果，或您无权限查看该患者的信息！");
            }
        } catch (Exception e) {
            return Result.error("获取患者测试结果时发生错误：" + e.getMessage());
        }
    }



    //获取指定患者的基本信息
    @GetMapping("/{doctorId}/patients/{patientId}/info")
    public Result getPatientInfo(@PathVariable String doctorId, @PathVariable String patientId) {
        try {
            // 通过 Service 层调用获取患者信息
            PatientDTO patient = doctorService.getPatientInfoByDoctor(doctorId, patientId);
            if (patient != null) {
                return Result.success(patient);
            } else {
                return Result.error("未找到该患者的信息，或您无权限查看该患者的信息！");
            }
        } catch (Exception e) {
            return Result.error("获取患者信息时发生错误：" + e.getMessage());
        }
    }

     //要新加的

// 查看特定患者的头部影像分析结果（医生端：结果 + 图片）
     @GetMapping("/{doctorId}/patients/{patientId}/image-analysis")
     public Result getPatientImageAnalysis(@PathVariable String doctorId,
                                           @PathVariable String patientId) {
         try {
             // 1) 调用 Service 获取最新一条影像分析记录
             ImageAnalysisResult entity =
                     doctorService.getPatientImageAnalysisByDoctor( patientId);

             if (entity == null) {
                 return Result.error("未找到该患者的头部影像分析结果");
             }

             // 2) 组装 DTO：分析结果 + 图片 Base64
             ImageAnalysisWithImageDTO dto = new ImageAnalysisWithImageDTO();
             dto.setImageId(entity.getImageId());
             dto.setPatientId(entity.getPatientId());
             dto.setUpdateTime(entity.getUpdateTime());
             dto.setAnalysisResult(entity.getAnalysisResult());

             // 3) 把 BLOB 转成 Base64 字符串
             if (entity.getImageData() != null && entity.getImageData().length > 0) {
                 String base64 = Base64.getEncoder().encodeToString(entity.getImageData());
                 dto.setImageBase64(base64);

                 // 你现在存的是 TIF，就先写死；以后如果存 PNG/JPG，可以扩展字段
                 dto.setImageContentType("image/tiff");
             }

             // 4) 用你的 Result.success 封装返回
             return Result.success(dto);

         } catch (Exception e) {
             e.printStackTrace();
             return Result.error("获取患者头部影像分析结果时发生错误：" + e.getMessage());
         }
     }

    //发布医疗知识文章
    @PostMapping("/{doctorId}/articles")
    public Result<String> publishArticle(@PathVariable String doctorId,
                                         @RequestBody HealthArticleRequestDTO articleDTO) {
        try {
            doctorService.publishArticle(doctorId, articleDTO);
            return Result.success("文章发布成功！");
        } catch (Exception e) {
            return Result.error("文章发布失败：" + e.getMessage());
        }
    }



}
