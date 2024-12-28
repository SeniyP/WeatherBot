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

function getWeatherForSpecificDate(units, lang, city, targetDate) {
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
            // Получаем все данные с прогнозом
            var forecastData = response.data.list;

            // Преобразуем targetDate в объект Date
            var targetDateObj = new Date(targetDate);

            // Ищем ближайший прогноз, который соответствует дате
            var selectedForecast = forecastData.find(function (forecast) {
                var forecastDate = new Date(forecast.dt * 1000); // Время из API в секундах, преобразуем в миллисекунды
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
        $reactions.answer("Произошла ошибка при запросе данных о погоде: " + err.message);
        return null;
    });
}