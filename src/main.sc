require: weather-api.js
    type = scriptEs6
    name = weatherAPI

theme: /

    state: Start
        q!: $regex</start>
        a: Привет! Я могу рассказать тебе о погоде в любом городе. Просто спроси меня о погоде в каком-либо городе!

    state: GetWeather
        event!: textEvent
        scriptEs6:
            const userQuery = $request.data.text.toLowerCase();  // Приводим запрос к нижнему регистру
            const cityRegex = /погода в ([а-яё\s]+)/;  // Регулярное выражение для поиска города с кириллицей
            const match = userQuery.match(cityRegex);  // Ищем название города в запросе
            
            if (match && match[1]) {
                const city = match[1].trim();  // Извлекаем город из запроса и удаляем лишние пробелы
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
