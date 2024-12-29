require: functions.js

theme: /

    state: Start
        q!: $regex</start>
        a: Привет! Я электронный помощник. Я могу сообщить вам текущую погоду в любом городе. Напишите город.
    
    state: GetWeather
        intent!: /geo
        script:
            var city = $caila.inflect($parseTree._geo, ["nomn"]);
            openWeatherMapCurrent("metric", "ru", city).then(function (res) {
                if (res && res.weather) {
                    $reactions.answer("Сегодня в городе " + capitalize(city) + " " + res.weather[0].description + ", " + Math.round(res.main.temp) + "°C");
                    if (res.weather[0].main == 'Rain' || res.weather[0].main == 'Drizzle') {
                        $reactions.answer("Советую захватить с собой зонтик!");
                    } else if (Math.round(res.main.temp) < 0) {
                        $reactions.answer("Бррррр ну и мороз!");
                    }
                } else {
                    $reactions.answer("Что-то сервер барахлит. Не могу узнать погоду.");
                }
            }).catch(function (err) {
                $reactions.answer("Что-то сервер барахлит. Не могу узнать погоду.");
            });

    state: GetWeatherByDate
        intent!: /geo-date
        script:
            var city = $caila.inflect($parseTree._geo, ["nomn"]);
            var date = $parseTree._date;
            var formattedDate = formatDate(date.year, date.month, date.day);
            
            // Вставьте ваш API для получения прогноза на конкретную дату, например:
            openWeatherMapForecast("metric", "ru", city, formattedDate).then(function (res) {
                if (res && res.list && res.list.length > 0) {
                    var forecast = res.list[0];  // Прогноз на выбранную дату
                    var temp = Math.round(forecast.main.temp);
                    var description = forecast.weather[0].description;
                    var time = forecast.dt_txt;
                    
                    $reactions.answer("Прогноз на " + formattedDate + " в городе " + capitalize(city) + ": " + 
                        "Время: " + time + ", Температура: " + temp + "°C, Описание: " + description);
                } else {
                    $reactions.answer("Не могу найти прогноз на указанную дату.");
                }
            }).catch(function (err) {
                $reactions.answer("Что-то сервер барахлит. Не могу узнать прогноз на указанную дату.");
            });
    
    state: CatchAll || noContext=true
        event!: noMatch
        a: Извините, я вас не понимаю, зато могу рассказать о погоде. Введите название города
        go: /GetWeather

