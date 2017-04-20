;(function(window, document, $, undefined) {
    OnDemand.Weather = {
        attach: function(context) {
            if ($(context).hasClass('weather--forecast')) {
                OnDemand.Weather.initialize(context);
            } else {
                $.each($('.weather--forecast', context), function(i, _component) {
                   OnDemand.Weather.initialize(_component);
                });
            }
        },
        initialize: function(context) {
            console.log('Initializing Weather Forecast Component');
            $.each($('.weather-data',context), function(i, _component) {
                var source = $("#weather--forecast-template", _component).html();
                var template = Handlebars.compile(source);
                $.getJSON("/bin/apis/weather.json", function(data) {
                    console.log('Rendering Weather Forecast Component');
                    _component.innerHTML = template(data);
                });
            });
        }
    };

}(window, document, $));
