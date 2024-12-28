require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Начнём.

    state: Hello
        intent!: /привет
        a: Привет привет

    state: Bye
        intent!: /пока
        a: Пока пока

    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}}

    state: Match
        event!: match
        a: {{$context.intent.answer}}

    state: Weather
        intent!: /погода
        a: Дайте мне секунду, я проверю погоду для вас...
        event: weather_request

event: weather_request
    action: get_weather

action: get_weather
    url: "http://api.weatherapi.com/v1/current.json?key=50aa229c887e47dd8c631208240411&q=Москва"
    method: GET
    response: $.current
    result_mapping:
        temperature: $.temp_c
        condition: $.condition.text

    result: Погода на текущий момент: {{$result.temperature}}°C, {{$result.condition}}.
