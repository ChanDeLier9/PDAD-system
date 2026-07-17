document.addEventListener('DOMContentLoaded', () => {
    // ===== 全局配置 =====
    const API_BASE = '/api';

    // ===== DOM 获取：预测相关 =====
    const fmriFileInput = document.getElementById('fmriFileInput');
    const fileNameDisplay = document.getElementById('fileNameDisplay');
    const predictButton = document.getElementById('predictButton');
    const predictionOutput = document.getElementById('predictionOutput');

    // ===== DOM 获取：训练相关 =====
    const modelTypeSelect = document.getElementById('modelType');
    const trainRatioInput = document.getElementById('trainRatio');
    const randomStateInput = document.getElementById('randomState');
    const nEstimatorsInput = document.getElementById('nEstimators');
    const maxDepthInput = document.getElementById('maxDepth');
    const learningRateInput = document.getElementById('learningRate');
    const trainButton = document.getElementById('trainButton');
    const trainingOutput = document.getElementById('trainingOutput');
    const lossCanvas = document.getElementById('lossChart');

    // 返回按钮
    const backButton = document.getElementById('backButton');
    if (backButton) {
        backButton.addEventListener('click', () => {
            if (window.history.length > 1) {
                window.location.href = './mine.html';
            } else {
                window.history.back();
            }
        });
    }

    // 选择文件后显示文件名
    if (fmriFileInput && fileNameDisplay) {
        fmriFileInput.addEventListener('change', (event) => {
            if (event.target.files.length > 0) {
                fileNameDisplay.textContent = event.target.files[0].name;
            } else {
                fileNameDisplay.textContent = '未选择文件';
            }
        });
    }

    // =======================
    // 一、fMRI 预测功能（医生沙盒）
    // =======================
    if (predictButton) {
        predictButton.addEventListener('click', async () => {
            const file = fmriFileInput.files[0];
            if (!file) {
                alert('请选择一个 TIF/TIFF 图像文件进行预测。');
                return;
            }

            predictionOutput.textContent = '正在上传并预测...';

            const formData = new FormData();
            formData.append('file', file);  // 对应后端 @RequestPart("file")

            try {
                const response = await fetch(`${API_BASE}/fmri/predict`, {
                    method: 'POST',
                    body: formData,
                });

                const result = await response.json();

                if (response.ok && result.code === 0 && result.data) {
                    const d = result.data;

                    const riskMap = {
                        low: '低风险',
                        medium: '中等风险',
                        high: '高风险'
                    };
                    const riskText = riskMap[d.riskLevel] || d.riskLevel || '未知';

                    const text =
                        `预测结论：${d.result}\n` +
                        `抑郁概率：${(d.probability * 100).toFixed(2)}%\n` +
                        `结果可靠性：${(d.reliability * 100).toFixed(1)}%\n` +
                        `风险等级：${riskText}\n` +
                        `诊断建议：${d.diagnosis}`;

                    predictionOutput.textContent = text;
                } else {
                    predictionOutput.textContent = `预测失败: ${result.message || '未知错误'}`;
                }
            } catch (error) {
                console.error('fMRI 预测请求失败:', error);
                predictionOutput.textContent = `请求失败: ${error.message}`;
            }
        });
    }

// =======================
// 二、模型训练可视化
// =======================

    let lossChart = null;
    let curveTimer = null;  // 用来控制动画，防止多次点击叠加

// 创建 / 重置 Chart.js 图表（坐标轴中文）
    function createOrResetChart(metricNameText = '指标值 / 损失值') {
        if (!lossCanvas) return;

        const ctx = lossCanvas.getContext('2d');

        if (lossChart) {
            lossChart.destroy();
            lossChart = null;
        }

        lossChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: [], // 训练步数 step
                datasets: [
                    {
                        label: '训练集指标',
                        data: [],
                        borderWidth: 2,
                        tension: 0.2
                    },
                    {
                        label: '验证集指标',
                        data: [],
                        borderWidth: 2,
                        tension: 0.2
                    }
                ]
            },
            options: {
                responsive: true,
                animation: false,
                scales: {
                    x: {
                        title: {
                            display: true,
                            text: '训练步数（Step）'   // X 轴中文
                        }
                    },
                    y: {
                        title: {
                            display: true,
                            text: metricNameText      // Y 轴中文 + 指标名
                        }
                    }
                },
                plugins: {
                    legend: {
                        display: true,
                        labels: {
                            // 图例 label 已经是中文
                        }
                    }
                }
            }
        });
    }

    /**
     * 动态 / 快速绘制收敛曲线：
     * - 如果点数 <= MAX_ANIM_POINTS：逐点动画
     * - 如果点数太多：一次性画完，不拖时间
     */
    function animateCurve(curve) {
        if (!lossChart || !curve || curve.length === 0) return;

        // 先停掉上一次的动画
        if (curveTimer) {
            clearInterval(curveTimer);
            curveTimer = null;
        }

        const steps = curve.map(p => p.step);
        const trainMetricArr = curve.map(p => p.train_metric);
        const valMetricArr = curve.map(p => p.val_metric);

        // 根据 metric_name 设置 Y 轴中文标题
        const metricName = curve[0].metric_name || 'metric';
        let metricTitle = '指标值 / 损失值';
        if (metricName.toLowerCase() === 'logloss') {
            metricTitle = '对数损失（logloss）';
        } else if (metricName.toLowerCase().includes('auc')) {
            metricTitle = 'AUC 指标';
        }
        lossChart.options.scales.y.title.text = metricTitle;
        lossChart.update();

        const MAX_ANIM_POINTS = 40;   // 动画最多“演”40个点，多了直接画完
        const INTERVAL_MS = 200;      // 动画间隔（越小越快）

        // 点数太多就直接画完，避免 200 × 0.6s 那种“一直在画”的体验
        if (steps.length > MAX_ANIM_POINTS) {
            lossChart.data.labels = steps;
            lossChart.data.datasets[0].data = trainMetricArr;
            lossChart.data.datasets[1].data = valMetricArr;
            lossChart.update();
            return;
        }

        // 逐点动态演示收敛过程
        let i = 0;
        curveTimer = setInterval(() => {
            if (i >= steps.length) {
                clearInterval(curveTimer);
                curveTimer = null;
                return;
            }
            lossChart.data.labels.push(steps[i]);
            lossChart.data.datasets[0].data.push(trainMetricArr[i]);
            lossChart.data.datasets[1].data.push(valMetricArr[i]);
            lossChart.update();
            i++;
        }, INTERVAL_MS);
    }

    if (trainButton) {
        trainButton.addEventListener('click', async () => {
            const modelType = modelTypeSelect.value;
            const trainRatio = parseFloat(trainRatioInput.value);
            const randomState = parseInt(randomStateInput.value);

            if (isNaN(trainRatio) || trainRatio < 0 || trainRatio > 1) {
                alert('训练比例必须是 0 到 1 之间的数字。');
                return;
            }
            if (isNaN(randomState)) {
                alert('随机种子必须是数字。');
                return;
            }

            // ===== 根据模型类型，构造 Python 端要的 params keyword =====
            const params = {};
            const nEst = parseInt(nEstimatorsInput.value);
            const maxDep = parseInt(maxDepthInput.value);
            const lr = parseFloat(learningRateInput.value);

            // 随机森林：支持 n_estimators / max_depth
            if (modelType === 'rf') {
                if (!isNaN(nEst)) {
                    params.n_estimators = nEst;
                }
                if (!isNaN(maxDep)) {
                    params.max_depth = maxDep;
                }
            }

            // XGBoost / LightGBM：支持 n_estimators / max_depth / learning_rate
            if (modelType === 'xgb' || modelType === 'lgbm') {
                if (!isNaN(nEst)) {
                    params.n_estimators = nEst;
                }
                if (!isNaN(maxDep)) {
                    params.max_depth = maxDep;
                }
                if (!isNaN(lr)) {
                    params.learning_rate = lr;
                }
            }

            // 逻辑回归：暂时不接收树模型参数，使用 Python 默认配置

            trainingOutput.textContent = '正在开始模型训练，请稍候...';

            // 初始化图表（先给一个中文 Y 轴标题占位）
            createOrResetChart('指标值 / 损失值');

            const requestBody = {
                modelType: modelType,
                trainRatio: trainRatio,
                randomState: randomState,
                params: params
            };

            try {
                const response = await fetch(`${API_BASE}/fmri/train`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(requestBody),
                });

                const result = await response.json();

                if (response.ok && result.code === 0 && result.data) {
                    const d = result.data;
                    const m = d.final_metrics || {};
                    const curve = d.curve || [];

                    // 文本区：给老师看的最终结论
                    const summary =
                        `模型类型：${d.model_type || d.modelType}\n` +
                        `训练集样本数：${d.n_train}\n` +
                        `验证集样本数：${d.n_val}\n\n` +
                        `最终指标：\n` +
                        `  准确率 (accuracy)：${(m.accuracy ?? 0).toFixed(4)}\n` +
                        `  精确率 (precision)：${(m.precision ?? 0).toFixed(4)}\n` +
                        `  召回率 (recall)：${(m.recall ?? 0).toFixed(4)}\n` +
                        `  F1 值 (f1)：${(m.f1 ?? 0).toFixed(4)}\n` +
                        `  AUC：${(m.auc ?? 0).toFixed(4)}\n` +
                        `  PR-AUC：${(m.pr_auc ?? 0).toFixed(4)}\n`;
                    trainingOutput.textContent = summary;

                    // 图：动态/静态展示收敛曲线
                    animateCurve(curve);
                } else {
                    trainingOutput.textContent = `训练失败: ${result.message || '未知错误'}`;
                }
            } catch (error) {
                console.error('模型训练请求失败:', error);
                trainingOutput.textContent = `请求失败: ${error.message}`;
            }
        });
    }

});
