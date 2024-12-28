require: weather-api.js
    type = scriptEs6
    name = weatherApi

state: WeatherRequest
    intent!: /Погода
    scriptEs6:
        const city = $context.entities.city;
        if (!city) {
            $reactions.answer("Пожалуйста, уточните город, например: 'Какая погода в Москве?'");
            return;
        }

        try {
            const weatherData = await weatherApi.getWeather(city);

            if (!weatherData) {
                $reactions.answer("Не удалось получить данные о погоде. Проверьте название города или попробуйте позже.");
                return;
            }

            const location = weatherData.location.name;
            const temp = weatherData.current.temp_c;
            const condition = weatherData.current.condition.text;

            $reactions.answer(`Сейчас в ${location}: ${temp}°C, ${condition}.`);
        } catch (e) {
            $reactions.answer("Произошла ошибка при получении данных о погоде. Попробуйте позже.");
        }
