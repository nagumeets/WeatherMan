package one.mann.weatherman.api.teleport

import com.google.gson.annotations.SerializedName

internal class DtoTimezone(@SerializedName("_embedded") val embedded1: Embedded1) {

    data class CityTimezone(
            @SerializedName("iana_name")
            val ianaName: String
    )

    data class Embedded1(
            @SerializedName("location:nearest-cities")
            val locationNearestCities: List<LocationNearestCities>
    )

    data class Embedded2(
            @SerializedName("location:nearest-city")
            val locationNearestCity: LocationNearestCity
    )

    data class Embedded3(
            @SerializedName("city:timezone")
            val cityTimezone: CityTimezone
    )

    data class LocationNearestCities(
            @SerializedName("_embedded")
            val embedded2: Embedded2
    )

    data class LocationNearestCity(
            @SerializedName("_embedded")
            val embedded3: Embedded3
    )
}