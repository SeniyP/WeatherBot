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

// Добавление функции для получения прогноза погоды
function openWeatherMapForecast(units, lang, q, date) {
    return $http.get("http://api.openweathermap.org/data/2.5/forecast?APPID=${OPENWEATHERMAP_API_KEY}&units=${units}&lang=${lang}&q=${q}", {
        timeout: 10000,
        query: {
            APPID: OPENWEATHERMAP_API_KEY,
            units: units,
            lang: lang,
            q: q
        }
    }).then(function(res) {
        if (res && res.data && res.data.list) {
            $reactions.answer("Получен прогноз: " + JSON.stringify(res.data));  // Используем реакцию для вывода данных
            return res.data;
        } else {
            throw new Error("Нет данных в ответе от API.");
        }
    }).catch(function(err) {
        $reactions.answer("Ошибка при запросе прогноза: " + err.message);  // Выводим ошибку через $reactions
        throw new Error("Не могу получить прогноз. Проверьте запрос.");
    });
}
