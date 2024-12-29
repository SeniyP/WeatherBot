var OPENWEATHERMAP_API_KEY = $injector.api_key;

// Форматирование даты
function formatDate(date) {
    var year = date.getFullYear();
    var month = date.getMonth() + 1; // Январь = 0
    var day = date.getDate();

    // Форматирование без `padStart`
    month = month < 10 ? '0' + month : month;
    day = day < 10 ? '0' + day : day;

    return year + '-' + month + '-' + day;
}

// Получение текущей погоды
function openWeatherMapCurrent(units, lang, q) {
    return $http.get("http://api.openweathermap.org/data/2.5/weather", {
        timeout: 10000,
        query: {
            APPID: OPENWEATHERMAP_API_KEY,
            units: units,
            lang: lang,
            q: q
        }
    });
}

// Прогноз погоды
function openWeatherMapForecast(units, lang, q, date) {
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

// Приведение к капитализации
function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}
