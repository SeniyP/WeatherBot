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
