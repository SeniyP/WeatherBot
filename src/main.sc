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
             // Задайте API ключ
            var apiKey = "de907e53b9a4691b221ea39abe59380c";  // Ваш API-ключ
            var city = "Москва";  // Город для запроса
            
            // Формируем URL для запроса
            var url = `http://api.openweathermap.org/data/2.5/forecast?q=${city}&appid=${apiKey}&units=metric&lang=ru`;
            
            // Функция для отправки запроса
            function getWeatherForecast() {
                // Отправка GET-запроса
                $http.get(url).then(function(response) {
                    // Проверяем, есть ли данные в ответе
                    if (response.data && response.data.list && response.data.list.length > 0) {
                        var forecastData = response.data.list;
                        var weatherInfo = forecastData[0];  // Берем данные о первой погоде (по времени)
                        
                        // Извлекаем информацию из ответа
                        var date = weatherInfo.dt_txt;
                        var temperature = weatherInfo.main.temp;
                        var description = weatherInfo.weather[0].description;
                        
                        // Выводим информацию о погоде
                        console.log(`Погода на ${date}: ${temperature}°C, ${description}`);
            
                        // Отправляем информацию пользователю
                        $reactions.answer(`Погода в ${city} на ${date}: ${temperature}°C, ${description}`);
                    } else {
                        // Обработка ошибки, если данные не получены
                        console.error("Не удалось получить данные о погоде.");
                        $reactions.answer("Не удалось получить прогноз погоды. Попробуйте позже.");
                    }
                }).catch(function(error) {
                    // Обработка ошибок при запросе
                    console.error("Ошибка при запросе к OpenWeatherMap:", error);
                    $reactions.answer("Ошибка при запросе к OpenWeatherMap. Ошибка: " + error.message);
                });
            }
            
            // Вызов функции для получения прогноза
            getWeatherForecast();






    state: CatchAll || noContext=true
        event!: noMatch
        a: Извините, я вас не понимаю, зато могу рассказать о погоде. Введите название города
        go: /GetWeather
