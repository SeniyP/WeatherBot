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
// Функция для запроса прогноза погоды на 5 дней
function openWeatherMapForecast(units, lang, q) {
    return $http.get("http://api.openweathermap.org/data/2.5/forecast?APPID=${APPID}&units=${units}&lang=${lang}&q=${q}", {
        timeout: 10000,
        query: {
            APPID: OPENWEATHERMAP_API_KEY,
            units: units,
            lang: lang,
            q: q
        }
    });
}

// Новая функция для запроса погоды, активности и одежды
function getWeatherActivityAndClothing(city) {
    return $http.get("https://d916f0e2-0f17-47b5-bf66-142c6f79d239-00-g9jewjkrlxpn.janeway.replit.dev/weather?city=" + city, {
        timeout: 10000
    }).then(function(response) {
        return response.data;  // предполагаем, что ответ приходит в формате JSON
    }).catch(function(error) {
        console.error("Ошибка при запросе к серверу погоды: ", error);
        throw new Error("Не удалось получить данные о погоде.");
    });
}