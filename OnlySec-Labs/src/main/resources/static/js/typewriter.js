// 代码雨效果类
class MatrixRain {
    constructor(canvasId) {
        this.canvas = document.getElementById(canvasId);
        if (!this.canvas) return; // 如果找不到 canvas 则退出

        this.ctx = this.canvas.getContext('2d');
        this.characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
        this.fontSize = 14;
        this.columns = 0;
        this.drops = [];
        
        // 重要：只获取 welcome-section 的尺寸
        const welcomeSection = document.querySelector('.welcome-section');
        if (!welcomeSection) return;
        
        // 设置 canvas 大小为 welcome-section 的大小
        this.canvas.width = welcomeSection.offsetWidth;
        this.canvas.height = welcomeSection.offsetHeight;
        
        this.columns = Math.floor(this.canvas.width / this.fontSize);
        
        // 监听 welcome-section 的大小变化
        const resizeObserver = new ResizeObserver(entries => {
            for (let entry of entries) {
                this.canvas.width = entry.contentRect.width;
                this.canvas.height = entry.contentRect.height;
                this.columns = Math.floor(this.canvas.width / this.fontSize);
                this.init();
            }
        });
        
        resizeObserver.observe(welcomeSection);
        this.init();
    }

    init() {
        this.drops = [];
        for(let i = 0; i < this.columns; i++) {
            this.drops[i] = Math.random() * -100;
        }
    }

    draw() {
        this.ctx.fillStyle = 'rgba(0, 0, 0, 0.05)';
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);

        this.ctx.fillStyle = '#0F0';
        this.ctx.font = this.fontSize + 'px monospace';

        for(let i = 0; i < this.drops.length; i++) {
            const text = this.characters.charAt(Math.floor(Math.random() * this.characters.length));
            this.ctx.fillText(text, i * this.fontSize, this.drops[i] * this.fontSize);

            if(this.drops[i] * this.fontSize > this.canvas.height && Math.random() > 0.975) {
                this.drops[i] = 0;
            }
            this.drops[i]++;
        }
    }

    animate() {
        this.draw();
        requestAnimationFrame(() => this.animate());
    }
}

// 修改后的打字机效果实现
function typeWriter(element, text, speed = 100, delay = 0) {
    element.textContent = '';
    
    return new Promise(resolve => {
        let i = 0;
        setTimeout(() => {
            const timer = setInterval(() => {
                if (i < text.length) {
                    element.textContent += text.charAt(i);
                    element.scrollTop = element.scrollHeight;
                    i++;
                } else {
                    clearInterval(timer);
                    element.classList.add('typing-done');
                    resolve();
                }
            }, speed);
        }, delay);
    });
}

// 执行打字机效果和代码雨效果
async function runTypewriter() {
    const welcomeSection = document.querySelector('.welcome-section');
    if (!welcomeSection) return;

    // 确保只在 welcome-section 中初始化代码雨
    const matrixCanvas = welcomeSection.querySelector('#matrixCanvas');
    if (matrixCanvas) {
        const matrixRain = new MatrixRain('matrixCanvas');
        matrixRain.animate();
    }
    
    const title = document.getElementById('typewriter-title');
    const text = document.getElementById('typewriter-text');
    
    // 标题打字效果
    await typeWriter(title, '渗透测试工程师宣言', 200);
    
    // 文本打字效果
    const manifesto = `在这个数字化的时代，我们是网络安全的守护者。

以好奇之心探索系统，以敬畏之心对待权限。深入代码的迷宫，穿越防御的壁垒，不是为了破坏，而是为了构建更坚固的堡垒。

我们追寻漏洞的踪迹，不是为了利用它们制造混乱，而是为了在攻击者之前发现并修复它们。在这个过程中，我们始终保持职业操守，遵循道德准则。

技术是一把双刃剑，我们选择用它来保护而不是伤害。每一次测试都是一次学习，每份报告都是一份责任。我们用专业的技能和严谨的态度，守护着无数用户的数字资产和隐私安全。

这条路上没有捷径，唯有持续学习，不断进步。因为黑客的技术在进化，防御的手段也必须升级。我们以此为荣，以此为责。

这，就是我们选择的道路，这，就是渗透测试工程师的使命。`;
    
    await typeWriter(text, manifesto, 80, 800);
}

// 确保在页面完全加载后执行
window.addEventListener('load', runTypewriter); 