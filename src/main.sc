require: weather-api.js
    type = scriptEs6
    name = weatherApi

theme: /

    state: Start
        q!: $regex</start>
        a: Привет! Я могу рассказать погоду. Просто скажите, например: 'Какая погода в Москве?'

    state: WeatherRequest
        intent!: /Погода
        scriptEs6:
            const city = $context.entities.city;
            if (!city) {
                $reactions.answer("Пожалуйста, уточните город, например: 'Какая погода в Москве?'");
                return;
            }

            try {
                // Получаем данные о погоде для указанного города
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

    state: NoMatch || noContext = true
        event!: noMatch
        a: Извините, я не понял. Пожалуйста, уточните запрос, например: 'Какая погода в Москве?'

    state: WeatherError || noContext = true
        event!: httpError
        a: Не удалось получить данные о погоде. Попробуйте позже!
