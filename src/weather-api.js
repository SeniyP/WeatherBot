// weather-api.js
require: https-request.js
    type = scriptEs6
    name = httpRequest

const WEATHER_API_KEY = '50aa229c887e47dd8c631208240411';  // Ваш API ключ от WeatherAPI

// Функция для получения данных о погоде
async function getWeather(city) {
    const url = `http://api.weatherapi.com/v1/current.json?key=${WEATHER_API_KEY}&q=${city}`;

    try {
        const response = await httpRequest.get(url);

        // Проверка на успешность ответа
        if (response.statusCode === 200) {
            const data = JSON.parse(response.body);
            return {
                location: data.location,
                current: data.current,
            };
        } else {
            return null;
        }
    } catch (error) {
        console.error('Error fetching weather data:', error);
        return null;
    }
}

// Экспортируем функцию как default
export default {
    getWeather,
};
