// 启用编辑模式
function enableEdit() {
    const spans = document.querySelectorAll('.second');  // 获取所有用于展示字段值的 span
    const inputs = document.querySelectorAll('.editable-input');  // 获取所有用于编辑的 input

    // 遍历所有字段，将 span 替换为 input
    spans.forEach((span, index) => {
        const value = span.innerText.trim();  // 获取当前 span 的文本内容
        const input = inputs[index];  // 获取对应的 input 元素

        input.value = value;  // 设置 input 的默认值为当前 span 的值

        // 隐藏 span，显示 input
        span.style.display = 'none';
        input.style.display = 'inline-block';
    });

    // 隐藏“修改”按钮，显示“完成”按钮
    document.getElementById('edit-button').classList.add('hidden');
    document.getElementById('save-button').classList.remove('hidden');
}

function saveChanges() {
    const inputs = document.querySelectorAll('.editable-input');
    const updatedInfo = {};

    // 字段映射：前端字段名映射到后端字段名
    const fieldMapping = {
        '姓名': 'doctorName',
        '工龄': 'experience',
        '身份证号': 'idNumber',  // 添加身份证号字段
        '电话': 'phone',
        '邮箱': 'email',
        '性别': 'gender',
        '年龄': 'age',
    };

    // 直接按映射顺序处理
    for (const label in fieldMapping) {
// 根据字段映射创建正确的input ID
        const inputId = `${fieldMapping[label]}Input`;
        const input = document.querySelector(`#${inputId}`); // 获取对应的输入框
        if (input) {
            const backendFieldName = fieldMapping[label];

            if (backendFieldName) {
                updatedInfo[backendFieldName] = input.value;
            }

            // 调试输出：查看获取的标签和输入的值
            console.log('标签名:', label, '输入值:', input.value);

            // 替换input为span元素，显示更新后的值
            const span = document.createElement('span');
            span.className = 'second';  // 使用正确的类名
            span.innerText = input.value;

            // 找到与输入框相同的字段显示区域，替换内容
            const displayElement = document.querySelector(`#${fieldMapping[label]}Display`);
            if (displayElement) {
                displayElement.replaceWith(span);
            }

            // 让输入框隐藏，显示span
            input.style.display = 'none';
            span.style.display = 'inline';
        } else {
            console.log(`未找到输入框：${inputId}`);
        }
    }
    // 假设你已经从表单中获取了这些字段
    updatedInfo.doctorName = document.querySelector("#doctorNameInput").value;
    updatedInfo.experience = document.querySelector("#experienceInput").value;
    updatedInfo.idNumber = document.querySelector("#idNumberInput").value;
    updatedInfo.phone = document.querySelector("#phoneInput").value;
    updatedInfo.email = document.querySelector("#emailInput").value;
    updatedInfo.age = Number(document.querySelector("#ageInput").value); // 确保 age 为数字类型
    updatedInfo.gender = document.querySelector("#genderInput").value;  // 确保 gender 字段存在
    // 获取医生 ID，存在 doctorId 在 localStorage 或其他地方
    const doctorId = localStorage.getItem('doctorId');
    // 添加 doctorId 到请求体中
    updatedInfo.doctorId = doctorId;
    console.log("更新的医生信息：", updatedInfo); // 调试输出
    console.log("医生ID：", doctorId); // 调试输出

    localStorage.setItem('doctorInfo', JSON.stringify(updatedInfo));

    // 发起更新请求
    fetch(`/api/doctors/${doctorId}/info`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(updatedInfo), // 发送医生更新信息
    })
        .then(response => response.json())
        .then(data => {
            console.log("后端响应数据：", data); // 调试输出
            if (data.code === 0) {
                alert('信息已保存');
            } else {
                alert('更新失败，请稍后重试');
            }
        })
        .catch(error => {
            console.error('提交更新失败:', error);
            alert('更新失败，请稍后重试');
        });

    // 隐藏“完成”按钮，显示“修改”按钮
    document.getElementById('edit-button').classList.remove('hidden');
    document.getElementById('save-button').classList.add('hidden');
}

window.onload = function () {
    // 获取存储的 doctorId
    const storedDoctorId = localStorage.getItem('doctorId');

    if (storedDoctorId) {
        // 如果 doctorId 存在，则使用它查询医生信息
        loadDoctorInfo(storedDoctorId);
    } else {
        alert("医生ID无效或未获取！");
    }
}

// 从后端获取医生信息
function loadDoctorInfo(doctorId) {
    fetch(`/api/doctors/${doctorId}/info`, {  // 使用正确的路径
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    })
        .then(response => response.json())
        .then(data => {
            console.log("医生ID：", doctorId); // 调试输出

            console.log("后端返回的医生信息：", data); // 调试输出
            if (data.code === 0) { // 成功获取医生信息
                const doctorInfo = data.data;

                // 更新页面上的医生信息
                document.getElementById('doctorNameDisplay').innerText = doctorInfo['doctorName'] || '未获取医生姓名';
                document.getElementById('experienceDisplay').innerText = doctorInfo['experience'] || '未获取工龄';
                document.getElementById('phoneDisplay').innerText = doctorInfo['phone'] || '未获取电话';
                document.getElementById('emailDisplay').innerText = doctorInfo['email'] || '未获取邮箱';
                document.getElementById('genderDisplay').innerText = doctorInfo['gender'] || '未获取性别';
                document.getElementById('ageDisplay').innerText = doctorInfo['age'] || '未获取年龄';
                document.getElementById('idNumberDisplay').innerText = doctorInfo['idNumber'] || '未获取身份证号'; // 显示身份证号

                // 调试输出：查看获取的医生信息
                console.log('加载的医生信息:', doctorInfo);

                // 将获取到的信息存储到 localStorage
                localStorage.setItem('doctorInfo', JSON.stringify({
                    'doctorId': doctorInfo['doctorId'],  // 存储 doctorId，方便后续使用
                    'doctorName': doctorInfo['doctorName'],
                    'experience': doctorInfo['experience'],
                    'phone': doctorInfo['phone'],
                    'email': doctorInfo['email'],
                    'gender': doctorInfo['gender'],
                    'age': doctorInfo['age'],
                    'idNumber': doctorInfo['idNumber'], // 存储身份证号
                }));
            } else {
                alert('未找到医生信息，请稍后重试');
            }
        })
        .catch(error => {
            console.error('获取医生信息失败:', error);
            alert('获取医生信息失败，请稍后重试');
        });
}

