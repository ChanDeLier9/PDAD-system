// 获取存储的成绩表和结论
let testResults = JSON.parse(localStorage.getItem('testResults')) || [];
let testResultsConclusion = JSON.parse(localStorage.getItem('testResultsConclusion')) || []; // 结论数据

window.onload = function() {
    // 获取 URL 中的患者 ID 和姓名 参数
    const urlParams = new URLSearchParams(window.location.search);
    const patientId = urlParams.get('id');  // 从 URL 中获取患者 ID
    const patientName = urlParams.get('name');  // 从 URL 中获取患者姓名
    const scaleId = urlParams.get('scaleId');  // 获取 scaleId


    console.log("获取到的 patientId:", patientId);  // 打印 patientId
    console.log("获取到的 patientName:", patientName);  // 打印 patientName
    console.log("获取到的 scaleId:", scaleId);  // 打印 scaleId


    if (patientId && scaleId) {
        displayPatientInfo(patientId,patientName)
        // 调用后端接口获取诊断结论
        getDiagnosis(patientId, scaleId);

        // 调用后端接口获取每道题的答案、选项内容和得分
        getPatientScaleAnswers(patientId, scaleId);
    } else {
        alert("未找到患者ID或量表ID！");
    }
}

// 显示患者基本信息
function displayPatientInfo(patientId,patientName) {
    document.getElementById('patientName').textContent = patientName;
    document.getElementById('patientId').textContent = patientId;
}

// 获取诊断结论
function getDiagnosis(patientId, scaleId) {
    console.log(`调用接口获取患者 ${patientId} 的量表 ${scaleId} 诊断结果`);

    fetch(`/api/patients/${patientId}/diagnosis/${scaleId}`)
        .then(response => response.json())
        .then(data => {
            console.log(`获取量表 ${scaleId} 诊断结论的接口返回的数据:`, data);

            if (data.code === 0) {
                const diagnosis = data.data;
                console.log(diagnosis);
                // 更新页面上的量表结论
                document.getElementById('diagnosis').textContent = diagnosis || '无结论';
            } else {
                console.error(`获取量表 ${scaleId} 结论失败:`, data.message);
                alert(`获取量表 ${scaleId} 结论失败：` + data.message);
            }
        })
        .catch(error => {
            console.error("请求失败:", error);
            alert("获取量表结论时发生错误，请稍后再试。");
        });
}


// 获取量表每道题的答案、选项和得分
function getPatientScaleAnswers(patientId, scaleId) {
    console.log(`调用接口获取患者 ${patientId} 的量表 ${scaleId} 答案和得分`);

    fetch(`/api/patients/${patientId}/scale/${scaleId}/answers`)
        .then(response => response.json())
        .then(data => {
            console.log(`获取量表 ${scaleId} 答案和得分的接口返回的数据:`, data);
            console.log(data.code);
            if (data.code === 0) {
                const answers = data.data;
                // 填充表格中的问题、选项和得分
                displayQuestionAnswers(answers);
            } else {
                console.error(`获取量表 ${scaleId} 答案和得分失败:`, data.message);
                alert(`获取量表 ${scaleId} 答案和得分失败：` + data.message);
            }
        })
        .catch(error => {
            console.error("请求失败:", error);
            alert("获取量表答案和得分时发生错误，请稍后再试。");
        });
}
// 填充每道题的答案、选项和得分
function displayQuestionAnswers(answers) {
    console.log(answers);
    const tableBody = document.getElementById('test-table-body');
    tableBody.innerHTML = ''; // 清空当前表格内容

    // 根据 scaleId 来过滤不同的问题范围
    const filteredAnswers = filterAnswersByScaleId(answers);

    // 遍历每道题的答案并填充到表格
    filteredAnswers.forEach(answer => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${answer.questionContent}</td>
            <td>${answer.optionContent}</td>
            <td>${answer.optionScore}</td>
        `;
        tableBody.appendChild(row);
    });
}

// 根据 scaleId 来过滤不同的题目范围
function filterAnswersByScaleId(answers) {
    const scaleId = parseInt(new URLSearchParams(window.location.search).get('scaleId'));  // 获取当前 scaleId

    let filteredAnswers = [];

    switch (scaleId) {
        case 2:
            // 过滤 questionId 为 1 到 20 的问题
            filteredAnswers = answers.filter(answer => answer.questionId >= 1 && answer.questionId <= 20);
            break;
        case 3:
            // 过滤 questionId 为 21 到 37 的问题
            filteredAnswers = answers.filter(answer => answer.questionId >= 21 && answer.questionId <= 37);
            break;
        case 5:
            // 过滤 questionId 为 38 到 46 的问题
            filteredAnswers = answers.filter(answer => answer.questionId >= 38 && answer.questionId <= 46);
            break;
        default:
            console.error("不支持的 scaleId:", scaleId);
            break;
    }

    return filteredAnswers;
}