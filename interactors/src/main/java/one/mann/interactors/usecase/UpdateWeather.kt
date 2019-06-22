package one.mann.interactors.usecase

import one.mann.domain.model.LocationType
import one.mann.interactors.data.repository.WeatherRepository

class UpdateWeather(private val weatherRepository: WeatherRepository) {

    suspend fun invoke(locationType: LocationType) = weatherRepository.updateAll(locationType)
}