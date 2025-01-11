require: functions.js

theme: /

    state: Start
        q!: $regex</start>
        a: Привет!
        go!: /HelpInfo
    
    state: HelpInfo
        intent!: /help
        a: Я электронный помощник. Я могу сообщить вам текущую погоду в любом крупном городе. Вы можете запросить полную информацию, или информацию на конкретную дату.
    
    state: Test
        intent!: /Test
        a: Это тест
        
    state: CloseTask
        a: Могу я помочь чем то еще?
    
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
                $reactions.answer("Что-то сервер не отвечает. Не могу узнать погоду.");
            }
                }).catch(function (err) {
            $reactions.answer("Что-то сервер не отвечает. Не могу узнать погоду.");
                });
        go!: /CloseTask

    state: fullgeo
        intent!: /fullgeo
        script:
            var city = $caila.inflect($parseTree._geo, ["nomn"]);
                openWeatherMapCurrent("metric", "ru", city).then(function (res) {
            if (res && res.weather) {
                var temperature = Math.round(res.main.temp);
                var humidity = res.main.humidity;
                var pressure = res.main.pressure;
                var windSpeed = res.wind.speed;
                var description = res.weather[0].description;
                var windDirection = res.wind.deg;
                var fullWeatherInfo = "Полная информация о погоде в городе " + capitalize(city) + ":\n" +
            "Температура: " + temperature + "°C\n" +
            "Влажность: " + humidity + "%\n" +
            "Давление: " + pressure + " гПа\n" +
            "Скорость ветра: " + windSpeed + " м/с\n" +
            "Направление ветра: " + windDirection + "°\n" +
            "Описание погоды: " + description;
                $reactions.answer(fullWeatherInfo);
            } else {
                $reactions.answer("Что-то сервер не отвечает. Не могу узнать полную информацию о погоде.");
            }
                }).catch(function (err) {
            $reactions.answer("Что-то сервер не отвечает. Не могу узнать полную информацию о погоде.");
                });
        go!: /CloseTask

    state: GetWeatherWithDate
        intent!: /date
        script:
            var city = $caila.inflect($parseTree._geo, ["nomn"]);
                var date = $parseTree._date;
                var formattedDate = new Date(date.timestamp).toISOString().split('T')[0];
                openWeatherMapForecast("metric", "ru", city).then(function (res) {
            if (res && res.list) {

                var forecasts = res.list.filter(function (item) {
            return item.dt_txt.startsWith(formattedDate);
                });
                if (forecasts.length > 0) {
            var weatherInfo = "Прогноз погоды в городе " + capitalize(city) + " на " + formattedDate + ":\n";
            forecasts.forEach(function (forecast) {
                var time = forecast.dt_txt.split(" ")[1].slice(0, 5);
                weatherInfo += time + " — " + forecast.weather[0].description + ", " + Math.round(forecast.main.temp) + "°C\n";
            });
            $reactions.answer(weatherInfo);
                } else {
            $reactions.answer("Нет данных о погоде на указанную дату. (Из-за того что программа бесплатная предел это 5 дней)");
                }
            } else {
                $reactions.answer("Что-то сервер не отвечает. Не могу узнать прогноз.");
            }
                }).catch(function (err) {
            $reactions.answer("Что-то сервер не отвечает. Не могу узнать прогноз.");
                });
        go!: /CloseTask

    state: CatchAll || noContext=true
        event!: noMatch
        a: Извините, я вас не понимаю.
        go: /GetWeather