import request from '@/utils/request';

// 注册接口调用函数
export const userRegisterService = (registerData) => {
    return request.post('/api/user/register', registerData, {
        headers: {
            'Content-Type': 'application/json', // 使用 JSON 格式
        },
    }).then(response => {
        return response.data; // 返回后端响应的数据部分
    }).catch(error => {
        console.error("请求错误：", error);
        throw error; // 抛出错误，供调用方处理
    });
};


// 登录接口调用函数
export const userLoginService = (loginData) => {
    return request.post('/api/user/login', loginData, {
        headers: {
            'Content-Type': 'application/json', // 使用 JSON 格式
        },
    }).then(response => {
        return response.data; // 返回后端响应的数据部分
    }).catch(error => {
        console.error("请求错误：", error);
        throw error; // 抛出错误，供调用方处理
    });
};




