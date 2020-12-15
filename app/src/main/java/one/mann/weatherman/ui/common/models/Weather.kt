package one.mann.weatherman.ui.common.models

/* Created by Psmann. */

internal data class Weather(
        val city: City = City(),
        val currentWeather: CurrentWeather = CurrentWeather(),
        val dailyForecasts: List<DailyForecast> = listOf(),
        val hourlyForecasts: List<HourlyForecast> = listOf()
)