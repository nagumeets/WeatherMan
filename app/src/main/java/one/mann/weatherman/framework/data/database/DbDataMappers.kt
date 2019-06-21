package one.mann.weatherman.framework.data.database

import one.mann.domain.model.Location
import one.mann.domain.model.Weather as DomainWeather
import one.mann.weatherman.framework.data.database.Weather as DbWeather

internal fun LocationTuple.mapToDomain(): Location = Location(arrayOf(coordinatesLat, coordinatesLong))

internal fun DomainWeather.mapToDb(id: Int = 0): DbWeather = DbWeather(
        id, // Auto generated
        cityName,
        currentTemp,
        feelsLike,
        pressure,
        humidity,
        description,
        icon,
        sunrise,
        sunset,
        countryFlag,
        clouds,
        windSpeed,
        windDirection,
        lastUpdated,
        visibility,
        dayLength,
        lastChecked,
        sunPosition,
        minTemp,
        maxTemp,
        day1Date,
        day1MinTemp,
        day1MaxTemp,
        day1Icon,
        day2Date,
        day2MinTemp,
        day2MaxTemp,
        day2Icon,
        day3Date,
        day3MinTemp,
        day3MaxTemp,
        day3Icon,
        day4Date,
        day4MinTemp,
        day4MaxTemp,
        day4Icon,
        day5Date,
        day5MinTemp,
        day5MaxTemp,
        day5Icon,
        day6Date,
        day6MinTemp,
        day6MaxTemp,
        day6Icon,
        day7Date,
        day7MinTemp,
        day7MaxTemp,
        day7Icon,
        coordinatesLat,
        coordinatesLong,
        locationString
)

internal fun DbWeather.mapToDomain(): DomainWeather = DomainWeather(
        cityName,
        currentTemp,
        feelsLike,
        pressure,
        humidity,
        description,
        icon,
        sunrise,
        sunset,
        countryFlag,
        clouds,
        windSpeed,
        windDirection,
        lastUpdated,
        visibility,
        dayLength,
        lastChecked,
        sunPosition,
        minTemp,
        maxTemp,
        day1Date,
        day1MinTemp,
        day1MaxTemp,
        day1Icon,
        day2Date,
        day2MinTemp,
        day2MaxTemp,
        day2Icon,
        day3Date,
        day3MinTemp,
        day3MaxTemp,
        day3Icon,
        day4Date,
        day4MinTemp,
        day4MaxTemp,
        day4Icon,
        day5Date,
        day5MinTemp,
        day5MaxTemp,
        day5Icon,
        day6Date,
        day6MinTemp,
        day6MaxTemp,
        day6Icon,
        day7Date,
        day7MinTemp,
        day7MaxTemp,
        day7Icon,
        coordinatesLat,
        coordinatesLong,
        locationString
)