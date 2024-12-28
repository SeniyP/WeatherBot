require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Здравствуйте! Я могу рассказать погоду. Например, скажите: "Какая погода в Москве?"

    state: Request
        intent!: /Погода
        script:
            # Извлекаем город из сущности
            $session.city = $context.entities.city;
            
            # Проверяем, что город передан
            if ($session.city) {
                # Делаем запрос на погоду
                HttpRequest:
                    url = "http://api.weatherapi.com/v1/current.json?key=50aa229c887e47dd8c631208240411&q=" + $session.city
                    method = "GET"
                    body = ""
                    okState = "WeatherResponse"
                    errorState = "WeatherError"
                    timeout = 0
                    headers = []
            } else {
                a: "Пожалуйста, уточните город. Например, 'Какая погода в Москве?'"
                go: "NoMatch"
            }

    state: WeatherResponse
        event!: httpSuccess
        script:
            # Разбираем ответ от API
            $data = $parse.json($response.body);
            $location = $data.location.name;
            $temp = $data.current.temp_c;
            
            a: "Сейчас в {{$location}} температура: {{$temp}}°C."
        
        state: Yes
            q: * (Да|да) *
            go: "Start"
        state: No
            q: * (нет|Нет) *
            go: "Bye"

    state: WeatherError
        event!: httpError
        a: "Не удалось получить данные о погоде. Попробуйте снова."
        go: "Start"

    state: NoMatch
        event!: noMatch
        a: "Я не понял. Повторите Ваш запрос."

    state: Bye
        intent!: /пока
        a: "До свидания."
