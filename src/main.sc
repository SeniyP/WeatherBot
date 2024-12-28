require: slotfilling/slotFilling.sc
module = sys.zb-common
theme: /

state: Start
    q!: $regex</start>
    a: Здравствуйте! Я могу рассказать вам о текущей погоде.
    a: Введите или скажите город, для которого хотите узнать погоду.

state: RequestWeather
    intent!: /RequestWeather
    script:
        #$session.city = $parseTree._City
        $session.city = $parseTree._City;
    
    if: $session.city == ""
        go!: /NoMatch
    
    HttpRequest:
        url = http://api.weatherapi.com/v1/current.json?key=50aa229c887e47dd8c631208240411&q={{$session.city}}
        method = GET
        dataType = json
        okState = /WeatherResponse
        errorState = /ErrorRequest
        timeout = 0
        headers = []
        vars = [{"name":"temperature","value":"$httpResponse.current.temp_c"},
                {"name":"condition","value":"$httpResponse.current.condition.text"},
                {"name":"city","value":"$session.city"}]

state: WeatherResponse
    a: В городе {{$session.city}} сейчас {{$temperature}}°C, погода: {{$condition}}.
    a: У Вас есть другие вопросы по погоде?
    state: Yes
        q: * (Да|да) *
        go!: /Start
    state: No
        q: * (нет|Нет) *
        go!: /Bye

state: ErrorRequest
    a: Подключение не удалось. Увы, я не смог получить данные о погоде.
    go!: /Bye

state: NoMatch
    event!: noMatch
    a: Я не понял ваш запрос. Повторите, пожалуйста.
