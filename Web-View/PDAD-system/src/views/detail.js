// 获取 URL 中的患者 ID 参数
const urlParams = new URLSearchParams(window.location.search);
const patientId = urlParams.get('id');
const patientName = urlParams.get('name');

// 获取存储的病历和成绩表
let medicalRecords = JSON.parse(localStorage.getItem('medicalRecords')) || [];
let testResults = JSON.parse(localStorage.getItem('testResults')) || [];

window.onload = function() {
    const patient = medicalRecords.find(record => record.patientId === patientId && record.patientName === patientName);

    if (patient) {
        displayPatientInfo(patient);
        loadPatientScores(patientId);
    } else {
        alert("未找到该患者的信息");
    }
}

// 显示患者基本信息
function displayPatientInfo(patient) {
    const patientInfoDiv = document.getElementById('patient-info');
    patientInfoDiv.innerHTML = `
        <tr>
            <td class="time1" rowspan="8">${new Date().toLocaleDateString()}</td>
            <td>${patient.patientId}</td>
            <td>${patient.patientName}</td>
            <td id="test1">N/A</td>
            <td id="test2">N/A</td>
            <td id="test3">N/A</td>
            <td id="totalScore">N/A</td>
        </tr>
    `;
}

// 根据患者 ID 查找成绩并显示
function loadPatientScores(patientId) {
    const patientTestResults = testResults.find(test => test.patientId === patientId);

    if (patientTestResults) {
        // 填充量表成绩
        document.getElementById('test1').textContent = patientTestResults.test1|| 'N/A';
        document.getElementById('test2').textContent = patientTestResults.test2 || 'N/A';
        document.getElementById('test3').textContent = patientTestResults.test3 || 'N/A';

        // 计算总分（如果有成绩）
        const totalScore = (patientTestResults.test1 + patientTestResults.test2 + patientTestResults.test3) / 3;
        document.getElementById('totalScore').textContent = totalScore ? totalScore.toFixed(2) : 'N/A';
    }
}

// 保存诊断结果和治疗建议到 medicalRecords 中
function saveAdvice() {
    const diagnosis = document.getElementById('diagnosis').value.trim();
    const treatment = document.getElementById('treatment').value.trim();

    if (diagnosis && treatment) {
        // 更新 medicalRecord 中的诊断和治疗建议
        const record = medicalRecords.find(record => record.patientId === patientId && record.patientName === patientName);
        if (record) {
            record.diagnosis = diagnosis;
            record.treatment = treatment;

            // 更新 localStorage
            localStorage.setItem('medicalRecords', JSON.stringify(medicalRecords));

            alert('诊断结果和治疗建议已保存');
        } else {
            alert('未找到该患者的病历信息');
        }
    } else {
        alert('请输入诊断结果和治疗建议');
    }
}
