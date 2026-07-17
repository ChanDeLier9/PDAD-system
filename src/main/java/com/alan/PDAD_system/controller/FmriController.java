package com.alan.PDAD_system.controller;

import com.alan.PDAD_system.dto.*;
import com.alan.PDAD_system.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fmri")
public class FmriController {

    @Value("${ml.base-url}")
    private String mlBaseUrl;

    private final RestTemplate restTemplate;
    private final PatientService patientService;

    @Autowired
    public FmriController(RestTemplate restTemplate,
                          PatientService patientService) {
        this.restTemplate = restTemplate;
        this.patientService = patientService;
    }
    /**
     * 单张 fMRI 预测：前端上传 file，转发到 Python /predict-fmri
     */
    @PostMapping("/predict")
    public Result predict(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请上传 TIF/TIFF 图像文件");
        }

        // 组装发往 Python 的 multipart/form-data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", resource);
        } catch (IOException e) {
            return Result.error("读取上传文件失败: " + e.getMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        String url = mlBaseUrl + "/predict-fmri";
        try {
            ResponseEntity<PredictFmriResponseDTO> resp =
                    restTemplate.postForEntity(url, requestEntity, PredictFmriResponseDTO.class);

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                return Result.error("调用模型服务失败，HTTP状态：" + resp.getStatusCodeValue());
            }

            PredictFmriResponseDTO pythonDto = resp.getBody();

            // 转成给前端看的 DTO（驼峰命名，更干净）
            PredictResultDTO dto = new PredictResultDTO();
            dto.setLabel(pythonDto.getLabel());
            dto.setResult(pythonDto.getResult());
            dto.setProbability(pythonDto.getProbability());
            dto.setReliability(pythonDto.getReliability());
            dto.setThreshold(pythonDto.getThreshold());
            dto.setDiagnosis(pythonDto.getDiagnosis());
            dto.setRiskLevel(pythonDto.getRisk_level());
            dto.setModelInfo(pythonDto.getModel_info());

            return Result.success(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("调用模型服务异常: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    /**
     * 患者使用：上传 fMRI，调用 Python，并把结果写入 image_results 表
     */
    @PostMapping("/patient/predict")
    public Result patientPredict(@RequestPart("file") MultipartFile file,
                                 @RequestParam("patientId") String patientId) {
        if (file == null || file.isEmpty()) {
            return Result.error("请上传 TIF/TIFF 图像文件");
        }

        if (patientId == null || patientId.isEmpty()) {
            return Result.error("缺少患者编号 patientId");
        }

        try {
            System.out.println("上传成功");
            // 1) 调 Python 模型（和沙盒接口一样的逻辑，可以抽成私有方法，这里直接写）
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            body.add("file", resource);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            String url = mlBaseUrl + "/predict-fmri";

            ResponseEntity<PredictFmriResponseDTO> resp =
                    restTemplate.postForEntity(url, requestEntity, PredictFmriResponseDTO.class);

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                return Result.error("调用模型服务失败，HTTP状态：" + resp.getStatusCodeValue());
            }

            PredictFmriResponseDTO pythonDto = resp.getBody();

            // 2) 转成前端/业务使用的 DTO
            PredictResultDTO dto = new PredictResultDTO();
            dto.setLabel(pythonDto.getLabel());
            dto.setResult(pythonDto.getResult());
            dto.setProbability(pythonDto.getProbability());
            dto.setReliability(pythonDto.getReliability());
            dto.setThreshold(pythonDto.getThreshold());
            dto.setDiagnosis(pythonDto.getDiagnosis());
            dto.setRiskLevel(pythonDto.getRisk_level());
            dto.setModelInfo(pythonDto.getModel_info());

            // 3) 调用 service 入库：把图片 + AI 结果写入 image_results
            patientService.saveImageAnalysisResult(
                    patientId,
                    file.getBytes(),
                    dto
            );

            // 4) 返回结果给前端
            return Result.success(dto);
        } catch (IOException e) {
            return Result.error("读取上传文件失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("调用模型服务异常: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    /**
     * 模型训练：前端传 JSON，转发到 Python /train-model
     */
    @PostMapping("/train")
    public Result train(@RequestBody TrainModelRequestDTO req) {
        // 把驼峰 DTO 转成 Python 需要的下划线参数
        Map<String, Object> pyReq = new HashMap<>();
        pyReq.put("model_type", req.getModelType());
        pyReq.put("params", req.getParams() == null ? new HashMap<>() : req.getParams());
        pyReq.put("train_ratio", req.getTrainRatio() == null ? 0.8 : req.getTrainRatio());
        pyReq.put("random_state", req.getRandomState() == null ? 2025 : req.getRandomState());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(pyReq, headers);

        String url = mlBaseUrl + "/train-model";
        try {
            ResponseEntity<TrainModelResponseDTO> resp =
                    restTemplate.postForEntity(url, entity, TrainModelResponseDTO.class);

            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                return Result.error("调用训练服务失败，HTTP状态：" + resp.getStatusCodeValue());
            }

            // 这里直接把 Python 返回的结构给前端，用来画曲线+展示指标
            return Result.success(resp.getBody());
        } catch (Exception e) {
            return Result.error("调用训练服务异常: " + e.getMessage());
        }
    }
}
