require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

intents:
  - /погода
    examples:
      - Какая погода в {city}
      - Погода в {city}
      - Что с погодой в {city}
      - Как там погода в {city}

entities:
  - city: 
      examples:
        - Москва
        - Санкт-Петербург
        - Лондон
        - Париж

states:
    state: Start
        q!: $regex</start>
        a: Привет! Я могу рассказать погоду. Просто спросите, например: "Какая погода в Москве?"

    state: Weather
        intent!: /погода
        a: Сейчас уточню погоду для города {{$context.entities.city}}...
        script:
            $city = $context.entities.city;
            if ($city) {
                $http.get("http://api.weatherapi.com/v1/current.json", {
                    "key": "50aa229c887e47dd8c631208240411",
                    "q": $city.trim()
                });
            } else {
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
