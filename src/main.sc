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

    state: GeoDate
        intent!: /geo-date
        script:
            var city = $caila.inflect($parseTree._geo, ["nomn"]);
            var dateInput = $parseTree._date;
    
            // Проверка, что дата распознана через Duckling и преобразование в правильный формат
            var formattedDate;
            if (dateInput) {
                formattedDate = dateInput.value.split('T')[0];  // Извлекаем только дату из строки 'YYYY-MM-DD'
                console.log("Получена дата: " + formattedDate); // Логирование даты
            } else {
                $reactions.answer("Не удалось определить дату.");
                return;
            }
    
            // Запрос на 5-дневный прогноз
            openWeatherMapForecast("metric", "ru", city).then(function (res) {
                console.log("Ответ от OpenWeatherMap: ", res);  // Логирование ответа API
                if (res && res.list) {
                    var forecast = null;
                    for (var i = 0; i < res.list.length; i++) {
                        // Ищем ближайший прогноз для выбранной даты
                        if (res.list[i].dt_txt.startsWith(formattedDate)) {
                            forecast = res.list[i];
                            break;
                        }
                    }
                    if (forecast) {
                        var temperature = Math.round(forecast.main.temp);
                        var description = forecast.weather[0].description;
    
                        $reactions.answer("Прогноз погоды в городе " + capitalize(city) + " на " + formattedDate + ": " +
                            "температура " + temperature + "°C, " + description + ".");
                    } else {
                        $reactions.answer("Данных о погоде в городе " + capitalize(city) + " на дату " + formattedDate + " нет.");
                    }
                } else {
                    $reactions.answer("Что-то сервер барахлит. Не могу узнать прогноз погоды.");
                }
            }).catch(function (err) {
                console.error("Ошибка при запросе к OpenWeatherMap: ", err);  // Логирование ошибки
                $reactions.answer("Что-то сервер барахлит. Не могу узнать прогноз погоды.");
            });




    state: CatchAll || noContext=true
        event!: noMatch
        a: Извините, я вас не понимаю, зато могу рассказать о погоде. Введите название города
        go: /GetWeather
