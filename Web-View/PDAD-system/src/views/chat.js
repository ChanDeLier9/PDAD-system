// 获取URL参数
const urlParams = new URLSearchParams(window.location.search);
const chatUserId = urlParams.get('userId');
const chatUsername = urlParams.get('username');
const currentDoctorId = localStorage.getItem('doctorId') || 'doctor_001';
const currentDoctorName = localStorage.getItem('doctorName') || '医生';

// API基础URL配置
const API_BASE_URL = '/api';

// 页面加载时初始化
document.addEventListener('DOMContentLoaded', function() {
    if (!chatUserId || !chatUsername) {
        alert('参数错误');
        location.href = 'chat-contacts.html';
        return;
    }
    
    // 设置聊天标题
    document.getElementById('chatTitle').textContent = `与 ${chatUsername} 的聊天`;
    
    // 加载聊天记录
    loadChatHistory();
    
    // 标记消息为已读
    markMessagesAsRead();
    
    // 设置定时刷新（可选）
    setInterval(loadChatHistory, 5000); // 每5秒刷新一次
});

// 加载聊天记录
async function loadChatHistory() {
    try {
        const response = await fetch(`${API_BASE_URL}/chat/history?user1=${currentDoctorId}&user2=${chatUserId}&limit=50`);
        const result = await response.json();
        
        console.log('聊天记录API返回:', result);
        
        // 兼容不同的返回格式
        const isSuccess = result.success || result.code === 0;
        const messages = result.data || result.messages || [];
        
        if (isSuccess) {
            displayMessages(messages);
        } else {
            const errorMessage = result.message || '获取聊天记录失败';
            console.error('获取聊天记录失败:', errorMessage);
            document.getElementById('chatMessages').innerHTML = 
                `<div class="no-messages">获取聊天记录失败：${errorMessage}</div>`;
        }
    } catch (error) {
        console.error('网络错误:', error);
        document.getElementById('chatMessages').innerHTML = 
            `<div class="no-messages">加载失败：${error.message}</div>`;
    }
}

// 显示消息
function displayMessages(messages) {
    const chatMessages = document.getElementById('chatMessages');
    
    if (!messages || messages.length === 0) {
        chatMessages.innerHTML = '<div class="no-messages">暂无聊天记录</div>';
        return;
    }
    
    // 按时间排序
    messages.sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp));
    
    chatMessages.innerHTML = messages.map(message => {
        const isSent = message.fromUserId === currentDoctorId;
        const messageClass = isSent ? 'sent' : 'received';
        
        return `
            <div class="message ${messageClass}">
                <div class="message-bubble">
                    <div class="message-text">${escapeHtml(message.messageText)}</div>
                    <div class="message-time">${formatMessageTime(message.timestamp)}</div>
                    ${isSent ? `<div class="read-status">${message.isRead ? '已读' : '未读'}</div>` : ''}
                </div>
            </div>
        `;
    }).join('');
    
    // 滚动到底部
    scrollToBottom();
}

// 发送消息
async function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const sendBtn = document.getElementById('sendBtn');
    const messageText = messageInput.value.trim();
    
    if (!messageText) {
        alert('请输入消息内容');
        return;
    }
    
    // 禁用发送按钮
    sendBtn.disabled = true;
    sendBtn.textContent = '发送中...';
    
    try {
        const message = {
            fromUserId: currentDoctorId,
            toUserId: chatUserId,
            messageText: messageText,
            messageType: 'text',
            isRead: false
        };
        
        const response = await fetch(`${API_BASE_URL}/chat/send`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(message)
        });
        
        const result = await response.json();
        
        console.log('发送消息API返回:', result);
        
        // 兼容不同的返回格式
        const isSuccess = result.success || result.code === 0;
        
        if (isSuccess) {
            messageInput.value = '';
            loadChatHistory(); // 重新加载聊天记录
        } else {
            const errorMessage = result.message || '发送失败';
            alert('发送失败: ' + errorMessage);
        }
    } catch (error) {
        console.error('发送消息失败:', error);
        alert('发送失败，请重试');
    } finally {
        // 恢复发送按钮
        sendBtn.disabled = false;
        sendBtn.textContent = '发送';
        messageInput.focus();
    }
}

// 标记消息为已读
async function markMessagesAsRead() {
    try {
        const response = await fetch(`${API_BASE_URL}/chat/mark-read`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                fromUserId: chatUserId,
                toUserId: currentDoctorId
            })
        });
        
        const result = await response.json();
        
        console.log('标记已读API返回:', result);
        
        // 兼容不同的返回格式
        const isSuccess = result.success || result.code === 0;
        
        if (!isSuccess) {
            const errorMessage = result.message || '标记已读失败';
            console.error('标记已读失败:', errorMessage);
        }
    } catch (error) {
        console.error('标记已读失败:', error);
    }
}

// 格式化消息时间
function formatMessageTime(timestamp) {
    const date = new Date(timestamp);
    const now = new Date();
    const diff = now - date;
    
    if (diff < 24 * 60 * 60 * 1000) { // 24小时内
        return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
    } else if (diff < 7 * 24 * 60 * 60 * 1000) { // 7天内
        return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }) + ' ' +
               date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
    } else {
        return date.toLocaleDateString('zh-CN') + ' ' +
               date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
    }
}

// HTML转义
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// 滚动到底部
function scrollToBottom() {
    const chatMessages = document.getElementById('chatMessages');
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

// 回车发送消息
document.getElementById('messageInput').addEventListener('keypress', function(e) {
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
    }
});

// 自动调整输入框高度
document.getElementById('messageInput').addEventListener('input', function() {
    this.style.height = 'auto';
    this.style.height = Math.min(this.scrollHeight, 100) + 'px';
});