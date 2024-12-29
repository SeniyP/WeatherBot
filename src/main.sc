require: functions.js

theme: /

    state: Start
        q!: $regex</start>
        a: Привет! Я электронный помощник. Я могу сообщить вам текущую погоду в любом городе. Напишите город.
        
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
                    $reactions.answer("Что-то сервер барахлит. Не могу узнать погоду.");
                }
            }).catch(function (err) {
                $reactions.answer("Что-то сервер барахлит. Не могу узнать погоду.");
            });
        a: Могу я помочь чем то еще?
    
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
                    $reactions.answer("Что-то сервер барахлит. Не могу узнать полную информацию о погоде.");
                }
            }).catch(function (err) {
                $reactions.answer("Что-то сервер барахлит. Не могу узнать полную информацию о погоде.");
            });
        a: Могу я помочь чем то еще?

    state: ForecastWeather
        intent!: /date
        script:
            var city = $caila.inflect($parseTree._geo, ["nomn"]);
            var date = $parseTree._date.value;
    
            openWeatherMapForecast("metric", "ru", city).then(function (res) {
                if (res && res.list) {
                    // Преобразование даты в UNIX timestamp
                    var targetTimestamp = new Date(date).getTime() / 1000;
    
                    // Поиск ближайшей записи
                    var forecast = res.list.find(function (entry) {
                        return Math.abs(entry.dt - targetTimestamp) < 43200; // Разница меньше 12 часов
                    });
    
                    if (forecast) {
                        var temperature = Math.round(forecast.main.temp);
                        var description = forecast.weather[0].description;
    
                        $reactions.answer("Поиск информации " + capitalize(city) + ", " + date + ": " + description + ", " + temperature + "°C");
                    } else {
                        $reactions.answer("К сожалению, я не нашел прогноз для города " + capitalize(city) + " на " + date + ".");
                    }
                } else {
                    $reactions.answer("Что-то сервер барахлит. Не могу узнать прогноз погоды.");
                }
            }).catch(function (err) {
                $reactions.answer("Что-то сервер барахлит. Не могу узнать прогноз погоды.");
            });
        a: Могу я помочь чем-то еще?
    
    state: CatchAll || noContext=true
        event!: noMatch
        a: Извините, я вас не понимаю, зато могу рассказать о погоде.
        go: /GetWeather
