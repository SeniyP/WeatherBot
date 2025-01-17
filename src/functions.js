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

function getWeatherRecommendation(city, type, reactions) {
    var url = "https://d916f0e2-0f17-47b5-bf66-142c6f79d239-00-g9jewjkrlxpn.janeway.replit.dev/weather?city=" + city;

    $http.get(url)
        .then(function(response) {
            // Логируем весь ответ как текст
            var responseText = JSON.stringify(response);

            // Извлекаем данные о погоде, активности и одежде
            var weatherPattern = /"weather":"([^"]+)"/;
            var activityPattern = /"recommended_activity":"([^"]+)"/;
            var clothingPattern = /"recommended_clothing":"([^"]+)"/;

            var weatherMatch = responseText.match(weatherPattern);
            var activityMatch = responseText.match(activityPattern);
            var clothingMatch = responseText.match(clothingPattern);

            // Всегда выводим информацию о погоде
            var weatherInfo = weatherMatch ? weatherMatch[1] : "Неизвестно";
            reactions.answer("Погода в городе " + city + ": " + weatherInfo);

            // Выводим информацию в зависимости от типа
            if (type === "activity") {
                var activity = activityMatch ? activityMatch[1] : "Неизвестно";
                reactions.answer("Рекомендуемая активность: " + activity);
            } else if (type === "clothing") {
                var clothing = clothingMatch ? clothingMatch[1] : "Неизвестно";
                reactions.answer("Рекомендуемая одежда: " + clothing);
            } else {
                reactions.answer("Неизвестный запрос. Пожалуйста, выберите 'activity' или 'clothing'.");
            }
        })
        .catch(function(err) {
            // Логируем ошибку запроса
            reactions.answer("Ошибка запроса к серверу: " + err.message);
        });
}
