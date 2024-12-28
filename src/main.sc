require: weather-api.js
    type = scriptEs6
    name = weatherAPI

theme: /

    state: Start
        q!: $regex</start>
        a: Привет! Я могу рассказать тебе о погоде в любом городе. Просто спроси меня о погоде в каком-либо городе!

    state: GetWeather
        event!: intentEvent
        entities:
            - city
        scriptEs6:
            const city = $request.data.entities.city;  // Извлекаем город из сущности
            if (city) {
                try {
                    const weatherData = await weatherAPI.getWeather(city);
                    const { temperature, condition } = weatherData;
                    $reactions.answer(`Текущая погода в городе ${city}: ${temperature}°C, ${condition}.`);
                } catch (e) {
                    $reactions.answer("Извините, я не смог получить данные о погоде. Попробуйте снова позже.");
                }
            } else {
                $reactions.answer("Пожалуйста, уточните город, например, 'Какая погода в Москве?'");
            }

    state: NoMatch || noContext = true
        event!: noMatch
        a: Извините, я не понял ваш запрос. Пожалуйста, спросите о погоде в конкретном городе.

    state: RejectFile || noContext = true
        event!: fileTooBigEvent
        a: Извините, я принимаю только текстовые запросы. Пожалуйста, укажите название города.
