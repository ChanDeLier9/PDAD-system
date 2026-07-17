// 获取存储的病历表和成绩表
let medicalRecords = JSON.parse(localStorage.getItem('medicalRecords')) || [];
let testResults = JSON.parse(localStorage.getItem('testResults')) || [];

// 页面加载时填充表格
window.onload = function() {
    loadPatientData();
}

// 加载病历表中的患者信息并显示在表格中
async function loadPatientData() {
    const tableBody = document.getElementById('patient-table-body');
    tableBody.innerHTML = ''; // 清空当前表格内容

    const doctorId = localStorage.getItem('doctorId');  // 获取当前医生的 doctorId
    const patientId = localStorage.getItem('patientId');  // 获取当前患者的 patientId

    console.log("当前医生ID：", doctorId);  // 显示 doctorId
    console.log("当前患者ID：", patientId);  // 显示 patientId

    // 1. 获取患者信息
    try {
        const response = await fetch(`/api/doctors/${doctorId}/patients`);
        const data = await response.json();
        const medicalRecords = data.data; // 假设返回的患者病历数据
        console.log("获取到的患者信息：", medicalRecords); // 打印患者数据，检查每个 record.patientId 是否正确

        // 2. 获取每个患者的测试成绩
        const testScorePromises = medicalRecords.map(async (record) => {
            if (record.patientId) {
                console.log(record.patientId);
                try {
                    const [test1Response, test2Response, test3Response] = await Promise.all([
                        fetch(`/api/patients/${record.patientId}/test-score/2`), // test1
                        fetch(`/api/patients/${record.patientId}/test-score/3`), // test2
                        fetch(`/api/patients/${record.patientId}/test-score/5`)  // test3
                    ]);

                    // 解析每个分数请求的返回结果
                    const test1 = test1Response.ok ? await test1Response.json() : { data: 'N/A' };
                    const test2 = test2Response.ok ? await test2Response.json() : { data: 'N/A' };
                    const test3 = test3Response.ok ? await test3Response.json() : { data: 'N/A' };

                    return {
                        patientId: record.patientId,
                        patientName: record.patientName,
                        test1: test1,
                        test2: test2,
                        test3: test3
                    };
                } catch (error) {
                    console.error(`获取患者 ${record.patientId} 的测试分数失败:`, error);
                    return {
                        patientId: record.patientId,
                        patientName: record.patientName,
                        test1: { data: 'N/A' },
                        test2: { data: 'N/A' },
                        test3: { data: 'N/A' }
                    };
                }
            } else {
                // 如果没有 patientId，返回一个默认值
                return {
                    patientId: 'N/A',
                    patientName: record.patientName,
                    test1: { data: 'N/A' },
                    test2: { data: 'N/A' },
                    test3: { data: 'N/A' }
                };
            }
        });

        // 等待所有患者的测试成绩数据
        const patientData = await Promise.all(testScorePromises);
        console.log("患者数据与测试成绩：", patientData); // 打印调试输出

        // 将每个患者的病历数据和分数展示在表格中
        patientData.forEach(record => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${record.patientId}</td>
                <td>${record.patientName}</td>
                <td><a href="score.html?id=${record.patientId}&name=${record.patientName}&scaleId=2 ">${record.test1.data}</a></td>
                <td><a href="score.html?id=${record.patientId}&name=${record.patientName}&scaleId=3 ">${record.test2.data}</a></td>
                <td><a href="score.html?id=${record.patientId}&name=${record.patientName}&scaleId=5 ">${record.test3.data}</a></td>
                <td><button onclick="viewDetails('${record.patientId}')">详情</button></td>
                <td><button onclick="deletePatient('${record.patientId}')">删除</button></td>
            `;
            tableBody.appendChild(row);
        });

    } catch (error) {
        console.error('获取病历数据失败:', error);
    }
}

function viewDetails(patientId) {
    window.location.href = `clear.html`;
}

// 删除患者
function deletePatient(patientId) {
    const doctorId = localStorage.getItem('doctorId');  // 获取当前医生的 doctorId

    // 调用后端接口删除病历
    fetch(`/api/doctors/${doctorId}/patients/medical-record?patientId=${patientId}`, {
        method: 'DELETE',
    })
        .then(response => response.json())
        .then(data => {
            console.log(data.code);
            // 确保返回的数据格式正确，并且删除成功
            if (data.code === 0) {
                // 如果删除成功，更新本地存储的数据
                medicalRecords = medicalRecords.filter(record => record.patientId !== patientId);
                testResults = testResults.filter(test => test.patientId !== patientId);

                // 更新localStorage
                localStorage.setItem('medicalRecords', JSON.stringify(medicalRecords));
                localStorage.setItem('testResults', JSON.stringify(testResults));

                // 重新加载更新后的表格
                loadPatientData(); // 确保重新加载表格
                alert("患者病历删除成功！");
            } else {
                // 如果删除失败，弹出错误提示
                alert(`删除病历失败：${data.message || '未知错误'}`);
            }
        })
        .catch(error => {
            // 处理网络错误等
            console.error('删除病历时发生错误:', error);
            alert("删除病历失败，请稍后再试！");
        });
}
