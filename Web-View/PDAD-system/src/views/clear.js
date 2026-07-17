// 获取 URL 中的患者 ID 参数
//const urlParams = new URLSearchParams(window.location.search);
//const patientId = urlParams.get('id');  // 从URL中获取患者ID
//const patientName = urlParams.get('name');  // 从URL中获取患者姓名

// 获取存储的病历信息
//let medicalRecords = JSON.parse(localStorage.getItem('medicalRecords')) || [];

window.onload = function() {
    // 获取 URL 中的查询参数
    const urlParams = new URLSearchParams(window.location.search);
    const patientId = urlParams.get('patientId');  // 获取 patientId

    // 获取 doctorId 从 localStorage
    const doctorId = localStorage.getItem('doctorId');  // 从 localStorage 获取 doctorId

    console.log("获取到的 patientId:", patientId);  // 打印 patientId
    console.log("获取到的 doctorId:", doctorId);  // 打印 doctorId

    if (patientId && doctorId) {
        // 调用后端接口获取最终诊断结果
        console.log(`调用接口获取患者 ${patientId} 的病历信息，医生ID: ${doctorId}`);
        loadLatestImageAnalysis(patientId);
        fetch(`/api/patients/${patientId}/final-diagnosis`)
            .then(response => response.json())
            .then(data => {
                console.log("接口返回的数据:", data);  // 打印返回的数据
                console.log(data.code);
                if (data.code === 0) {
                    // 如果请求成功，显示返回的结果
                    const result = data.data;
                    console.log("最终结果:", result);
                    // 更新页面上的最终诊断结果
                    document.getElementById('finalScore').textContent = result.finalScore;
                    document.getElementById('finalResult').textContent = result.finalResult;
                    document.getElementById('patientName').textContent = result.patientName;  // 更新患者姓名
                    document.getElementById('patientId').textContent = patientId;  // 更新患者ID


                    // 在获取最终诊断结果后，调用第二个接口来获取治疗建议
                    getTreatmentSuggestions(doctorId, patientId);

                } else {
                    // 如果请求失败，显示错误信息
                    console.error("获取最终诊断结果失败:", data.message);
                    alert("获取最终诊断结果失败：" + data.message);
                }
            })
            .catch(error => {
                // 处理请求错误
                console.error("请求失败:", error);
                alert("获取最终诊断结果时发生错误，请稍后再试。");
            });
    } else {
        alert("未找到患者ID！");
    }
}

// 获取治疗建议
function getTreatmentSuggestions(doctorId, patientId) {
    console.log(`调用接口获取患者 ${patientId} 的治疗建议，医生ID: ${doctorId}`);

    // 第二个接口：获取治疗建议
    fetch(`/api/doctors/${doctorId}/patients/${patientId}/medical-record`)
        .then(response => response.json())
        .then(data => {
            console.log("获取治疗建议的接口返回的数据:", data);  // 打印返回的数据

            if (data.code === 0) {
                const treatmentData = data.data;
                console.log("治疗建议:", treatmentData);

                // 更新页面上的诊断和治疗建议
                document.getElementById('diagnosis').value = treatmentData.diagnosis || '';
                document.getElementById('treatment').value = treatmentData.treatment || '';
            } else {
                console.error("获取治疗建议失败:", data.message);
                alert("获取治疗建议失败：" + data.message);
            }
        })
        .catch(error => {
            console.error("请求失败:", error);
            alert("获取治疗建议时发生错误，请稍后再试。");
        });
}

// 保存诊断结果和治疗建议
function savePatientInfo() {
    const diagnosis = document.getElementById('diagnosis').value.trim();
    const treatment = document.getElementById('treatment').value.trim();

    if (diagnosis && treatment) {
        const doctorId = localStorage.getItem('doctorId');  // 从 localStorage 获取 doctorId
        const patientId = new URLSearchParams(window.location.search).get('patientId');  // 从 URL 获取 patientId
        console.log("获取到的 patientId:", patientId);  // 打印 patientId
        console.log("获取到的 doctorId:", doctorId);  // 打印 doctorId
        if (doctorId && patientId) {
            // 创建请求体对象
            const medicalRecordUpdateRequest = {
                diagnosis: diagnosis,
                treatment: treatment
            };

            // 调用后端接口更新病历记录
            fetch(`/api/doctors/${doctorId}/patients/${patientId}/medical-record/diagnosis`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(medicalRecordUpdateRequest)  // 将请求体传递到后端
            })
                .then(response => response.json())
                .then(data => {
                    console.log(data.code);
                    if (data.code === 0) {

                            alert('诊断结果和治疗建议已保存');

                    } else {
                        // 如果后端返回错误，显示错误信息
                        alert('病历更新失败：' + data.message);
                    }
                })
                .catch(error => {
                    // 处理请求错误
                    console.error('请求失败:', error);
                    alert('病历更新失败，请稍后再试。');
                });
        } else {
            alert('未找到医生ID或患者ID');
        }
    } else {
        alert('请输入诊断结果和治疗建议');
    }
}

async function loadLatestImageAnalysis(patientId) {
    try {
        const resp = await fetch(`/api/patients/${encodeURIComponent(patientId)}/image-analysis`);
        const res = await resp.json();

        if (!res || res.code !== 0 || !res.data) {
            setAiHint("暂无AI影像分析记录。");
            return;
        }

        const dto = res.data;

        // 1) 更新时间
        const t = formatLocalDateTime(dto.updateTime);
        setText("aiUpdateTime", t || "-");

        // 2) 分析结果（analysisResult 可能是：纯文本 或 JSON字符串）
        const parsed = tryParseJson(dto.analysisResult);
        const picked = pickAnalysisFields(parsed, dto.analysisResult);

        setText("aiRiskLevel", picked.riskLevel ?? "-");
        setText("aiProb", picked.probability ?? "-");
        setText("aiReliability", picked.reliability ?? "-");

// 结论：只显示 result + diagnosis
        const resultText = picked.result ? `结果：${picked.result}` : `结果：-`;
        const diagText = picked.diagnosis ? `建议：${picked.diagnosis}` : `建议：-`;
        setText("aiDiagnosisText", `${resultText}\n${diagText}`);

        // 3) 图片（注意：多数浏览器不原生支持 TIFF 直接 <img> 预览）
        const base64 = dto.imageBase64;
        const contentType = dto.imageContentType || "image/tiff";
        if (base64) {
            const dataUrl = `data:${contentType};base64,${base64}`;

            // 先提供下载（永远可用）
            const a = document.getElementById("aiDownloadLink");
            a.href = dataUrl;
            a.download = contentType.includes("tif") ? "fmri.tif" : "fmri_image";
            a.style.display = "inline-block";

            // 再尝试预览（如果浏览器不支持 TIFF，会 onerror）
            const img = document.getElementById("aiFmriImg");
            img.onload = () => {
                img.style.display = "block";
                setAiHint("");
            };
            img.onerror = () => {
                img.style.display = "none";
                setAiHint("当前浏览器可能不支持 TIFF 直接预览，已提供“下载原始影像”。（若要网页直接显示，建议后端改为返回 PNG/JPG）");
            };
            img.src = dataUrl;
        } else {
            setAiHint("本次记录未包含影像数据。");
        }

    } catch (e) {
        setAiHint("加载AI影像分析失败，请稍后重试。");
    }
}

/* ===== 工具函数 ===== */

function setText(id, text) {
    const el = document.getElementById(id);
    if (el) el.textContent = String(text);
}

function setAiHint(text) {
    const el = document.getElementById("aiImgHint");
    if (el) el.textContent = text || "";
}

function tryParseJson(str) {
    if (!str || typeof str !== "string") return null;
    try { return JSON.parse(str); } catch { return null; }
}

function pickAnalysisFields(obj, rawText) {
    // 1) 如果 analysisResult 是 JSON（未来你改成 JSON 存库也能用）
    if (obj && typeof obj === "object") {
        return normalizeFields({
            result: obj.result,
            diagnosis: obj.diagnosis,
            probability: obj.probability ?? obj.prob,
            reliability: obj.reliability,
            riskLevel: obj.riskLevel ?? obj.risk_level
        });
    }

    // 2) 兼容你现在的字符串格式：label=0, result=..., probability=..., ...
    if (typeof rawText === "string" && rawText.trim().length > 0) {
        const text = rawText.trim();
        const get = (key) => extractByKey(text, key);

        return normalizeFields({
            result: get("result"),
            diagnosis: get("diagnosis"),
            probability: get("probability"),
            reliability: get("reliability"),
            riskLevel: get("riskLevel")
        });
    }

    return {};
}

function extractByKey(text, key) {
    // 用 “, key=” 作为下一段开始的边界，避免 diagnosis 里出现逗号导致截断
    const boundary = ",\\s*(?:label|result|probability|reliability|threshold|riskLevel|diagnosis)=";
    const re = new RegExp(`${key}=([\\s\\S]*?)(?=${boundary}|$)`, "i");
    const m = text.match(re);
    return m ? m[1].trim() : null;
}

function normalizeFields(f) {
    // 数值格式化
    const prob = f.probability != null && f.probability !== ""
        ? Number(f.probability).toFixed(4)
        : null;

    const rel = f.reliability != null && f.reliability !== ""
        ? Number(f.reliability).toFixed(4)
        : null;

    // 风险等级英文 -> 中文（你也可以直接显示英文）
    let risk = f.riskLevel;
    if (risk) {
        const r = String(risk).toLowerCase();
        if (r === "low") risk = "低";
        else if (r === "medium") risk = "中";
        else if (r === "high") risk = "高";
    }

    return {
        result: f.result || null,
        diagnosis: f.diagnosis || null,
        probability: prob,
        reliability: rel,
        riskLevel: risk || null
    };
}

function formatLocalDateTime(s) {
    if (!s) return "";
    // 兼容 "2025-12-11 23:21:18" 或 "2025-12-11T23:21:18"
    const iso = String(s).replace(" ", "T");
    const d = new Date(iso);
    if (isNaN(d.getTime())) return String(s);
    return d.toLocaleString("zh-CN");
}
