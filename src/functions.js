var OPENWEATHERMAP_API_KEY = $injector.api_key;

function openWeatherMapCurrent(units, lang, q) {
    return $http.get("http://api.openweathermap.org/data/2.5/weather?APPID=${OPENWEATHERMAP_API_KEY}&units=${units}&lang=${lang}&q=${q}", {
        timeout: 10000,
        query: {
            APPID: OPENWEATHERMAP_API_KEY,
            units: units,
            lang: lang,
            q: q
        }
    });
}

function openWeatherMapForecast(units, lang, q, date) {
    // Формируем запрос к API
    var url = "http://api.openweathermap.org/data/2.5/forecast?APPID=" + OPENWEATHERMAP_API_KEY + "&units=" + units + "&lang=" + lang + "&q=" + q;
    
    return $http.get(url, {
        timeout: 10000,
        query: {
            APPID: OPENWEATHERMAP_API_KEY,
            units: units,
            lang: lang,
            q: q
        }
    }).then(function(res) {
        // Проверка наличия данных в ответе
        if (res && res.data && res.data.list) {
            $reactions.answer("Получен прогноз: " + JSON.stringify(res.data));  // Выводим данные в ответ
            return res.data;
        } else {
            throw new Error("Нет данных в ответе от API.");
        }
    }).catch(function(err) {
        // Логируем подробную ошибку
        $reactions.answer("Ошибка при запросе прогноза: " + err.message);
        $reactions.answer("Ответ сервера: " + JSON.stringify(err));  // Дополнительная информация об ошибке
        throw new Error("Не могу получить прогноз. Проверьте запрос.");
    });
}

function getWeatherForSpecificDate(units, lang, city, targetDate) {
    console.log("Получаем прогноз для города: " + city + " на дату: " + targetDate); // Для отладки
    return $http.get("http://api.openweathermap.org/data/2.5/forecast?APPID=${APPID}&units=${units}&lang=${lang}&q=${city}", {
        timeout: 10000,
        query: {
            APPID: OPENWEATHERMAP_API_KEY,
            units: units,
            lang: lang,
            q: city
        }
    }).then(function (response) {
        if (response && response.data && response.data.list) {
            var forecastData = response.data.list;

            var targetDateObj = new Date(targetDate); // Преобразуем строку даты в объект Date
            console.log("Ищем прогноз для даты:", targetDateObj);

            var selectedForecast = forecastData.find(function (forecast) {
                var forecastDate = new Date(forecast.dt * 1000); // Преобразуем временную метку из API
                return forecastDate.toDateString() === targetDateObj.toDateString();
            });

            if (selectedForecast) {
                var description = selectedForecast.weather[0].description;
                var temp = Math.round(selectedForecast.main.temp);
                return {
                    city: city,
                    date: targetDate,
                    description: description,
                    temperature: temp + "°C"
                };
            } else {
                throw new Error("Не удалось найти данные для указанной даты.");
            }
        } else {
            throw new Error("Ошибка получения данных о погоде.");
        }
    }).catch(function (err) {
        console.error("Ошибка при запросе прогноза:", err); // Выводим подробную информацию об ошибке
        $reactions.answer("Произошла ошибка при запросе данных о погоде: " + err.message);
        return null;
    });
}