const API_BASE_URL = 'http://localhost:8081';

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
    const styleElement = document.createElement('style');
    styleElement.textContent = `
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
            background: linear-gradient(to bottom, #87CEEB 0%, #87CEFA 50%, #B0E0E6 100%);
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 20px;
            padding-bottom: 80px;
            position: relative;
            overflow-x: hidden;
        }
        .clouds {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            pointer-events: none;
            z-index: 0;
        }
        .cloud {
            position: absolute;
            background: rgba(255, 255, 255, 0.8);
            border-radius: 50px;
            opacity: 0.7;
        }
        .cloud:before,
        .cloud:after {
            content: '';
            position: absolute;
            background: rgba(255, 255, 255, 0.8);
            border-radius: 50px;
        }
        .cloud1 {
            width: 80px;
            height: 30px;
            top: 20%;
            left: -100px;
            animation: float1 20s infinite linear;
        }
        .cloud1:before {
            width: 50px;
            height: 50px;
            top: -25px;
            left: 10px;
        }
        .cloud1:after {
            width: 60px;
            height: 40px;
            top: -20px;
            right: 10px;
        }
        .cloud2 {
            width: 100px;
            height: 35px;
            top: 40%;
            left: -120px;
            animation: float2 25s infinite linear;
            animation-delay: 2s;
        }
        .cloud2:before {
            width: 60px;
            height: 60px;
            top: -30px;
            left: 15px;
        }
        .cloud2:after {
            width: 70px;
            height: 50px;
            top: -25px;
            right: 15px;
        }
        .cloud3 {
            width: 70px;
            height: 25px;
            top: 60%;
            left: -90px;
            animation: float3 18s infinite linear;
            animation-delay: 5s;
        }
        .cloud3:before {
            width: 45px;
            height: 45px;
            top: -22px;
            left: 8px;
        }
        .cloud3:after {
            width: 55px;
            height: 35px;
            top: -18px;
            right: 8px;
        }
        .cloud4 {
            width: 90px;
            height: 30px;
            top: 30%;
            left: -110px;
            animation: float4 22s infinite linear;
            animation-delay: 8s;
        }
        .cloud4:before {
            width: 55px;
            height: 55px;
            top: -27px;
            left: 12px;
        }
        .cloud4:after {
            width: 65px;
            height: 45px;
            top: -22px;
            right: 12px;
        }
        @keyframes float1 {
            0% {
                transform: translateX(0);
            }
            100% {
                transform: translateX(calc(100vw + 100px));
            }
        }
        @keyframes float2 {
            0% {
                transform: translateX(0);
            }
            100% {
                transform: translateX(calc(100vw + 120px));
            }
        }
        @keyframes float3 {
            0% {
                transform: translateX(0);
            }
            100% {
                transform: translateX(calc(100vw + 90px));
            }
        }
        @keyframes float4 {
            0% {
                transform: translateX(0);
            }
            100% {
                transform: translateX(calc(100vw + 110px));
            }
        }
        .snowflakes {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            pointer-events: none;
            z-index: 0;
        }
        .snowflake {
            position: absolute;
            color: white;
            font-size: 1em;
            font-family: Arial;
            text-shadow: 0 0 5px rgba(255, 255, 255, 0.8);
            top: -10px;
            animation: fall linear infinite;
            opacity: 0.8;
        }
        @keyframes fall {
            to {
                transform: translateY(100vh) rotate(360deg);
            }
        }
        .snowflakes.hidden,
        .clouds.hidden {
            display: none;
        }
        .container {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 0;
            box-shadow: 0 8px 32px rgba(40, 50, 60, 0.5);
            padding: 40px;
            max-width: 800px;
            width: 100%;
            backdrop-filter: blur(10px);
            margin-bottom: 20px;
            position: relative;
            z-index: 1;
        }
        h1 {
            color: #4a6a7a;
            margin-bottom: 30px;
            text-align: center;
            font-size: 28px;
            font-weight: 300;
        }
        .search-container {
            display: flex;
            gap: 10px;
            margin-bottom: 30px;
        }
        input[type="text"] {
            flex: 1;
            padding: 12px;
            border: 2px solid #5a7a8a;
            border-radius: 0;
            font-size: 16px;
            background: white;
            color: #4a6a7a;
        }
        input[type="text"]:focus {
            outline: none;
            border-color: #4a6a7a;
        }
        button {
            padding: 12px 30px;
            background: linear-gradient(135deg, #5a7a8a 0%, #4a6a7a 100%);
            color: white;
            border: none;
            border-radius: 0;
            font-size: 16px;
            font-weight: 400;
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s;
            box-shadow: 0 4px 15px rgba(40, 50, 60, 0.5);
        }
        button:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(40, 50, 60, 0.7);
            background: linear-gradient(135deg, #4a6a7a 0%, #3a5a6a 100%);
        }
        button:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }
        .weather-info {
            margin-top: 30px;
            padding: 20px;
            background: rgba(240, 245, 250, 0.7);
            border-radius: 0;
            display: none;
            border: 1px solid rgba(90, 122, 138, 0.3);
        }
        .weather-info.show {
            display: block;
        }
        .city-name {
            font-size: 24px;
            color: #4a6a7a;
            margin-bottom: 10px;
            font-weight: 500;
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
        }
        .city-info-left {
            display: flex;
            flex-direction: column;
        }
        .city-info-right {
            display: flex;
            flex-direction: column;
            align-items: flex-end;
        }
        .city-name-text {
            font-size: 24px;
            font-weight: 500;
            color: #4a6a7a;
            line-height: 1;
            margin: 0;
            padding: 0;
        }
        .current-temp {
            font-size: 24px;
            font-weight: 700;
            color: #4a6a7a;
            line-height: 1;
            margin: 0;
            padding: 0;
        }
        .current-time {
            font-size: 14px;
            font-weight: 400;
            color: #6a8a9a;
            line-height: 1;
            margin: 0;
            margin-top: 5px;
            padding: 0;
        }
        .coordinates {
            font-size: 14px;
            color: #6a8a9a;
            line-height: 1;
            margin: 0;
            margin-top: 5px;
            margin-bottom: 20px;
            padding: 0;
        }
        .chart-container {
            margin-top: 20px;
            position: relative;
        }
        canvas {
            width: 100%;
            height: 300px;
            border: 1px solid #ddd;
            background: white;
        }
        .error {
            color: #8b4a4a;
            margin-top: 15px;
            padding: 15px;
            background: rgba(240, 220, 220, 0.8);
            border-radius: 0;
            display: none;
            border: 1px solid rgba(139, 74, 74, 0.3);
        }
        .error.show {
            display: block;
        }
        .loading {
            text-align: center;
            color: #6a8a9a;
            margin-top: 20px;
            display: none;
        }
        .loading.show {
            display: block;
        }
        .footer {
            padding: 15px 30px;
            text-align: center;
            color: rgba(255, 255, 255, 0.9);
            font-size: 14px;
            background: rgb(40, 50, 60);
            border-radius: 0;
            width: 100%;
            position: fixed;
            bottom: 0;
            left: 0;
            right: 0;
            z-index: 1;
        }
    `;
    document.head.appendChild(styleElement);
}

function initApp() {
    applyStyles();
    
    const clouds = createElement('div', { class: 'clouds', id: 'clouds' });
    const cloud1 = createElement('div', { class: 'cloud cloud1' });
    const cloud2 = createElement('div', { class: 'cloud cloud2' });
    const cloud3 = createElement('div', { class: 'cloud cloud3' });
    const cloud4 = createElement('div', { class: 'cloud cloud4' });
    clouds.appendChild(cloud1);
    clouds.appendChild(cloud2);
    clouds.appendChild(cloud3);
    clouds.appendChild(cloud4);
    document.body.appendChild(clouds);

    const snowflakes = createElement('div', { class: 'snowflakes hidden', id: 'snowflakes' });
    const snowflakeSymbols = ['❄', '❅', '❆', '✻', '✼', '✽'];
    for (let i = 0; i < 50; i++) {
        const snowflake = createElement('div', { class: 'snowflake' });
        snowflake.textContent = snowflakeSymbols[Math.floor(Math.random() * snowflakeSymbols.length)];
        snowflake.style.left = Math.random() * 100 + '%';
        snowflake.style.animationDuration = (Math.random() * 3 + 2) + 's';
        snowflake.style.animationDelay = Math.random() * 5 + 's';
        snowflake.style.fontSize = (Math.random() * 10 + 10) + 'px';
        snowflakes.appendChild(snowflake);
    }
    document.body.appendChild(snowflakes);

    const container = createElement('div', { class: 'container' });
    const title = createElement('h1', {}, 'Прогноз погоды');
    
    const searchContainer = createElement('div', { class: 'search-container' });
    const cityInput = createElement('input', {
        type: 'text',
        id: 'cityInput',
        placeholder: 'Введите название города (например, Москва)'
    });
    
    const searchBtn = createElement('button', { id: 'searchBtn' }, 'Поиск');
    
    const loadingDiv = createElement('div', { class: 'loading', id: 'loading' }, 'Загрузка данных о погоде...');
    const errorDiv = createElement('div', { class: 'error', id: 'error' });
    const weatherInfo = createElement('div', { class: 'weather-info', id: 'weatherInfo' });
    
    const footer = createElement('div', { class: 'footer' }, '© 2026 Weather Forecast Service');
    
    searchContainer.appendChild(cityInput);
    searchContainer.appendChild(searchBtn);
    
    container.appendChild(title);
    container.appendChild(searchContainer);
    container.appendChild(loadingDiv);
    container.appendChild(errorDiv);
    container.appendChild(weatherInfo);
    
    document.body.appendChild(container);
    document.body.appendChild(footer);
    
    searchBtn.addEventListener('click', () => {
        const city = cityInput.value.trim();
        if (city) {
            fetchWeather(city);
        }
    });
    
    cityInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            const city = cityInput.value.trim();
            if (city) {
                fetchWeather(city);
            }
        }
    });
}

async function fetchWeather(city) {
    hideError();
    hideWeatherInfo();
    showLoading();
    
    const searchBtn = document.getElementById('searchBtn');
    searchBtn.disabled = true;
    
    try {
        const response = await fetch(`${API_BASE_URL}/weather?city=${encodeURIComponent(city)}`);
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({ error: 'Ошибка получения данных' }));
            throw new Error(errorData.error || 'Ошибка получения данных о погоде');
        }
        
        const data = await response.json();
        console.log('Получены данные о погоде:', data);
        if (!data.temperatures || !data.timeLabels) {
            throw new Error('Неполные данные о погоде');
        }
        showWeather(data);
    } catch (error) {
        showError(error.message);
    } finally {
        hideLoading();
        searchBtn.disabled = false;
    }
}

function showWeather(data) {
    const weatherInfo = document.getElementById('weatherInfo');
    weatherInfo.innerHTML = '';

    const clouds = document.getElementById('clouds');
    const snowflakes = document.getElementById('snowflakes');
    const temperature = data.currentTemperature || (data.temperatures && data.temperatures[0]) || 0;
    
    if (temperature < 0) {
        if (clouds) clouds.classList.add('hidden');
        if (snowflakes) snowflakes.classList.remove('hidden');
    } else {
        if (clouds) clouds.classList.remove('hidden');
        if (snowflakes) snowflakes.classList.add('hidden');
    }
    
    const cityName = createElement('div', { class: 'city-name' });
    
    const cityInfoLeft = createElement('div', { class: 'city-info-left' });
    const cityNameText = createElement('div', { class: 'city-name-text' }, data.city);
    const coordinates = createElement('div', { class: 'coordinates' }, 
        `Координаты: ${data.latitude.toFixed(4)}, ${data.longitude.toFixed(4)}`);
    cityInfoLeft.appendChild(cityNameText);
    cityInfoLeft.appendChild(coordinates);
    
    const cityInfoRight = createElement('div', { class: 'city-info-right' });
    const currentTemp = createElement('div', { class: 'current-temp' }, 
        Math.round(temperature) + '°C');
    const currentTime = createElement('div', { class: 'current-time', id: 'currentTime' });
    updateCurrentTime(currentTime, data.timezone);
    cityInfoRight.appendChild(currentTemp);
    cityInfoRight.appendChild(currentTime);
    
    cityName.appendChild(cityInfoLeft);
    cityName.appendChild(cityInfoRight);
    
    const chartContainer = createElement('div', { class: 'chart-container' });
    const canvas = createElement('canvas', { id: 'temperatureChart' });
    
    weatherInfo.appendChild(cityName);
    weatherInfo.appendChild(chartContainer);
    chartContainer.appendChild(canvas);
    
    weatherInfo.classList.add('show');

    setTimeout(() => {
        drawChart(canvas, data.temperatures, data.timeLabels);
    }, 10);

    if (window.timeUpdateInterval) {
        clearInterval(window.timeUpdateInterval);
    }
    window.timeUpdateInterval = setInterval(() => {
        updateCurrentTime(currentTime, data.timezone);
    }, 1000);
}

function updateCurrentTime(timeElement, timezone) {
    if (!timeElement) return;
    
    let now;
    if (timezone) {
        try {
            const formatter = new Intl.DateTimeFormat('ru-RU', {
                timeZone: timezone,
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit',
                hour12: false
            });
            const parts = formatter.formatToParts(new Date());
            const hours = parts.find(p => p.type === 'hour').value;
            const minutes = parts.find(p => p.type === 'minute').value;
            const seconds = parts.find(p => p.type === 'second').value;
            timeElement.textContent = `${hours}:${minutes}:${seconds}`;
            return;
        } catch (e) {
            console.warn('Ошибка определения часового пояса:', e);
        }
    }

    now = new Date();
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');
    timeElement.textContent = `${hours}:${minutes}:${seconds}`;
}

function drawChart(canvas, temperatures, timeLabels) {
    if (!temperatures || !timeLabels || temperatures.length === 0) {
        console.error('Нет данных для графика:', { temperatures, timeLabels });
        return;
    }
    
    console.log('Рисуем график:', { 
        temperaturesCount: temperatures.length, 
        timeLabelsCount: timeLabels.length,
        temps: temperatures.slice(0, 5)
    });
    
    const ctx = canvas.getContext('2d');

    const containerWidth = canvas.parentElement ? canvas.parentElement.offsetWidth : 700;
    const width = canvas.width = containerWidth || 700;
    const height = canvas.height = 300;
    
    if (width <= 0) {
        console.error('Некорректная ширина canvas:', width);
        return;
    }
    
    const padding = 60;
    const chartWidth = width - padding * 2;
    const chartHeight = height - padding * 2;
    
    const minTemp = Math.min(...temperatures);
    const maxTemp = Math.max(...temperatures);
    const tempRange = maxTemp - minTemp || 1;
    
    const xStep = chartWidth / (temperatures.length - 1 || 1);
    
    ctx.clearRect(0, 0, width, height);
    ctx.fillStyle = 'white';
    ctx.fillRect(0, 0, width, height);
    
    ctx.strokeStyle = '#ddd';
    ctx.lineWidth = 1;
    
    for (let i = 0; i <= 4; i++) {
        const y = padding + (chartHeight / 4) * i;
        ctx.beginPath();
        ctx.moveTo(padding, y);
        ctx.lineTo(width - padding, y);
        ctx.stroke();
        
        const temp = maxTemp - (tempRange / 4) * i;
        ctx.fillStyle = '#666';
        ctx.font = '12px sans-serif';
        ctx.textAlign = 'right';
        ctx.fillText(Math.round(temp) + '°C', padding - 10, y + 4);
    }
    
    for (let i = 0; i < timeLabels.length; i += 2) {
        const x = padding + xStep * i;
        ctx.fillStyle = '#666';
        ctx.font = '11px sans-serif';
        ctx.textAlign = 'center';
        ctx.fillText(timeLabels[i], x, height - padding + 20);
    }

    ctx.strokeStyle = '#4a6a7a';
    ctx.fillStyle = '#5a7a8a';
    ctx.lineWidth = 2;
    
    ctx.beginPath();
    for (let i = 0; i < temperatures.length; i++) {
        const x = padding + xStep * i;
        const normalizedTemp = (temperatures[i] - minTemp) / tempRange;
        const y = padding + chartHeight - (normalizedTemp * chartHeight);
        
        if (i === 0) {
            ctx.moveTo(x, y);
        } else {
            ctx.lineTo(x, y);
        }
    }
    ctx.stroke();

    for (let i = 0; i < temperatures.length; i++) {
        const x = padding + xStep * i;
        const normalizedTemp = (temperatures[i] - minTemp) / tempRange;
        const y = padding + chartHeight - (normalizedTemp * chartHeight);
        
        ctx.fillStyle = '#5a7a8a';
        ctx.beginPath();
        ctx.arc(x, y, 3, 0, Math.PI * 2);
        ctx.fill();
    }

    ctx.strokeStyle = '#4a6a7a';
    ctx.lineWidth = 1;
    ctx.beginPath();
    ctx.moveTo(padding, padding);
    ctx.lineTo(padding, height - padding);
    ctx.lineTo(width - padding, height - padding);
    ctx.stroke();
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

function showLoading() {
    const loadingDiv = document.getElementById('loading');
    loadingDiv.classList.add('show');
}

function hideLoading() {
    const loadingDiv = document.getElementById('loading');
    loadingDiv.classList.remove('show');
}

function hideWeatherInfo() {
    const weatherInfo = document.getElementById('weatherInfo');
    weatherInfo.classList.remove('show');
}

document.addEventListener('DOMContentLoaded', initApp);

