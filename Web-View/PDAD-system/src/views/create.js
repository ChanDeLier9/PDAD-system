// 获取存储的病患信息
let patients = JSON.parse(localStorage.getItem('patients')) || [];

// 页面加载时，检查并加载数据
window.onload = function() {
    checkButtonStatus();
    loadPatientData();  // 加载病患数据
};

// 检查按钮状态（如果病患已存在，则禁用按钮）
function checkButtonStatus() {
    const patientName = document.getElementById('search-name').value.trim();

    // 检查病患是否已存在
    const isPatientExist = patients.some(patient => patient.name === patientName);

    const addButton = document.getElementById('add-patient-btn');

    if (isPatientExist) {
        addButton.disabled = true;
        addButton.innerText = "已添加";
    } else {
        addButton.disabled = false;
        addButton.innerText = "添加患者";
    }
}

// 搜索患者
function searchPatient() {
    const searchName = document.getElementById('search-name').value.trim();
    const patientTableBody = document.getElementById('patient-table-body');

    // 清空表格内容
    patientTableBody.innerHTML = '';

    // 打印 doctorId 和 searchName
    const doctorId = localStorage.getItem('doctorId');  // 获取当前医生的 doctorId
    console.log("当前医生ID：", doctorId);  // 显示 doctorId
    console.log("搜索的患者姓名：", searchName);  // 显示搜索的姓名

    if (searchName) {
        // 发送 GET 请求到后端查询患者数据
        const doctorId = localStorage.getItem('doctorId');  // 假设 doctorId 存储在 localStorage 中

        fetch(`/api/doctors/${doctorId}/patients/search?patientName=${encodeURIComponent(searchName)}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            },
        })
            .then(response => response.json())
            .then(data => {
                console.log("后端返回的患者数据：", data);  // 调试输出
                if (data.code === 0) {
                    // 成功获取患者数据，更新页面内容
                    const filteredPatients = data.data;  // 从返回的数据中获取患者信息

                    if (filteredPatients.length > 0) {
                        filteredPatients.forEach(patient => {
                            const row = document.createElement('tr');
                            row.innerHTML = `
                            <td>${patient.patientId}</td>
                            <td>${patient.patientName}</td>
                            <td>${patient.phone}</td>
                            <td>${patient.gender}</td>
                            <td>${patient.email}</td>
                            <td>${patient.age}</td>
                            <td><button onclick="addMedicalRecord('${patient.patientId}', '${patient.patientName}')">添加病历</button></td>
                        `;
                            patientTableBody.appendChild(row);
                        });
                    } else {
                        const row = document.createElement('tr');
                        row.innerHTML = '<td colspan="7">未找到匹配的患者</td>';
                        patientTableBody.appendChild(row);
                    }
                } else {
                    alert(data.message || '查询失败，请稍后重试');
                }
            })
            .catch(error => {
                console.error('获取患者信息失败:', error);
                alert('获取患者信息失败，请稍后重试');
            });
    } else {
        alert("请输入姓名进行搜索");
    }
}


// 添加患者病历
function addMedicalRecord(patientId, patientName) {
// 从 localStorage 获取医生ID
    const doctorId = localStorage.getItem('doctorId');  // 获取 doctorId
    if (!doctorId) {
        alert("未找到医生信息，请登录！");
        return;
    }
    const diagnosis =''
    const treatment = ''
    // 构造请求数据
    const medicalRecordRequest = {
        patientId: patientId,
        patientName: patientName,
        doctorId: doctorId,
        diagnosis: diagnosis,
        treatment: treatment
    };

    // 调用后端接口添加病历
    fetch(`/api/doctors/${doctorId}/patients/medical-record`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(medicalRecordRequest)
    })
        .then(response => response.json())
        .then(data => {
            if (data.code === 0) {
                alert("患者病历添加成功！");
            } else {
                alert("添加病历失败：" + data.message);
            }
        })
        .catch(error => {
            alert("添加病历请求失败，请稍后再试！");
            console.error('Error:', error);
        });
}

// 加载患者数据并显示在表格中
function loadPatientData() {
    const tableBody = document.getElementById('patient-table-body');
    tableBody.innerHTML = ''; // 清空当前表格内容

    patients.forEach(patient => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${patient.patientId}</td>
            <td>${patient.patientName}</td>
            <td>${patient.phone}</td>
            <td>${patient.gender}</td>
            <td>${patient.email}</td>
            <td>${patient.age}</td>
            <td><button onclick="addMedicalRecord('${patient.patientId}', '${patient.patientName}')">添加病历</button></td>
        `;
        tableBody.appendChild(row);
    });
}
