// 获取医生ID（从localStorage或其他方式获取）
function getDoctorId() {
    // 这里需要根据您的登录系统获取当前医生ID
    // 示例：从localStorage获取
    return localStorage.getItem('doctorId') || 'doctor001'; // 默认值，实际应用中需要修改
}

// 返回上一页
function goBack() {
    window.history.back();
}

// 显示/隐藏加载状态
function showLoading(show) {
    const loading = document.getElementById('loading');
    if (show) {
        loading.classList.remove('hidden');
    } else {
        loading.classList.add('hidden');
    }
}

// 显示消息提示
function showMessage(message, type = 'info') {
    // 创建消息提示元素
    const messageDiv = document.createElement('div');
    messageDiv.className = `message message-${type}`;
    messageDiv.textContent = message;
    
    // 添加样式
    messageDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 8px;
        color: white;
        font-weight: 600;
        z-index: 2000;
        animation: slideIn 0.3s ease;
        ${type === 'success' ? 'background: #27ae60;' : ''}
        ${type === 'error' ? 'background: #e74c3c;' : ''}
        ${type === 'info' ? 'background: #3498db;' : ''}
    `;
    
    document.body.appendChild(messageDiv);
    
    // 3秒后自动移除
    setTimeout(() => {
        messageDiv.remove();
    }, 3000);
}

// 验证表单
function validateForm(formData) {
    if (!formData.title.trim()) {
        showMessage('请输入文章标题', 'error');
        return false;
    }
    
    if (!formData.doctor_name.trim()) {
        showMessage('请输入作者姓名', 'error');
        return false;
    }
    
    if (!formData.tags) {
        showMessage('请选择文章标签', 'error');
        return false;
    }
    
    if (!formData.content.trim()) {
        showMessage('请输入文章内容', 'error');
        return false;
    }

    
    return true;
}

// 发布文章
async function publishArticle(articleData) {
    const doctorId = getDoctorId();
    
    try {
        const response = await fetch(`/api/doctors/${doctorId}/articles`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(articleData)
        });
        
        const result = await response.json();
        
        if (response.ok && result.code === 0) {
            showMessage('文章发布成功！', 'success');
            // 延迟跳转，让用户看到成功消息
            setTimeout(() => {
                goBack();
            }, 2000);
            return true;
        } else {
            throw new Error(result.message || '发布失败');
        }
    } catch (error) {
        console.error('发布文章失败:', error);
        showMessage(`发布失败: ${error.message}`, 'error');
        return false;
    }
}

// 表单提交处理
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('articleForm');
    
    form.addEventListener('submit', async function(e) {
        e.preventDefault();
        
        // 获取表单数据
        const formData = {
            title: document.getElementById('title').value,
            doctor_name: document.getElementById('doctor_name').value,
            tags: document.getElementById('tags').value,
            content: document.getElementById('content').value
        };
        
        // 验证表单
        if (!validateForm(formData)) {
            return;
        }
        
        // 显示加载状态
        showLoading(true);
        
        // 禁用提交按钮
        const submitBtn = form.querySelector('.btn-submit');
        submitBtn.disabled = true;
        
        try {
            // 发布文章
            const success = await publishArticle(formData);
            
            if (!success) {
                // 如果发布失败，重新启用按钮
                submitBtn.disabled = false;
            }
        } finally {
            // 隐藏加载状态
            showLoading(false);
        }
    });
    
    // 添加CSS动画
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideIn {
            from {
                transform: translateX(100%);
                opacity: 0;
            }
            to {
                transform: translateX(0);
                opacity: 1;
            }
        }
    `;
    document.head.appendChild(style);
});