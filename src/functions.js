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

function openWeatherMapForecast(units, lang, city, date) {
    return $http.get("http://api.openweathermap.org/data/2.5/forecast?APPID=${APPID}&units=${units}&lang=${lang}&q=${city}", {
        timeout: 10000,
        query: {
            APPID: OPENWEATHERMAP_API_KEY,
            units: units,
            lang: lang,
            q: city
        }
    });
}

# Функции
function formatDate(year, month, day) {
    return year + '-' + month.padStart(2, '0') + '-' + day.padStart(2, '0');
}
