require: functions.js

theme: /

    state: Start
        q!: $regex</start>
        a: Привет! Я электронный помощник. Я могу сообщить вам текущую погоду в любом городе. Напишите город.

    state: GetWeather
        intent!: /geo
        script:
            var city = $caila.inflect($parseTree._geo, ["nomn"]);
            openWeatherMapForecast("metric", "ru", city).then(function (res) {
                if (res && res.list) {
                    var today = new Date();
                    var weatherData = res.list.filter(function (item) {
                        var weatherDate = new Date(item.dt * 1000);  // Convert timestamp to Date
                        return weatherDate.toDateString() === today.toDateString();
                    });
                    if (weatherData.length > 0) {
                        var description = weatherData[0].weather[0].description;
                        var temperature = Math.round(weatherData[0].main.temp);
                        $reactions.answer("Сегодня в городе " + capitalize(city) + " " + description + ", " + temperature + "°C.");
                        if (weatherData[0].weather[0].main == 'Rain' || weatherData[0].weather[0].main == 'Drizzle') {
                            $reactions.answer("Советую захватить с собой зонтик!");
                        } else if (temperature < 0) {
                            $reactions.answer("Бррррр, ну и мороз!");
                        }
                    } else {
                        $reactions.answer("Что-то сервер барахлит. Не могу узнать погоду.");
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
            var date = $caila.extract($parseTree._date, ["value"]);
            var formattedDate = formatDateForAPI(date);
            
            openWeatherMapForecast("metric", "ru", city).then(function (res) {
                if (res && res.list) {
                    var weatherDataForDate = res.list.filter(function (item) {
                        var weatherDate = new Date(item.dt * 1000);  // Convert timestamp to Date
                        return weatherDate.toDateString() === formattedDate.toDateString();
                    });
                    if (weatherDataForDate.length > 0) {
                        var description = weatherDataForDate[0].weather[0].description;
                        var temperature = Math.round(weatherDataForDate[0].main.temp);
                        $reactions.answer("На выбранную дату в городе " + capitalize(city) + " " + description + ", " + temperature + "°C.");
                    } else {
                        $reactions.answer("Не могу найти данные на эту дату.");
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
    
    state: CatchAll || noContext=true
        event!: noMatch
        a: Извините, я вас не понимаю, зато могу рассказать о погоде. Введите название города
        go: /GetWeather

