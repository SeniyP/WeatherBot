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

state: GetWeatherByDate
    intent!: /geo-date
    script:
        var city = $caila.inflect($parseTree._geo, ["nomn"]);
        var dateStr = formatDate(new Date($parseTree._date.value));
        openWeatherMapForecast("metric", "ru", city, dateStr).then(function (res) {
            if (res && res.list) {
                var forecast = res.list.find(item => item.dt_txt.startsWith(dateStr));
                if (forecast) {
                    var temperature = Math.round(forecast.main.temp);
                    var description = forecast.weather[0].description;

                    $reactions.answer("Прогноз погоды в городе " + capitalize(city) + " на " + dateStr + ":\n" +
                        description + ", температура: " + temperature + "°C.");
                } else {
                    $reactions.answer("Не удалось найти прогноз на указанную дату.");
                }
            } else {
                $reactions.answer("Не удалось получить прогноз на указанную дату.");
            }
        }).catch(function (err) {
            $reactions.answer("Произошла ошибка при запросе прогноза.");
        });

state: CatchAll || noContext=true
    event!: noMatch
    a: Извините, я вас не понимаю, зато могу рассказать о погоде. Введите название города
    go: /GetWeather
