// login.js - 部署版（不写死 localhost）
// 接口统一走 /api，由 Nginx 反代到后端 Spring Boot
const API_BASE_URL = '/api';

// 注册规则
const registerRules = {
    userId: [
        { required: true, message: '请输入用户ID(3到6位)', pattern: /^[A-Za-z\d]{3,6}$/, errorId: 'errorUserId' },
    ],
    password: [
        { required: true, message: '请输入密码(至少包含一个大写字母、小写字母和数字)', pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,15}$/, errorId: 'errorPassword' },
    ],
    email: [
        { required: true, message: '请输入正确格式的邮箱', pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, errorId: 'errorEmail' },
    ],
    idNumber: [
        { required: true, message: '请输入身份证号', pattern: /^[0-9]{15,18}$/, errorId: 'errorIdNumber' },
    ],
    role: [
        { required: true, message: '请选择角色', errorId: 'errorRole' },
    ],
    age: [
        { required: true, message: '请输入年龄', pattern: /^(?:1[0-9]|[2-9][0-9]|100)$/, errorId: 'errorAge' },
    ],
    gender: [
        { required: true, message: '请选择性别', errorId: 'errorGender' },
    ],
};

// ===== 给 HTML onclick 用：必须挂 window =====
window.showRegisterForm = function () {
    document.getElementById('login')?.classList.add('hidden');
    document.getElementById('register')?.classList.remove('hidden');
};

window.showLoginForm = function () {
    document.getElementById('register')?.classList.add('hidden');
    document.getElementById('login')?.classList.remove('hidden');
};

// 验证表单字段
function validateField(value, rules) {
    for (let rule of rules) {
        const errorElement = document.getElementById(rule.errorId);
        if (!errorElement) continue;

        if (rule.required && !value) {
            errorElement.textContent = rule.message;
            return false;
        }
        if (rule.pattern && !rule.pattern.test(value)) {
            errorElement.textContent = rule.message;
            return false;
        }
        errorElement.textContent = '';
    }
    return true;
}

// 检查所有字段的有效性 -> 控制注册按钮 enable
function checkFormValidity() {
    const userId = document.getElementById('registerUserId')?.value ?? '';
    const password = document.getElementById('registerPassword')?.value ?? '';
    const email = document.getElementById('registerEmail')?.value ?? '';
    const idNumber = document.getElementById('registerIdNumber')?.value ?? '';
    const role = document.getElementById('registerRole')?.value ?? '';
    const age = document.getElementById('registerAge')?.value ?? '';
    const gender = document.getElementById('registerGender')?.value ?? '';

    const isUserIdValid = validateField(userId, registerRules.userId);
    const isPasswordValid = validateField(password, registerRules.password);
    const isEmailValid = validateField(email, registerRules.email);
    const isIdNumberValid = validateField(idNumber, registerRules.idNumber);
    const isRoleValid = validateField(role, registerRules.role);
    const isAgeValid = validateField(age, registerRules.age);
    const isGenderValid = validateField(gender, registerRules.gender);

    const submitBtn = document.getElementById('registerBtn');
    if (submitBtn) {
        submitBtn.disabled = !(
            isUserIdValid &&
            isPasswordValid &&
            isEmailValid &&
            isIdNumberValid &&
            isRoleValid &&
            isAgeValid &&
            isGenderValid
        );
    }
}

// 登录
window.login = async function () {
    const doctorId = document.getElementById('loginUserId')?.value ?? '';
    const password = document.getElementById('loginPassword')?.value ?? '';

    if (!doctorId || !password) {
        alert('请输入账号和密码');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/user/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ doctorId, password }),
        });

        const data = await response.json().catch(() => ({}));

        if (response.ok && data.code === 0) {
            alert('登录成功');
            localStorage.setItem('token', data.data?.token ?? '');
            localStorage.setItem('doctorId', data.data?.userId ?? '');

            // ✅ 同目录跳转：预览/部署都能用
            location.href = './mine.html';
        } else {
            alert(data.message || '登录失败，请稍后重试');
        }
    } catch (error) {
        console.error('登录过程中出现错误:', error);
        alert('登录失败，请检查网络连接或稍后重试！');
    }
};

// 注册
window.register = async function () {
    const userId = document.getElementById('registerUserId')?.value ?? '';
    const password = document.getElementById('registerPassword')?.value ?? '';
    const email = document.getElementById('registerEmail')?.value ?? '';
    const idNumber = document.getElementById('registerIdNumber')?.value ?? '';
    const role = document.getElementById('registerRole')?.value ?? '';
    const age = document.getElementById('registerAge')?.value ?? '';
    const gender = document.getElementById('registerGender')?.value ?? '';

    // 验证每个字段
    const isUserIdValid = validateField(userId, registerRules.userId);
    const isPasswordValid = validateField(password, registerRules.password);
    const isEmailValid = validateField(email, registerRules.email);
    const isIdNumberValid = validateField(idNumber, registerRules.idNumber);
    const isRoleValid = validateField(role, registerRules.role);
    const isAgeValid = validateField(age, registerRules.age);
    const isGenderValid = validateField(gender, registerRules.gender);

    if (!isUserIdValid || !isPasswordValid || !isEmailValid || !isIdNumberValid || !isRoleValid || !isAgeValid || !isGenderValid) {
        alert('请正确填写表单数据！');
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/user/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                userId,
                password,
                email,
                idNumber,
                role,
                age,
                gender,
            }),
        });

        const data = await response.json().catch(() => ({}));

        if (response.ok && data.code === 0) {
            alert('注册成功');
            window.showLoginForm();
        } else {
            alert(data.message || '注册失败，请稍后重试');
        }
    } catch (error) {
        console.error('注册过程中出现错误:', error);
        alert('注册失败，请检查网络连接或稍后重试！');
    }
};

// 绑定实时验证事件（等 DOM 有了再绑，避免 null 报错）
document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('registerUserId')?.addEventListener('input', checkFormValidity);
    document.getElementById('registerPassword')?.addEventListener('input', checkFormValidity);
    document.getElementById('registerEmail')?.addEventListener('input', checkFormValidity);
    document.getElementById('registerIdNumber')?.addEventListener('input', checkFormValidity);
    document.getElementById('registerRole')?.addEventListener('change', checkFormValidity);
    document.getElementById('registerAge')?.addEventListener('input', checkFormValidity);
    document.getElementById('registerGender')?.addEventListener('change', checkFormValidity);

    // 初始化一次按钮状态
    checkFormValidity();
});
