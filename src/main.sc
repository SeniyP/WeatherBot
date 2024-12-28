require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: "Привет! Я могу рассказать погоду. Просто скажите, например: 'Какая погода в Москве?'"

    state: Weather
        intent!: /Погода
        script:
            $session.city = $context.entities.city; // Используем сессионную переменную
            if ($session.city) {
                $http.get("http://api.weatherapi.com/v1/current.json", {
                    "key": "50aa229c887e47dd8c631208240411",
                    "q": $session.city.trim()
                });
            } else {
                $actions.reply("Пожалуйста, уточните город, например: 'Какая погода в Москве?'");
                $actions.go("/NoMatch");
            }

    state: WeatherResponse
        event!: httpSuccess
        script:
            $data = $parse.json($response.body);  // Парсим JSON-ответ
            $location = $data.location.name;
            $temp = $data.current.temp_c;
            $condition = $data.current.condition.text;
            $actions.reply("Сейчас в {{$location}}: {{$temp}}°C, {{$condition}}.");

    state: WeatherError
        event!: httpError
        $actions.reply("Не удалось получить данные о погоде. Проверьте название города или попробуйте позже.");

    state: NoMatch
        event!: noMatch
        $actions.reply("Я не понял. Вы сказали: {{$request.query}}");
