// Web漏洞模块的交互功能
const WebVulnModule = {
    init() {
        this.bindEvents();
        this.initializeCards();
    },

    bindEvents() {
        // 为所有漏洞卡片添加点击效果
        document.querySelectorAll('.vuln-card').forEach(card => {
            card.addEventListener('click', (e) => this.handleCardClick(e, card));
        });
    },

    initializeCards() {
        // 添加卡片出现动画
        document.querySelectorAll('.vuln-card').forEach((card, index) => {
            card.style.opacity = '0';
            card.style.transform = 'translateY(20px)';
            
            setTimeout(() => {
                card.style.transition = 'all 0.3s ease';
                card.style.opacity = '1';
                card.style.transform = 'translateY(0)';
            }, index * 100);
        });
    },

    handleCardClick(e, card) {
        // 添加点击波纹效果
        const ripple = document.createElement('div');
        ripple.classList.add('ripple');
        
        const rect = card.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;
        
        ripple.style.left = `${x}px`;
        ripple.style.top = `${y}px`;
        
        card.appendChild(ripple);
        
        setTimeout(() => ripple.remove(), 1000);
    },

    // 检查实验环境状态
    async checkEnvironmentStatus(vulnType) {
        try {
            const response = await fetch(`/api/environment/status/${vulnType}`);
            const status = await response.json();
            return status.available;
        } catch (error) {
            console.error('检查环境状态失败:', error);
            return false;
        }
    }
};

// 页面加载完成后初始化模块
document.addEventListener('DOMContentLoaded', () => {
    WebVulnModule.init();
});

// Web漏洞模块的折叠功能
document.addEventListener('DOMContentLoaded', function() {
    const moduleHeader = document.querySelector('.module-header');
    const vulnerabilityGrid = document.querySelector('.vulnerability-grid');
    const toggleIcon = document.querySelector('.toggle-icon');

    // 初始状态设置为展开
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
}); 