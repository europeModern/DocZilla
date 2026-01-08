const API_BASE_URL = 'http://localhost:8080';

function createElement(tag, attributes = {}, textContent = '') {
    const element = document.createElement(tag);
    Object.entries(attributes).forEach(([key, value]) => {
        if (key === 'style' && typeof value === 'object') {
            Object.assign(element.style, value);
        } else if (key === 'class') {
            element.className = value;
        } else {
            element.setAttribute(key, value);
        }
    });
    if (textContent) {
        element.textContent = textContent;
    }
    return element;
}

function applyStyles() {
    const style = createElement('style', {}, `
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: linear-gradient(135deg, #8b6f5f 0%, #7a5f4f 50%, #6a4f3f 100%);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            padding: 20px 0;
            margin: 0;
        }
        .container {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 0;
            box-shadow: 0 8px 32px rgba(60, 40, 30, 0.5);
            padding: 40px;
            max-width: 500px;
            width: 100%;
            backdrop-filter: blur(10px);
            margin-bottom: 20px;
        }
        h1 {
            color: #5a3c2a;
            margin-bottom: 30px;
            text-align: center;
            font-size: 28px;
            font-weight: 300;
        }
        .upload-area {
            border: 2px dashed #8b6f5f;
            border-radius: 0;
            padding: 30px;
            text-align: center;
            margin-bottom: 20px;
            transition: all 0.3s;
            cursor: pointer;
            background: rgba(200, 180, 160, 0.5);
        }
        .upload-area:hover {
            background: rgba(180, 160, 140, 0.8);
            border-color: #7a5f4f;
        }
        .upload-area.dragover {
            background: rgba(160, 140, 120, 0.9);
            border-color: #6a4f3f;
        }
        input[type="file"] {
            display: none;
        }
        .file-label {
            display: block;
            cursor: pointer;
            color: #6a4f3f;
            font-weight: 500;
            margin-bottom: 10px;
        }
        .file-name {
            color: #5a3f2f;
            font-size: 14px;
            margin-top: 10px;
        }
        button {
            width: 100%;
            padding: 12px;
            background: linear-gradient(135deg, #8b6f5f 0%, #7a5f4f 100%);
            color: white;
            border: none;
            border-radius: 0;
            font-size: 16px;
            font-weight: 400;
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s;
            margin-top: 15px;
            box-shadow: 0 4px 15px rgba(60, 40, 30, 0.5);
        }
        button:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(60, 40, 30, 0.7);
            background: linear-gradient(135deg, #7a5f4f 0%, #6a4f3f 100%);
        }
        button:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }
        .result {
            margin-top: 25px;
            padding: 20px;
            background: rgba(200, 180, 160, 0.7);
            border-radius: 0;
            display: none;
            border: 1px solid rgba(139, 111, 95, 0.5);
        }
        .result.show {
            display: block;
        }
        .result-title {
            color: #5a3c2a;
            font-weight: 500;
            margin-bottom: 10px;
        }
        .link-text {
            width: 100%;
            min-height: 40px;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 0;
            font-size: 14px;
            background: white;
            word-break: break-all;
            cursor: pointer;
            user-select: all;
            color: #5a3c2a;
            margin-top: 15px;
            box-sizing: border-box;
            display: flex;
            align-items: center;
            line-height: 1.4;
        }
        .link-text:hover {
            background: #f5f5f5;
        }
        .download-btn {
            width: 100%;
            margin-top: 10px;
            padding: 10px 20px;
            background: linear-gradient(135deg, #7a5f4f 0%, #6a4f3f 100%);
        }
        .download-btn:hover:not(:disabled) {
            background: linear-gradient(135deg, #6a4f3f 0%, #5a3f2f 100%);
        }
        .error {
            color: #8b4a2a;
            margin-top: 15px;
            padding: 10px;
            background: rgba(180, 150, 130, 0.8);
            border-radius: 0;
            display: none;
            border: 1px solid rgba(139, 74, 42, 0.5);
        }
        .error.show {
            display: block;
        }
        .footer {
            padding: 15px 30px;
            text-align: center;
            color: rgba(255, 255, 255, 0.9);
            font-size: 14px;
            background: rgb(60, 40, 30);
            border-radius: 0;
            width: 100%;
            margin-top: auto;
            position: fixed;
            bottom: 0;
            left: 0;
            right: 0;
        }
    `);
    document.head.appendChild(style);
}

function initApp() {
    applyStyles();

    const container = createElement('div', { class: 'container' });
    const title = createElement('h1', {}, 'File Exchange Service');
    
    const uploadArea = createElement('div', { class: 'upload-area' });
    const fileInput = createElement('input', { type: 'file', id: 'fileInput' });
    const fileLabel = createElement('label', { class: 'file-label', for: 'fileInput' }, 'Выберите файл или перетащите его сюда');
    const fileName = createElement('div', { class: 'file-name', id: 'fileName' });
    
    uploadArea.appendChild(fileLabel);
    uploadArea.appendChild(fileName);
    
    const uploadBtn = createElement('button', { id: 'uploadBtn' }, 'Загрузить файл');
    uploadBtn.disabled = true;
    
    const errorDiv = createElement('div', { class: 'error', id: 'error' });
    const resultDiv = createElement('div', { class: 'result', id: 'result' });
    
    const footer = createElement('div', { class: 'footer' }, '© 2026 File Exchange Service');
    
    let selectedFile = null;
    
    fileInput.addEventListener('change', (e) => {
        selectedFile = e.target.files[0];
        if (selectedFile) {
            fileName.textContent = `Выбран: ${selectedFile.name} (${formatFileSize(selectedFile.size)})`;
            uploadBtn.disabled = false;
            hideError();
        }
    });
    
    uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadArea.classList.add('dragover');
    });
    
    uploadArea.addEventListener('dragleave', () => {
        uploadArea.classList.remove('dragover');
    });
    
    uploadArea.addEventListener('drop', (e) => {
        e.preventDefault();
        uploadArea.classList.remove('dragover');
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            selectedFile = files[0];
            fileInput.files = files;
            fileName.textContent = `Выбран: ${selectedFile.name} (${formatFileSize(selectedFile.size)})`;
            uploadBtn.disabled = false;
            hideError();
        }
    });
    
    uploadArea.addEventListener('click', () => {
        fileInput.click();
    });
    
    uploadBtn.addEventListener('click', async () => {
        if (!selectedFile) return;
        
        uploadBtn.disabled = true;
        uploadBtn.textContent = 'Загрузка...';
        hideError();
        hideResult();
        
        try {
            const formData = new FormData();
            formData.append('file', selectedFile);
            
            const encodedFileName = btoa(unescape(encodeURIComponent(selectedFile.name)));
            
            const response = await fetch(`${API_BASE_URL}/upload`, {
                method: 'POST',
                headers: {
                    'X-File-Name': encodedFileName
                },
                body: selectedFile
            });
            
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({ error: 'Ошибка загрузки' }));
                throw new Error(errorData.error || 'Ошибка загрузки файла');
            }
            
            const data = await response.json();
            showResult(data.linkId, data.downloadUrl);
            uploadBtn.textContent = 'Загрузить файл';
            uploadBtn.disabled = false;
            fileInput.value = '';
            selectedFile = null;
            fileName.textContent = '';
        } catch (error) {
            showError(error.message);
            uploadBtn.textContent = 'Загрузить файл';
            uploadBtn.disabled = false;
        }
    });
    
    container.appendChild(title);
    container.appendChild(fileInput);
    container.appendChild(uploadArea);
    container.appendChild(uploadBtn);
    container.appendChild(errorDiv);
    container.appendChild(resultDiv);
    
    document.body.appendChild(container);
    document.body.appendChild(footer);
}

function showResult(linkId, downloadUrl) {
    const resultDiv = document.getElementById('result');
    resultDiv.innerHTML = '';
    
    const title = createElement('div', { class: 'result-title' }, 'Файл успешно загружен!');
    
    const fullUrl = `${API_BASE_URL}${downloadUrl}`;
    const linkText = createElement('div', {
        class: 'link-text'
    }, fullUrl);
    
    let copyTimeout = null;
    linkText.addEventListener('click', () => {
        const range = document.createRange();
        range.selectNodeContents(linkText);
        const selection = window.getSelection();
        selection.removeAllRanges();
        selection.addRange(range);
        
        document.execCommand('copy');
        selection.removeAllRanges();
        
        const originalText = linkText.textContent;
        linkText.textContent = 'Скопировано!';
        linkText.style.background = '#e8f5e9';
        
        if (copyTimeout) {
            clearTimeout(copyTimeout);
        }
        copyTimeout = setTimeout(() => {
            linkText.textContent = originalText;
            linkText.style.background = 'white';
        }, 2000);
    });
    
    const downloadBtn = createElement('button', { class: 'download-btn' }, 'Скачать файл');
    downloadBtn.addEventListener('click', () => {
        window.location.href = fullUrl;
    });
    
    resultDiv.appendChild(title);
    resultDiv.appendChild(linkText);
    resultDiv.appendChild(downloadBtn);
    resultDiv.classList.add('show');
}

function showError(message) {
    const errorDiv = document.getElementById('error');
    errorDiv.textContent = message;
    errorDiv.classList.add('show');
}

function hideError() {
    const errorDiv = document.getElementById('error');
    errorDiv.classList.remove('show');
}

function hideResult() {
    const resultDiv = document.getElementById('result');
    resultDiv.classList.remove('show');
}

function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

document.addEventListener('DOMContentLoaded', initApp);

