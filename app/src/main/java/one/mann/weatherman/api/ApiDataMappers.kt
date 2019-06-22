package one.mann.weatherman.api

import one.mann.domain.model.CurrentWeather
import one.mann.domain.model.DailyForecast
import one.mann.weatherman.api.openweathermap.DtoCurrentWeather
import one.mann.weatherman.api.openweathermap.DtoDailyForecast
import one.mann.weatherman.api.teleport.DtoTimezone

internal const val ICON_URL = "http://openweathermap.org/img/w/"
internal const val ICON_EXTENSION = ".png"

internal fun DtoCurrentWeather.mapToDomain(): CurrentWeather = CurrentWeather(
        name,
        main.temp,
        main.pressure,
        main.humidity,
        weather[0].main,
        ICON_URL + weather[0].icon + ICON_EXTENSION,
        sys.sunrise * 1000,
        sys.sunset * 1000,
        sys.country,
        clouds.all,
        wind.speed,
        wind.deg,
        dt * 1000,
        visibility
)

internal fun DtoDailyForecast.List_.mapToDomain(): DailyForecast = DailyForecast(
        dt * 1000,
        temp.min,
        temp.max,
        ICON_URL + weather[0].icon + ICON_EXTENSION
)

internal fun DtoTimezone.mapToString(): String =
        embedded.locationNearestCities[0].embedded.locationNearestCity.embedded.cityTimezone.ianaName