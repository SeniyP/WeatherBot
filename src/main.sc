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
            
            // Выводим дату в формате "YYYY-MM-DD"
            var formattedDate = date.year + '-' + date.month + '-' + date.day;
            
            // Запрос погоды на несколько дней (погода доступна не на все даты)
            openWeatherMapForecast("metric", "ru", city).then(function (res) {
                if (res && res.list) {
                    // Если дата слишком далека, то сообщаем об этом
                    var today = new Date();
                    var requestDate = new Date(date.year, date.month - 1, date.day);
                    if (requestDate > today.setDate(today.getDate() + 7)) {
                        $reactions.answer("Прогноз погоды на эту дату недоступен, так как мы можем показывать только прогноз на ближайшие 7 дней.");
                    } else {
                        // Найдем ближайшую дату в прогнозе
                        var closestForecast = res.list.find(function(item) {
                            return item.dt_txt.startsWith(formattedDate);
                        });

                        if (closestForecast) {
                            $reactions.answer("Прогноз на " + formattedDate + " в городе " + capitalize(city) + ": " +
                                closestForecast.weather[0].description + ", " + Math.round(closestForecast.main.temp) + "°C");
                        } else {
                            $reactions.answer("На эту дату прогноз недоступен, но вот ближайший прогноз.");
                            var nearestForecast = res.list[0]; // ближайший доступный прогноз
                            $reactions.answer("Прогноз на ближайший день: " + nearestForecast.weather[0].description + ", " + Math.round(nearestForecast.main.temp) + "°C");
                        }
                    }
                } else {
                    $reactions.answer("Что-то сервер барахлит. Не могу узнать прогноз на эту дату.");
                }
            }).catch(function (err) {
                $reactions.answer("Что-то сервер барахлит. Не могу узнать прогноз на эту дату.");
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
