module.exports = {
    async getWeather(city) {
        const fetch = require('node-fetch');  // Используем fetch для запроса данных
        const url = `http://api.weatherapi.com/v1/current.json?key=50aa229c887e47dd8c631208240411&q=${city}`;
        
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error("Unable to fetch weather data.");
        }
        
        const data = await response.json();
        return {
            temperature: data.current.temp_c,
            condition: data.current.condition.text
        };
    }
};
