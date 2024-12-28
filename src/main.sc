require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Привет! Я могу рассказать погоду. Просто спросите, например: "Какая погода в Москве?"

    state: Weather
        intent!: /погода
        a: Погода в каком городе вас интересует?
        script:
            $city = $request.query.split('в ')[1];
            if ($city) {
                $http.get("http://api.weatherapi.com/v1/current.json", {
                    "key": "50aa229c887e47dd8c631208240411",
                    "q": $city
                });
            } else {
                $reactions.say("Пожалуйста, уточните город.");
                $reactions.go("/NoMatch");
            }

    state: WeatherResponse
        event!: httpSuccess
        a: Сейчас в {{$parse.json($response.body).location.name}}: {{$parse.json($response.body).current.temp_c}}°C, {{$parse.json($response.body).current.condition.text}}.

    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}}

    state: Match
        event!: match
        a: {{$context.intent.answer}}
