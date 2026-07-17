import request from '@/utils/request';
import axios from 'axios';
import instance from "@/utils/request";

// 更新医生信息
export const updateDoctorInfoService = (doctorId, doctorDetails) => {
    console.log(`调用更新接口 URL: doctors/${doctorId}/info`);
    console.log("传递的医生信息:", doctorDetails);

    return request.put(`/doctors/${doctorId}/info`, doctorDetails)
        .catch(error => {
            console.error("请求更新接口失败:", error);
            throw error; // 将错误抛出，进入 `catch` 块
        });
};


// 获取医生信息
export const getDoctorInfoService = (doctorId) => {
    return instance.get(`/doctors/${doctorId}/info`);
};

// 获取医生的患者列表和相关记录
export const getPatientsWithRecordsService = async (doctorId) => {
    try {
        const response = await axios.get(`/doctors/${doctorId}/patients`);
        return response.data;  // 假设返回的结构为 { code: 0, data: [...] }
    } catch (error) {
        console.error('获取患者列表失败', error);
        return { code: -1, message: '获取患者列表失败' };  // 处理失败时的返回结构
    }
};

// 搜索患者
export const searchPatientsByNameService = async (doctorId, patientName) => {
    try {
        const response = await axios.get(`/doctors/${doctorId}/patients/search`, {
            params: { patientName }
        });
        return response.data;  // 假设返回的结构为 { code: 0, data: [...] }
    } catch (error) {
        console.error('根据名字搜索患者失败', error);
        return { code: -1, message: '根据名字搜索患者失败' };  // 处理失败时的返回结构
    }
};

// 获取患者的病历信息
export const getMedicalRecordService = async (doctorId, patientName) => {
    try {
        const response = await axios.get(`/doctors/${doctorId}/patients/medical-record`, {
            params: { patientName }
        });
        return response.data;  // 假设返回的结构为 { code: 0, data: [...] }
    } catch (error) {
        console.error('获取患者病历失败', error);
        return { code: -1, message: '获取患者病历失败' };  // 处理失败时的返回结构
    }
};
// 添加患者病历
export const addMedicalRecordService = async (doctorId, medicalRecordRequest) => {
    try {
        const response = await axios.post(`/doctors/${doctorId}/patients/medical-record`, medicalRecordRequest);
        return response.data;  // 假设返回的结构为 { code: 0, message: '添加成功' }
    } catch (error) {
        console.error('添加患者病历失败', error);
        return { code: -1, message: '添加患者病历失败' };  // 处理失败时的返回结构
    }
};
// 删除患者病历
export const deleteMedicalRecordService = async (doctorId, patientId) => {
    try {
        const response = await axios.delete(`/doctors/${doctorId}/patients/medical-record`, {
            params: { patientId }
        });
        return response.data;  // 假设返回的结构为 { code: 0, message: '删除成功' }
    } catch (error) {
        console.error('删除患者病历失败', error);
        return { code: -1, message: '删除患者病历失败' };  // 处理失败时的返回结构
    }
};
// 更新病历治疗建议
export const updateMedicalRecordService = async (doctorId, patientId, medicalRecordUpdateRequest) => {
    try {
        const response = await axios.post(`/doctors/${doctorId}/patients/${patientId}/medical-record/diagnosis`, medicalRecordUpdateRequest);
        return response.data;  // 假设返回的结构为 { code: 0, message: '更新成功' }
    } catch (error) {
        console.error('更新病历失败', error);
        return { code: -1, message: '更新病历失败' };  // 处理失败时的返回结构
    }
};
// 获取患者测试结果
export const getPatientTestResultsService = async (doctorId, patientId) => {
    try {
        const response = await axios.get(`/doctors/${doctorId}/patients/${patientId}/test-results`);
        return response.data;  // 假设返回的结构为 { code: 0, data: [...] }
    } catch (error) {
        console.error('获取患者测试结果失败', error);
        return { code: -1, message: '获取患者测试结果失败' };  // 处理失败时的返回结构
    }
};
// 获取患者影像分析结果
export const getPatientImageAnalysisService = async (doctorId, patientId) => {
    try {
        const response = await axios.get(`/doctors/${doctorId}/patients/${patientId}/image-analysis`);
        return response.data;  // 假设返回的结构为 { code: 0, data: [...] }
    } catch (error) {
        console.error('获取患者影像分析结果失败', error);
        return { code: -1, message: '获取患者影像分析结果失败' };  // 处理失败时的返回结构
    }
};

