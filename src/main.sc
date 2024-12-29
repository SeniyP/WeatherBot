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
    a: Могу я помочь чем-то еще?

state: GetWeatherWithDate
    intent!: /date
    script:
        var city = $caila.inflect($parseTree._geo, ["nomn"]);
        var date = $parseTree._date;  // Извлекаем дату
        var formattedDate = new Date(date.timestamp).toISOString().split('T')[0];  // Преобразуем дату в формат YYYY-MM-DD
        
        openWeatherMapForecast("metric", "ru", city).then(function (res) {
            if (res && res.list) {
                var forecast = res.list.find(function (entry) {
                    var forecastDate = new Date(entry.dt * 1000).toISOString().split('T')[0];
                    return forecastDate === formattedDate;
                });

                if (forecast) {
                    $reactions.answer("Прогноз на " + formattedDate + " для города " + capitalize(city) + ": " + forecast.weather[0].description + ", " + Math.round(forecast.main.temp) + "°C");
                } else {
                    $reactions.answer("Не могу найти прогноз для этой даты.");
                }
            } else {
                $reactions.answer("Что-то сервер барахлит. Не могу узнать прогноз погоды.");
            }
        }).catch(function (err) {
            $reactions.answer("Что-то сервер барахлит. Не могу узнать прогноз погоды.");
        });
    a: Могу я помочь чем-то еще?

state: CloseTask
    a: Могу я помочь чем-то еще?

state: CatchAll || noContext=true
    event!: noMatch
    a: Извините, я вас не понимаю, зато могу рассказать о погоде.
    go: /GetWeather
