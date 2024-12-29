require: functions.js
# Основной поток
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

    state: geo-date
        intent!: /geo-date
        script:
            var city = $caila.inflect($parseTree._geo, ["nomn"]);
            var dateString = $caila.inflect($parseTree._date, ["nomn"]);

            // Если дата в формате YYYY-MM-DD (например, 2024-12-29), преобразуем её в формат "на 29 декабря"
            var regexDate = /(\d{4})-(\d{2})-(\d{2})/;
            var match = dateString.match(regexDate);

            if (match) {
                var year = match[1];
                var month = match[2];
                var day = match[3];
                
                // Преобразуем в формат "на 29 декабря"
                var formattedDate = "на " + day + " " + getMonthName(month) + " " + year;
                dateString = formattedDate;
            }

            // Теперь передаем в Duckling сущность для обработки
            var parsedDate = $caila.inflect(dateString, ["date"]);

            if (parsedDate) {
                var requestedDate = parsedDate.value;

                // Используем прогноз погоды на 5 дней
                openWeatherMapForecast("metric", "ru", city).then(function (res) {
                    if (res && res.list) {
                        var weatherOnDate = res.list.filter(function (forecast) {
                            var forecastDate = new Date(forecast.dt * 1000).toISOString().split('T')[0];
                            return forecastDate === requestedDate;
                        });

                        if (weatherOnDate.length > 0) {
                            var forecastMessage = "Погода в городе " + capitalize(city) + " на " + requestedDate + ":\n";
                            weatherOnDate.forEach(function (forecast) {
                                var time = new Date(forecast.dt * 1000).toLocaleTimeString();
                                var temp = Math.round(forecast.main.temp);
                                var description = forecast.weather[0].description;
                                forecastMessage += `Время: ${time}, Температура: ${temp}°C, Описание: ${description}\n`;
                            });
                            $reactions.answer(forecastMessage);
                        } else {
                            $reactions.answer("Извините, нет прогноза погоды на эту дату.");
                        }
                    } else {
                        $reactions.answer("Что-то сервер барахлит. Не могу узнать погоду на указанную дату.");
                    }
                }).catch(function (err) {
                    $reactions.answer("Что-то сервер барахлит. Не могу узнать погоду на указанную дату.");
                });
            } else {
                $reactions.answer("Не удалось распознать дату. Пожалуйста, укажите корректную дату.");
            }

# Вспомогательная функция для получения названия месяца по числовому значению
function getMonthName(monthNumber) {
    const months = ["января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"];
    return months[parseInt(monthNumber) - 1];
}
