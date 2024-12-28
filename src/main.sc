require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Привет! Я могу рассказать погоду. Просто скажите, например: "Какая погода в Москве?"

    state: Weather
        intent!: /Погода
        script:
            $city = $context.entities.city; // Извлекаем город из сущности
            if ($city) {
                $http.get("http://api.weatherapi.com/v1/current.json", {
                    "key": "50aa229c887e47dd8c631208240411",
                    "q": $city.trim()
                });
            } else {
                $response.say("Пожалуйста, уточните город, например: \"Какая погода в Москве?\"");
                $reactions.go("/NoMatch");
            }

    state: WeatherResponse
        event!: httpSuccess
        script:
            $data = $parse.json($response.body);  // Парсим JSON-ответ
            $location = $data.location.name;
            $temp = $data.current.temp_c;
            $condition = $data.current.condition.text;
            $response.say("Сейчас в {{$location}}: {{$temp}}°C, {{$condition}}.");  // Ответ пользователю
        a:

    state: WeatherError
        event!: httpError
        a: Не удалось получить данные о погоде. Проверьте название города или попробуйте позже.

    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}}
