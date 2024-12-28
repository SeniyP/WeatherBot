require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Здравствуйте! Я могу рассказать погоду. Просто скажите, например: "Какая погода в Москве?"
        a: Ваш запрос:

    state: Request
        intent!: /Погода
        script:
            # Сохраняем город в сессии
            $session.city = $context.entities.city;
            
            # Проверяем, был ли передан город
            if ($session.city) {
                # Делаем запрос на погоду
                HttpRequest:
                    url = "http://api.weatherapi.com/v1/current.json?key=50aa229c887e47dd8c631208240411&q=" + $session.city.trim()
                    method = "GET"
                    body = ""
                    okState = "WeatherResponse"
                    errorState = "WeatherError"
                    timeout = 0
                    headers = []
            } else {
                a: "Пожалуйста, уточните город, например: 'Какая погода в Москве?'"
                go: "NoMatch"
            }

    state: WeatherResponse
        event!: httpSuccess
        script:
            # Разбираем полученный ответ от API
            $data = $parse.json($response.body);
            $location = $data.location.name;
            $temp = $data.current.temp_c;
            $condition = $data.current.condition.text;
            a: "Сейчас в {{$location}}: {{$temp}}°C, {{$condition}}."
        inlineButtons:
            {text: "Перейти на сайт погоды", url: "https://www.weatherapi.com"}

        state: Yes
            q: * (Да|да) *
            go: "Start"
        state: No
            q: * (нет|Нет) *
            go: "Bye"

    state: WeatherError
        event!: httpError
        a: "Не удалось получить данные о погоде. Проверьте название города или попробуйте позже."
        go: "Start"

    state: NoMatch
        event!: noMatch
        a: "Я не понял. Вы сказали: {{$request.query}}"
        a: "Повторите Ваш запрос:"

    state: Bye
        intent!: /пока
        a: "До свидания."
