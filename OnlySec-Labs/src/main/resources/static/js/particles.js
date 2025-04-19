class ParticleEffect {
    constructor() {
        this.canvas = document.getElementById('particleCanvas');
        this.ctx = this.canvas.getContext('2d');
        this.particles = [];
        this.mouseX = 0;
        this.mouseY = 0;
        this.isMouseMoving = false;
        
        const welcomeSection = document.querySelector('.welcome-section');
        this.canvas.width = welcomeSection.offsetWidth;
        this.canvas.height = welcomeSection.offsetHeight;
        
        let mouseMoveTimeout;
        welcomeSection.addEventListener('mousemove', (e) => {
            if (mouseMoveTimeout) return;
            mouseMoveTimeout = setTimeout(() => {
                const rect = welcomeSection.getBoundingClientRect();
                this.mouseX = e.clientX - rect.left;
                this.mouseY = e.clientY - rect.top;
                this.isMouseMoving = true;
                
                if (Math.random() > 0.8) {
                    this.addParticle(this.mouseX, this.mouseY, true);
                }
                mouseMoveTimeout = null;
            }, 16);
        });
        
        window.addEventListener('resize', () => {
            this.canvas.width = welcomeSection.offsetWidth;
            this.canvas.height = welcomeSection.offsetHeight;
        });
        
        this.init();
        this.animate();
    }

    addParticle(x, y, isMouseParticle = false) {
        if (this.particles.length >= 60) return;
        
        const particle = {
            x: x || Math.random() * this.canvas.width,
            y: y || Math.random() * this.canvas.height,
            size: isMouseParticle ? Math.random() * 2 + 1 : Math.random() * 1.5 + 0.5,
            speedX: (Math.random() - 0.5) * (isMouseParticle ? 2 : 0.8),
            speedY: (Math.random() - 0.5) * (isMouseParticle ? 2 : 0.8),
            color: '#00ff9d',
            life: isMouseParticle ? 50 : 150,
            maxLife: isMouseParticle ? 50 : 150
        };
        
        this.particles.push(particle);
    }

    init() {
        for (let i = 0; i < 40; i++) {
            this.addParticle();
        }
    }

    animate() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        
        for (let i = this.particles.length - 1; i >= 0; i--) {
            const p = this.particles[i];
            
            p.life--;
            if (p.life <= 0) {
                this.particles.splice(i, 1);
                continue;
            }
            
            p.x += p.speedX;
            p.y += p.speedY;
            
            if (p.x < 0 || p.x > this.canvas.width) p.speedX *= -0.95;
            if (p.y < 0 || p.y > this.canvas.height) p.speedY *= -0.95;
            
            const alpha = p.life / p.maxLife;
            
            this.ctx.beginPath();
            this.ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
            this.ctx.fillStyle = p.color;
            this.ctx.globalAlpha = alpha;
            this.ctx.fill();
            this.ctx.globalAlpha = 1;
            
            for (let j = i + 1; j < Math.min(i + 10, this.particles.length); j++) {
                const p2 = this.particles[j];
                const dx = p2.x - p.x;
                const dy = p2.y - p.y;
                const distance = Math.sqrt(dx * dx + dy * dy);
                
                if (distance < 60) {
                    this.ctx.beginPath();
                    this.ctx.strokeStyle = '#00ff9d';
                    this.ctx.globalAlpha = 0.15 * (1 - distance/60) * alpha;
                    this.ctx.lineWidth = 0.3;
                    this.ctx.moveTo(p.x, p.y);
                    this.ctx.lineTo(p2.x, p2.y);
                    this.ctx.stroke();
                    this.ctx.globalAlpha = 1;
                }
            }
        }
        
        if (this.particles.length < 40 && Math.random() > 0.95) {
            this.addParticle();
        }
        
        requestAnimationFrame(() => this.animate());
    }
}

window.addEventListener('load', () => {
    new ParticleEffect();
}); 