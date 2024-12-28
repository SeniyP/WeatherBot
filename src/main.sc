require: weather-api.js
    type = scriptEs6
    name = weatherAPI

theme: /

    state: Start
        q!: $regex</start>
        a: Hi! I can provide weather information. Just ask me about the weather in any city!

    state: GetWeather
        event!: textEvent
        scriptEs6:
            const city = $request.data.text;  // Получаем название города из запроса пользователя

            // Проверяем, что город указан
            if (!city) {
                $reactions.answer("Please provide a city name.");
                return;
            }

            try {
                const weatherData = await weatherAPI.getWeather(city);
                const { temperature, condition } = weatherData;

                $reactions.answer(`The current weather in ${city} is ${temperature}°C with ${condition}.`);
            } catch (e) {
                $reactions.answer("Sorry, I couldn't fetch the weather data. Please try again later.");
            }

    state: NoMatch || noContext = true
        event!: noMatch
        a: I’m sorry, I didn’t get it. Please ask for the weather in a city.

    state: RejectFile || noContext = true
        event!: fileTooBigEvent
        a: I’m sorry, I can only accept text queries. Please send a valid city name.

