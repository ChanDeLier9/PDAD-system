// 获取当前医生ID
const currentDoctorId = localStorage.getItem('doctorId');

// API基础URL配置
const API_BASE_URL = '/api';

// 添加定时器变量
let refreshTimer = null;

// 页面加载时获取最近联系人
document.addEventListener('DOMContentLoaded', function() {
    loadRecentContacts();
    
    // 启动自动刷新
    startAutoRefresh();
    
    // 角色选择变化时更新输入框提示
    const roleSelect = document.getElementById('roleSelect');
    const searchInput = document.getElementById('searchInput');
    
    roleSelect.addEventListener('change', function() {
        const selectedRole = this.value;
        if (selectedRole === 'patient') {
            searchInput.placeholder = '输入患者姓名进行搜索';
        } else if (selectedRole === 'doctor') {
            searchInput.placeholder = '输入医生姓名进行搜索';
        }
        
        // 清空之前的搜索结果
        const searchResult = document.getElementById('searchResult');
        searchResult.classList.add('hidden');
        searchResult.innerHTML = '';
    });
});

// 页面卸载时清理定时器
window.addEventListener('beforeunload', function() {
    stopAutoRefresh();
});

// 页面获得焦点时刷新数据
window.addEventListener('focus', function() {
    loadRecentContacts();
});

// 启动自动刷新
function startAutoRefresh() {
    // 清除现有定时器
    stopAutoRefresh();
    
    // 每5秒刷新一次联系人列表
    refreshTimer = setInterval(() => {
        loadRecentContacts();
    }, 5000);
}

// 停止自动刷新
function stopAutoRefresh() {
    if (refreshTimer) {
        clearInterval(refreshTimer);
        refreshTimer = null;
    }
}

// 加载最近联系人
async function loadRecentContacts() {
    try {
        // 使用完整的URL包含端口号
        const response = await fetch(`${API_BASE_URL}/chat/${currentDoctorId}/contacts`);
        const result = await response.json();
        console.log('后端返回数据：', result);

        if (result.code === 0) {
            if (result.data && result.data.length > 0) {
                displayContacts(result.data);
            } else {
                document.getElementById('contactsList').innerHTML =
                    '<div class="no-contacts">暂无联系人</div>';
            }
        } else {
            console.error('获取联系人失败:', result.message);
            document.getElementById('contactsList').innerHTML =
                `<div class="no-contacts">获取联系人失败：${result.message}</div>`;
        }
    } catch (error) {
        console.error('网络错误:', error);
        // 添加更详细的错误信息
        document.getElementById('contactsList').innerHTML =
            `<div class="no-contacts">加载失败：${error.message}<br>请检查网络连接和后端服务</div>`;
    }
}

// 显示联系人列表（优化数据比较，避免不必要的DOM更新）
let lastContactsData = null;

function displayContacts(contacts) {
    const contactsList = document.getElementById('contactsList');
    
    if (!contacts || contacts.length === 0) {
        contactsList.innerHTML = '<div class="no-contacts">暂无联系人</div>';
        return;
    }
    
    // 比较数据是否有变化，避免不必要的DOM更新
    const currentDataString = JSON.stringify(contacts);
    if (lastContactsData === currentDataString) {
        return; // 数据没有变化，不更新DOM
    }
    lastContactsData = currentDataString;
    
    contactsList.innerHTML = contacts.map(contact => `
        <div class="contact-item" onclick="startChat('${contact.userId}', '${contact.username}')">
            <div class="contact-info">
                <div class="contact-name">${contact.username}</div>
                <div class="last-message">${contact.lastMessage || '暂无消息'}</div>
                <div class="message-time">${formatTime(contact.timestamp)}</div>
            </div>
            ${contact.unreadCount > 0 ? `<div class="unread-badge">${contact.unreadCount}</div>` : ''}
        </div>
    `).join('');
}

// 搜索用户
async function searchUser() {
    const searchInput = document.getElementById('searchInput');
    const searchResult = document.getElementById('searchResult');
    const roleSelect = document.getElementById('roleSelect');
    const searchName = searchInput.value.trim();
    const selectedRole = roleSelect.value;
    
    if (!searchName) {
        const roleText = selectedRole === 'patient' ? '患者' : '医生';
        alert(`请输入${roleText}姓名`);
        return;
    }
    
    try {
        const searchUrl = `${API_BASE_URL}/chat/find-user?role=${selectedRole}&name=${encodeURIComponent(searchName)}`;
        console.log('搜索请求:', searchUrl);
        const response = await fetch(searchUrl);
        
        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
        
        const result = await response.json();
        console.log('搜索结果:', result);
        
        // 添加调试信息
        console.log('返回数据结构:', JSON.stringify(result));
        
        // 检查返回的数据格式，兼容不同的成功状态表示
        const isSuccess = result.success || result.code === 0;
        const userData = result.data || result.user || result;
        
        if (isSuccess && userData) {
            // 确保userData中包含必要的字段
            const userId = userData.userId || userData.id || '';
            const username = userData.username || userData.name || searchName;
            
            console.log('处理后的用户数据:', {userId, username, role: selectedRole});
            
            if (userId) {
                const roleText = selectedRole === 'patient' ? '患者' : '医生';
                searchResult.innerHTML = `
                    <div class="search-item" onclick="startChat('${userId}', '${username}')">
                        <div class="contact-info">
                            <div class="contact-name">${username} (${roleText})</div>
                            <div class="last-message">点击开始聊天</div>
                        </div>
                    </div>
                `;
                searchResult.classList.remove('hidden');
                console.log('搜索结果已显示');
            } else {
                searchResult.innerHTML = `
                    <div class="search-item">
                        <div class="contact-info">
                            <div class="contact-name" style="color: #999;">找到用户但缺少ID信息，无法开始聊天</div>
                        </div>
                    </div>
                `;
                searchResult.classList.remove('hidden');
            }
        } else {
            const message = result.message || '未找到匹配的用户';
            searchResult.innerHTML = `
                <div class="search-item">
                    <div class="contact-info">
                        <div class="contact-name" style="color: #999;">${message}</div>
                    </div>
                </div>
            `;
            searchResult.classList.remove('hidden');
        }
    } catch (error) {
        console.error('搜索失败:', error);
        alert(`搜索失败：${error.message}`);
    }
}

// 开始聊天（添加刷新联系人列表的逻辑）
function startChat(userId, username) {
    // 跳转到聊天界面，传递用户信息
    location.href = `./chat.html?userId=${userId}&username=${encodeURIComponent(username)}`;
    
    // 延迟刷新联系人列表，确保新的聊天记录被加载
    setTimeout(() => {
        loadRecentContacts();
    }, 1000);
}

// 格式化时间
function formatTime(timestamp) {
    if (!timestamp) return '';
    
    const date = new Date(timestamp);
    const now = new Date();
    const diff = now - date;
    
    if (diff < 24 * 60 * 60 * 1000) { // 24小时内
        return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
    } else {
        return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' });
    }
}

// 回车搜索
document.getElementById('searchInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        searchUser();
    }
});

// 手动刷新按钮（可选）
function manualRefresh() {
    loadRecentContacts();
}