require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Привет! Я могу рассказать погоду. Просто спросите, например: "Какая погода в Москве?"

    state: Weather
        intent!: /Погода
        a: Сейчас уточню погоду для города {{$context.entities.city}}...
        script:
            // Получение города из сущности
            $city = $context.entities.city[0]; // Используем первый элемент из массива сущностей, если их несколько
            
            // Проверка наличия города
            if ($city) {
                // Запрос к API погоды
                $http.get("http://api.weatherapi.com/v1/current.json", {
                    "key": "50aa229c887e47dd8c631208240411",  // Твой API-ключ
                    "q": $city.trim()
                });
            } else {
                // Ответ, если город не найден
                $reactions.say("Пожалуйста, уточните город, например: \"Какая погода в Москве?\"");
                $reactions.go("/NoMatch");
            }

    state: WeatherResponse
        event!: httpSuccess
        a: Сейчас в {{$parse.json($response.body).location.name}}: {{$parse.json($response.body).current.temp_c}}°C, {{$parse.json($response.body).current.condition.text}}.

    state: WeatherError
        event!: httpError
        a: Не удалось получить данные о погоде. Проверьте название города или попробуйте позже.

    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}}

    state: Match
        event!: match
        a: {{$context.intent.answer}}
