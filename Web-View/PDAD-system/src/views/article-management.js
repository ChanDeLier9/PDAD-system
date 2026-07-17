// 获取医生ID
function getDoctorId() {
    return localStorage.getItem('doctorId') || 'doctor001';
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
    const messageDiv = document.createElement('div');
    messageDiv.className = `message message-${type}`;
    messageDiv.textContent = message;
    
    messageDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 8px;
        color: white;
        font-weight: 600;
        z-index: 3000;
        animation: slideIn 0.3s ease;
        ${type === 'success' ? 'background: #27ae60;' : ''}
        ${type === 'error' ? 'background: #e74c3c;' : ''}
        ${type === 'info' ? 'background: #3498db;' : ''}
    `;
    
    document.body.appendChild(messageDiv);
    
    setTimeout(() => {
        messageDiv.remove();
    }, 3000);
}

// 获取医生的所有文章
async function loadDoctorArticles() {
    const doctorId = getDoctorId();
    showLoading(true);

    try {
        // 首先获取医生信息来获取医生姓名
        const doctorResponse = await fetch(`/api/doctors/${doctorId}/info`);
        const doctorResult = await doctorResponse.json();

        if (!doctorResponse.ok || doctorResult.code !== 0) {
            throw new Error('无法获取医生信息');
        }

        const doctorName = doctorResult.data.doctorName;

        // 根据医生Id获取文章
        const articlesResponse = await fetch(`/api/articles/doctors/Id/${encodeURIComponent(doctorId)}`);
        const articlesResult = await articlesResponse.json();

        if (articlesResponse.ok && articlesResult.code === 0) {
            displayArticles(articlesResult.data || []);
        } else {
            throw new Error(articlesResult.message || '获取文章失败');
        }
    } catch (error) {
        console.error('加载文章失败:', error);
        showMessage(`加载文章失败: ${error.message}`, 'error');
        displayArticles([]);
    } finally {
        showLoading(false);
    }
}

// 显示文章列表
function displayArticles(articles) {
    console.log(articles);
    const articlesList = document.getElementById('articlesList');

    if (!articles || articles.length === 0) {
        articlesList.innerHTML = `
            <div class="empty-state">
                <h3>暂无文章</h3>
                <p>您还没有发布任何文章</p>
                <button class="btn-new-article" onclick="location.href='./publish-article.html'">发布第一篇文章</button>
            </div>
        `;
        return;
    }

    articlesList.innerHTML = articles.map(article => `
        <div class="article-card">
            <div class="article-header">
                <div>
                    <div class="article-title">${escapeHtml(article.title)}</div>
                    <div class="article-meta">
                        作者：${escapeHtml(article.doctorName)} | 
                        发布时间：${formatDate(article.createdAt)}
                    </div>
                </div>
                <span class="article-tag">${escapeHtml(article.tags)}</span>
            </div>
            <div class="article-content">
                ${escapeHtml(article.content.substring(0, 200))}${article.content.length > 200 ? '...' : ''}
            </div>
            <div class="article-actions">
                <button class="btn-edit" onclick="editArticle(${article.articleId})">
                    编辑
                </button>
                <button class="btn-delete" onclick="deleteArticle(${article.articleId}, '${escapeHtml(article.title)}')">
                    删除
                </button>
            </div>
        </div>
    `).join('');
}

// HTML转义
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// 格式化日期
function formatDate(dateString) {
    if (!dateString) return '未知';
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN') + ' ' + date.toLocaleTimeString('zh-CN', {hour: '2-digit', minute: '2-digit'});
}

// 搜索文章
function searchArticles() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const articleCards = document.querySelectorAll('.article-card');
    
    articleCards.forEach(card => {
        const title = card.querySelector('.article-title').textContent.toLowerCase();
        const content = card.querySelector('.article-content').textContent.toLowerCase();
        
        if (title.includes(searchTerm) || content.includes(searchTerm)) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
}

// 编辑文章
async function editArticle(articleId) {
    showLoading(true);
    
    try {
        // 获取文章详情
        const response = await fetch(`/api/articles/${articleId}`);
        const result = await response.json();
        
        if (response.ok && result.code === 0) {
            const article = result.data;
            
            // 填充编辑表单
            document.getElementById('editArticleId').value = article.articleId;
            document.getElementById('editTitle').value = article.title;
            document.getElementById('editAuthor').value = article.doctorName;
            document.getElementById('editTag').value = article.tags;
            document.getElementById('editContent').value = article.content;
            
            // 显示编辑模态框
            document.getElementById('editModal').classList.remove('hidden');
        } else {
            throw new Error(result.message || '获取文章详情失败');
        }
    } catch (error) {
        console.error('获取文章详情失败:', error);
        showMessage(`获取文章详情失败: ${error.message}`, 'error');
    } finally {
        showLoading(false);
    }
}

// 关闭编辑模态框
function closeEditModal() {
    document.getElementById('editModal').classList.add('hidden');
}

// 更新文章
async function updateArticle(articleData) {
    const doctorId = getDoctorId();
    const articleId = articleData.articleId;
    
    try {
        const response = await fetch(`/api/articles/doctors/${doctorId}/${articleId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                title: articleData.title,
                doctor_name: articleData.doctor_name,
                tags: articleData.tags,
                content: articleData.content
            })
        });
        
        const result = await response.json();
        
        if (response.ok && result.code === 0) {
            showMessage('文章更新成功！', 'success');
            closeEditModal();
            // 重新加载文章列表
            setTimeout(() => {
                loadDoctorArticles();
            }, 1000);
            return true;
        } else {
            throw new Error(result.message || '更新失败');
        }
    } catch (error) {
        console.error('更新文章失败:', error);
        showMessage(`更新失败: ${error.message}`, 'error');
        return false;
    }
}

// 删除文章
async function deleteArticle(articleId, articleTitle) {
    if (!confirm(`确定要删除文章「${articleTitle}」吗？此操作不可恢复。`)) {
        return;
    }
    
    const doctorId = getDoctorId();
    showLoading(true);
    
    try {
        const response = await fetch(`/api/articles/doctors/${doctorId}/${articleId}`, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json'
            }
        });
        
        const result = await response.json();
        
        if (response.ok && result.code === 0) {
            showMessage('文章删除成功！', 'success');
            // 重新加载文章列表
            setTimeout(() => {
                loadDoctorArticles();
            }, 1000);
        } else {
            throw new Error(result.message || '删除失败');
        }
    } catch (error) {
        console.error('删除文章失败:', error);
        showMessage(`删除失败: ${error.message}`, 'error');
    } finally {
        showLoading(false);
    }
}

// 页面加载完成后的初始化
document.addEventListener('DOMContentLoaded', function() {
    // 加载文章列表
    loadDoctorArticles();
    
    // 搜索框回车事件
    document.getElementById('searchInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchArticles();
        }
    });
    
    // 编辑表单提交事件
    document.getElementById('editArticleForm').addEventListener('submit', async function(e) {
        e.preventDefault();
        
        const formData = {
            articleId: document.getElementById('editArticleId').value,
            title: document.getElementById('editTitle').value,
            doctor_name: document.getElementById('editAuthor').value,
            tags: document.getElementById('editTag').value,
            content: document.getElementById('editContent').value
        };
        
        // 验证表单
        if (!formData.title.trim()) {
            showMessage('请输入文章标题', 'error');
            return;
        }
        
        if (!formData.doctor_name.trim()) {
            showMessage('请输入作者姓名', 'error');
            return;
        }
        
        if (!formData.tags) {
            showMessage('请选择文章标签', 'error');
            return;
        }
        
        if (!formData.content.trim()) {
            showMessage('请输入文章内容', 'error');
            return;
        }
        
        if (formData.content.trim().length < 10) {
            showMessage('文章内容至少需要10个字符', 'error');
            return;
        }
        
        showLoading(true);
        
        try {
            await updateArticle(formData);
        } finally {
            showLoading(false);
        }
    });
    
    // 模态框点击外部关闭
    document.getElementById('editModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeEditModal();
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