var OPENWEATHERMAP_API_KEY = $injector.api_key;

function openWeatherMapCurrent(units, lang, q) {
    return $http.get("https://api.openweathermap.org/data/2.5/weather", {
        timeout: 10000,
        params: {
            APPID: OPENWEATHERMAP_API_KEY,
            units: units,
            lang: lang,
            q: q
        }
    });
}

function openWeatherMapCurrent(units, lang, q) {
    return $http.get("http://api.openweathermap.org/data/2.5/forecast?appid=${OPENWEATHERMAP_API_KEY}&units=${units}&lang=${lang}&q=${q}", {
        timeout: 10000,
        query: {
            appid: OPENWEATHERMAP_API_KEY,  // API ключ
            units: units,                   // Единицы измерения (метрические)
            lang: lang,                     // Язык
            q: q                            // Город
        }
    });
}
