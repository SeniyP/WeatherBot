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
            var date = $parseTree._duckling_date;
            if (!date || !date.value) {
                $reactions.answer("Пожалуйста, укажите дату.");
                return;
            }

            var formattedDate = new Date(date.value);
            // Преобразуем дату в строку формата для запроса в API (например, "2024-12-31")
            var formattedDateString = formattedDate.toISOString().split('T')[0];

            // Запрос на погоду по дате
            openWeatherMapForecast("metric", "ru", city, formattedDateString).then(function (res) {
                if (res && res.list) {
                    var weatherForDate = res.list.find(function (item) {
                        return item.dt_txt.startsWith(formattedDateString);
                    });

                    if (weatherForDate) {
                        var description = weatherForDate.weather[0].description;
                        var temp = Math.round(weatherForDate.main.temp);
                        $reactions.answer("Погода в городе " + capitalize(city) + " на " + formattedDateString + ": " + description + ", " + temp + "°C.");
                    } else {
                        $reactions.answer("Не могу найти погоду на эту дату.");
                    }
                } else {
                    $reactions.answer("Что-то сервер барахлит. Не могу узнать погоду за указанную дату.");
                }
            }).catch(function (err) {
                $reactions.answer("Что-то сервер барахлит. Не могу узнать погоду за указанную дату.");
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
    
    state: CatchAll || noContext=true
        event!: noMatch
        a: Извините, я вас не понимаю, зато могу рассказать о погоде. Введите название города
        go: /GetWeather
