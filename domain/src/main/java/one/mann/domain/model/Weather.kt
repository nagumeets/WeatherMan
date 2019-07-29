package one.mann.domain.model

data class Weather(
        // Current Weather
        val id: Int? = null,
        val cityName: String = "",
        val currentTemp: String = "",
        val feelsLike: String = "",
        val pressure: String = "",
        val humidity: String = "",
        val description: String = "",
        val iconId: Int = 0,
        val sunrise: String = "",
        val sunset: String = "",
        val countryFlag: String = "",
        val clouds: String = "",
        val windSpeed: String = "",
        val windDirection: String = "",
        val lastUpdated: String = "",
        val visibility: String = "",
        val dayLength: String = "",
        val lastChecked: String = "",
        val sunPosition: Float = 0f,
        val minTemp: String = "",
        val maxTemp: String = "",
        // Daily Forecast
        val day1Date: String = "",
        val day1MinTemp: String = "",
        val day1MaxTemp: String = "",
        val day1IconId: Int = 0,
        val day2Date: String = "",
        val day2MinTemp: String = "",
        val day2MaxTemp: String = "",
        val day2IconId: Int = 0,
        val day3Date: String = "",
        val day3MinTemp: String = "",
        val day3MaxTemp: String = "",
        val day3IconId: Int = 0,
        val day4Date: String = "",
        val day4MinTemp: String = "",
        val day4MaxTemp: String = "",
        val day4IconId: Int = 0,
        val day5Date: String = "",
        val day5MinTemp: String = "",
        val day5MaxTemp: String = "",
        val day5IconId: Int = 0,
        val day6Date: String = "",
        val day6MinTemp: String = "",
        val day6MaxTemp: String = "",
        val day6IconId: Int = 0,
        val day7Date: String = "",
        val day7MinTemp: String = "",
        val day7MaxTemp: String = "",
        val day7IconId: Int = 0,
        // Location
        val coordinatesLat: Float = 0f,
        val coordinatesLong: Float = 0f,
        val locationString: String = ""
)