// 导入axios和路由
import axios from 'axios';
import router from '@/router';
import {useUserStore} from "@/stores/token.js";

// 定义公共的 baseURL
const baseURL = '/api';

// 创建 axios 实例
const instance = axios.create({ baseURL });
// 请求拦截器：为每个请求添加 Token
instance.interceptors.request.use(
    config => {
        const userStore = useUserStore();
        const token = userStore.jwtToken || localStorage.getItem("jwtToken");

        if (token) {
            config.headers.Authorization = `Bearer ${token}`; // 在请求头中携带 JWT Token
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);
// 添加响应拦截器
instance.interceptors.response.use(
    result => {
        // 业务状态码为0，表示操作成功
        if (result.data.code === 0) {
            return result.data;
        }
        // 业务状态码非0，表示失败
        alert(result.data.message || '服务异常');
        return Promise.reject(result.data);
    },
    error => {
        // 处理 HTTP 错误
        if (error.response) {
            if (error.response.status === 401) {
                alert('未授权，请重新登录');
                router.push('/login').catch(() => {}); // 捕获并忽略导航错误
            } else {
                alert(error.response.data.message || '服务异常');
            }
        }
        return Promise.reject(error); // 返回错误信息
    }
);

export default instance;
