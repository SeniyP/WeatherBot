require: js/axios.js
    type = scriptEs6
    name = axios

theme: /

    state: Start
        q!: $regexp</start>
        a: Привет! Я могу рассказать вам погоду в любом городе. Просто напишите, например: "Какая погода в Москве?"

    state: Weather
        q!: /Какая погода в (.+)/
        scriptEs6:
            const city = $parseTree.text.match(/Какая погода в (.+)/)[1].trim();
            $session.city = city;
            $http.get(`http://api.weatherapi.com/v1/current.json?key=50aa229c887e47dd8c631208240411&q=${city}`)
                .then(response => {
                    const data = response.data;
                    const location = data.location.name;
                    const temp = data.current.temp_c;
                    const condition = data.current.condition.text;
                    $reactions.answer(`Сейчас в ${location}: ${temp}°C, ${condition}.`);
                })
                .catch(error => {
                    $reactions.answer("Не удалось получить данные о погоде. Проверьте название города или попробуйте позже.");
                });
            go!: /AnythingElse

    state: AnythingElse
        a: Хотите узнать погоду в другом городе?
        buttons:
            "Да"
            "Нет"

    state: NoMatch
        event!: noMatch
        a: Я не понял. Могу рассказать вам погоду, например, в "Москва". Напишите "Какая погода в [город]?"
