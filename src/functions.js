var OPENWEATHERMAP_API_KEY = $injector.api_key;

function openWeatherMapCurrent(units, lang, q){
return $http.get("http://api.openweathermap.org/data/2.5/weather?APPID=${APPID}&units=${units}&lang=${lang}&q=${q}", {
        timeout: 10000,
        query:{
            APPID: OPENWEATHERMAP_API_KEY,
            units: units,
            lang: lang,
            q: q
        }
    });
}
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
            console.log("Получен прогноз:", res.data);
            return res.data;
        } else {
            throw new Error("Нет данных в ответе от API.");
        }
    }).catch(function(err) {
        console.error("Ошибка при запросе прогноза:", err);
        throw new Error("Не могу получить прогноз. Проверьте запрос.");
    });
}
