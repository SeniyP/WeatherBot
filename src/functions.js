var OPENWEATHERMAP_API_KEY = $injector.api_key;

function openWeatherMapForecast(units, lang, q) {
    return $http.get("http://api.openweathermap.org/data/2.5/forecast", {
        timeout: 10000,
        query: {
            APPID: OPENWEATHERMAP_API_KEY,
            units: units,
            lang: lang,
            q: q
        }
    });
}

function formatDateForAPI(date) {
    // Преобразуем дату в объект Date
    var parsedDate = new Date(date);
    return parsedDate;
}
