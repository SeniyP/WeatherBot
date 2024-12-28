require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: "Привет! Я могу рассказать погоду. Просто скажите, например: 'Какая погода в Москве?'"

    state: Weather
        intent!: /Погода
        script:
            $session.city = $context.entities.city; // Сохранение города в сессии
            if ($session.city) {
                $http.get("http://api.weatherapi.com/v1/current.json", {
                    "key": "50aa229c887e47dd8c631208240411",
                    "q": $session.city.trim()
                });
            } else {
                a: "Пожалуйста, уточните город, например: 'Какая погода в Москве?'";
                // Переход на состояние "NoMatch"
                $state.go("/NoMatch");  // Явный переход через $state
            }

    state: WeatherResponse
        event!: httpSuccess
        script:
            $data = $parse.json($response.body);  // Парсим JSON-ответ
            $location = $data.location.name;
            $temp = $data.current.temp_c;
            $condition = $data.current.condition.text;
            a: "Сейчас в {{$location}}: {{$temp}}°C, {{$condition}}.";

    state: WeatherError
        event!: httpError
        a: "Не удалось получить данные о погоде. Проверьте название города или попробуйте позже.";

    state: NoMatch
        event!: noMatch
        a: "Я не понял. Вы сказали: {{$request.query}}";
