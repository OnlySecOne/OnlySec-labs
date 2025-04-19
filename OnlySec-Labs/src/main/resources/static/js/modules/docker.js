// Docker容器管理模块的交互功能
document.addEventListener('DOMContentLoaded', function() {
    // 折叠功能
    const moduleHeader = document.querySelector('.module-header');
    const vulnerabilityGrid = document.querySelector('.vulnerability-grid');
    const toggleIcon = document.querySelector('.toggle-icon');

    let isCollapsed = false;

    moduleHeader.addEventListener('click', function() {
        isCollapsed = !isCollapsed;
        
        if (isCollapsed) {
            vulnerabilityGrid.style.maxHeight = '0';
            vulnerabilityGrid.style.opacity = '0';
            vulnerabilityGrid.style.padding = '0';
            toggleIcon.style.transform = 'rotate(180deg)';
        } else {
            vulnerabilityGrid.style.maxHeight = '2000px';
            vulnerabilityGrid.style.opacity = '1';
            vulnerabilityGrid.style.padding = '1rem 0';
            toggleIcon.style.transform = 'rotate(0)';
        }
    });

    // 初始化加载运行中的环境
    loadRunningEnvironments();
});

// 切换子卡片显示
function toggleSubCards(mainCard) {
    const subCards = mainCard.nextElementSibling;
    const icon = mainCard.querySelector('.difficulty-label i');
    
    mainCard.classList.toggle('expanded');
    subCards.classList.toggle('show');
}

// 选择漏洞环境
function selectVulhub(select) {
    const vulhubPath = select.value;
    if (!vulhubPath) return;

    // 启动环境
    startVulhubEnvironment(vulhubPath);
    
    // 重置选择框
    setTimeout(() => {
        select.value = '';
    }, 1000);
}

// 启动Vulhub环境
async function startVulhubEnvironment(vulhubPath) {
    try {
        showNotification(`正在启动环境: ${vulhubPath}`, 'info');
        
        const response = await fetch('/api/vulhub/start', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ 
                environment: vulhubPath
            })
        });

        if (response.ok) {
            loadRunningEnvironments();
            showNotification(`环境启动成功: ${vulhubPath}`, 'success');
        } else {
            const error = await response.text();
            showNotification(`环境启动失败: ${error}`, 'error');
        }
    } catch (error) {
        console.error('启动环境失败:', error);
        showNotification('环境启动失败: ' + error.message, 'error');
    }
}

// 加载运行中的环境
async function loadRunningEnvironments() {
    try {
        const response = await fetch('/api/vulhub/environments');
        const environments = await response.json();
        updateEnvironmentsList(environments);
    } catch (error) {
        console.error('加载环境列表失败:', error);
        showNotification('加载环境列表失败', 'error');
    }
}

// 停止环境
async function stopEnvironment(envId) {
    try {
        const response = await fetch(`/api/vulhub/stop/${envId}`, {
            method: 'POST'
        });

        if (response.ok) {
            loadRunningEnvironments();
            showNotification('环境已停止', 'success');
        } else {
            const error = await response.text();
            showNotification(`环境停止失败: ${error}`, 'error');
        }
    } catch (error) {
        console.error('停止环境失败:', error);
        showNotification('环境停止失败', 'error');
    }
}

// 更新环境列表显示
function updateEnvironmentsList(environments) {
    const envList = document.querySelector('.environments-list');
    envList.innerHTML = environments.map(env => `
        <div class="environment-card">
            <div class="environment-info">
                <h4>${env.name}</h4>
                <p>漏洞: ${env.cve || '未指定'}</p>
                <p>端口: ${env.port}</p>
                <p>状态: <span class="status-badge running">
                    <i class="fas fa-circle"></i> 运行中
                </span></p>
                <a href="http://localhost:${env.port}" target="_blank" class="visit-btn">
                    <i class="fas fa-external-link-alt"></i> 访问环境
                </a>
            </div>
            <div class="environment-actions">
                <button onclick="stopEnvironment('${env.id}')" class="stop-btn">
                    <i class="fas fa-stop"></i> 停止
                </button>
            </div>
        </div>
    `).join('');
}

function showNotification(message, type) {
    // 简单的通知实现
    alert(message);
} 