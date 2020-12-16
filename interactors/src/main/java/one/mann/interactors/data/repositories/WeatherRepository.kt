package one.mann.interactors.data.repositories

import one.mann.domain.models.NotificationData
import one.mann.domain.models.location.Location
import one.mann.domain.models.location.LocationType
import one.mann.domain.models.weather.City
import one.mann.domain.models.weather.Weather
import one.mann.interactors.data.mapToDomainWeather
import one.mann.interactors.data.sources.api.TimezoneDataSource
import one.mann.interactors.data.sources.api.WeatherDataSource
import one.mann.interactors.data.sources.framework.DatabaseDataSource
import one.mann.interactors.data.sources.framework.DeviceLocationSource
import one.mann.interactors.data.sources.framework.PreferencesDataSource
import java.util.*
import javax.inject.Inject

/* Created by Psmann. */

class WeatherRepository @Inject constructor(
        private val weatherData: WeatherDataSource,
        private val timezoneData: TimezoneDataSource,
        private val deviceLocation: DeviceLocationSource,
        private val dbData: DatabaseDataSource,
        private val prefsData: PreferencesDataSource
) {

    companion object {
        private const val TIME_OUT = 1000000 // ~ 16.6 minutes (= 16.667 * 60 * 1000)
    }

    /** Insert new city in the database */
    suspend fun createCity(apiLocation: Location? = null) {
        val location = apiLocation ?: deviceLocation.getLocation() // Use device GPS location if null
        val timeCreated = System.currentTimeMillis()
        val currentWeather = weatherData.getCurrentWeather(location).copy(units = prefsData.getUnits())
        dbData.insertWeather(
                mapToDomainWeather(
                        City(
                                generateCityId(),
                                location.coordinates[0],
                                location.coordinates[1],
                                timezoneData.getTimezone(location),
                                timeCreated
                        ),
                        currentWeather,
                        weatherData.getDailyForecast(location),
                        weatherData.getHourlyForecast(location)
                )
        )
    }

    /** Returns a list of all Weather data from the database */
    suspend fun readAllWeather(): List<Weather> = dbData.getAllWeather()

    /** Update all rows in the database with new data */
    suspend fun updateAllWeather(weatherData: List<Weather>) = dbData.updateAllWeather(weatherData)

    /** Remove a row from the database */
    suspend fun deleteCity(cityId: String) = dbData.deleteCity(cityId)

    /** Returns notification data */
    suspend fun readNotificationData(): NotificationData = dbData.getNotificationData()

    /** Update weather from server if syncFromServer() is true, otherwise only update lastChecked value */
    suspend fun updateAllData(locationType: LocationType): Boolean {
        val gpsLocation = if (locationType == LocationType.DEVICE) deviceLocation.getLocation() else null

        gpsLocation?.let { dbData.updateUserCity(it) }

        return if (syncFromServer()) {
            val weathersFromDb = readAllWeather()
            val weathersForUpdate = mutableListOf<Weather>()
            weathersFromDb.forEachIndexed { i, dbWeather ->
                val cityForUpdate = if (i == 0 && gpsLocation != null) {
                    dbWeather.city.copy(
                            coordinatesLat = gpsLocation.coordinates[0],
                            coordinatesLong = gpsLocation.coordinates[1]
                    )
                } else dbWeather.city
                val location = Location(coordinates = listOf(cityForUpdate.coordinatesLat, cityForUpdate.coordinatesLong))
                val currentWeatherForUpdate = weatherData.getCurrentWeather(location).copy(
                        weatherId = dbWeather.currentWeather.weatherId,
                        units = prefsData.getUnits()
                )
                val dailyForecastsForUpdate = weatherData.getDailyForecast(location).toMutableList()
                val hourlyForecastsForUpdate = weatherData.getHourlyForecast(location).toMutableList()

                dbWeather.dailyForecasts.mapIndexed { index, dbDailyForecast ->
                    dailyForecastsForUpdate[index].copy(dailyId = dbDailyForecast.dailyId)
                }
                dbWeather.hourlyForecasts.mapIndexed { index, dbHourlyForecast ->
                    hourlyForecastsForUpdate[index].copy(hourlyId = dbHourlyForecast.hourlyId)
                }
                weathersForUpdate.add(
                        mapToDomainWeather(
                                cityForUpdate,
                                currentWeatherForUpdate,
                                dailyForecastsForUpdate,
                                hourlyForecastsForUpdate
                        )
                )
            }
            updateAllWeather(weathersForUpdate)
            true
        } else {
            dbData.updateLastChecked(System.currentTimeMillis())
            false
        }
    }

    /** Creates a new unique code for cityId */
    private fun generateCityId(): String = UUID.randomUUID().toString()

    /** Returns true if it has been longer than time-out period since last update */
    private suspend fun syncFromServer(): Boolean = System.currentTimeMillis() - prefsData.getLastChecked() > TIME_OUT
}