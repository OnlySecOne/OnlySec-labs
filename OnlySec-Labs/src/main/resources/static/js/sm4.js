// SM4 加密相关的工具函数
(function() {
    // 等待库加载完成
    function waitForGMCrypt(callback, maxAttempts = 10) {
        let attempts = 0;
        const checkInterval = setInterval(function() {
            attempts++;
            if (window.GMCrypt && window.GMCrypt.SM4) {
                clearInterval(checkInterval);
                console.log('GMCrypt 库加载成功');
                callback(true);
            } else if (attempts >= maxAttempts) {
                clearInterval(checkInterval);
                console.error('GMCrypt 库加载失败');
                callback(false);
            }
        }, 500);
    }

    // 初始化 SM4 工具
    waitForGMCrypt(function(success) {
        if (!success) {
            console.error('无法加载 GMCrypt 库');
            return;
        }

        window.SM4 = {
            encrypt: function(text, key) {
                if (!window.GMCrypt || !window.GMCrypt.SM4) {
                    throw new Error('SM4加密库未加载，请刷新页面重试');
                }
                try {
                    const sm4 = new window.GMCrypt.SM4({
                        key: key,
                        mode: 'ecb',
                        padding: 'pkcs#5',
                        iv: ''
                    });
                    return sm4.encrypt(text);
                } catch (error) {
                    console.error('SM4加密失败:', error);
                    throw new Error('SM4加密失败，请重试');
                }
            },
            decrypt: function(text, key) {
                if (!window.GMCrypt || !window.GMCrypt.SM4) {
                    throw new Error('SM4加密库未加载，请刷新页面重试');
                }
                try {
                    const sm4 = new window.GMCrypt.SM4({
                        key: key,
                        mode: 'ecb',
                        padding: 'pkcs#5',
                        iv: ''
                    });
                    return sm4.decrypt(text);
                } catch (error) {
                    console.error('SM4解密失败:', error);
                    throw new Error('SM4解密失败，请重试');
                }
            }
        };

        // 测试加密功能
        try {
            const testResult = window.SM4.encrypt('test', '1234567890abcdef');
            console.log('SM4加密测试成功');
        } catch (error) {
            console.error('SM4加密测试失败:', error);
        }
    });
})();

// 导出检查函数
window.checkSM4Ready = function() {
    return !!(window.GMCrypt && window.GMCrypt.SM4 && window.SM4);
};

