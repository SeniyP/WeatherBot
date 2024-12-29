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
            var date = $parseTree._date;

            openWeatherMapForecast("metric", "ru", city).then(function (res) {
                if (res && res.list) {
                    // Преобразуем дату в формат YYYY-MM-DD
                    function formatDate(dateString) {
                        var date = new Date(dateString);
                        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
                    }
                    var formattedDate = formatDate(date);

                    // Ищем прогноз для указанной даты
                    var forecast = res.list.find(item => item.dt_txt.startsWith(formattedDate));

                    if (forecast) {
                        var temp = Math.round(forecast.main.temp);
                        var description = forecast.weather[0].description;
                        var humidity = forecast.main.humidity;
                        var windSpeed = forecast.wind.speed;

                        var weatherInfo = "Прогноз погоды в городе " + capitalize(city) + " на " + formattedDate + ":\n" +
                            "Температура: " + temp + "°C\n" +
                            "Описание: " + description + "\n" +
                            "Влажность: " + humidity + "%\n" +
                            "Скорость ветра: " + windSpeed + " м/с";

                        $reactions.answer(weatherInfo);
                    } else {
                        $reactions.answer("Извините, данных о погоде на выбранную дату нет.");
                    }
                } else {
                    $reactions.answer("Что-то сервер барахлит. Не могу узнать прогноз погоды.");
                }
            }).catch(function (err) {
                $reactions.answer("Что-то сервер барахлит. Не могу узнать прогноз погоды.");
            });

    state: CatchAll || noContext=true
        event!: noMatch
        a: Извините, я вас не понимаю, зато могу рассказать о погоде. Введите название города
        go: /GetWeather
