package com.pandemic.infectiontracker

enum class CityColor {
    BLUE, YELLOW, BLACK, RED, UNKNOWN
}

data class City(val name: String, val color: CityColor)

object PandemicCities {
    val all: List<City> = listOf(
        City("Atananarivo", CityColor.BLACK),
        City("Atlanta", CityColor.BLUE),
        City("Baghdad", CityColor.BLACK),
        City("Bangkok", CityColor.RED),
        City("Bogotá", CityColor.YELLOW),
        City("Buenos Aires", CityColor.YELLOW),
        City("Cairo", CityColor.BLACK),
        City("Chicago", CityColor.BLUE),
        City("Dar es Salaam", CityColor.YELLOW),
        City("Denver", CityColor.BLUE),
        City("Frankfurt", CityColor.BLUE),
        City("Ho Chi Minh City", CityColor.RED),
        City("Hong Kong", CityColor.RED),
        City("Istanbul", CityColor.BLACK),
        City("Jacksonville", CityColor.YELLOW),
        City("Jakarta", CityColor.RED),
        City("Johannesburg", CityColor.BLACK),
        City("Khartoum", CityColor.BLACK),
        City("Kinshasa", CityColor.BLACK),
        City("Kolkata", CityColor.BLACK),
        City("Lagos", CityColor.YELLOW),
        City("Lima", CityColor.YELLOW),
        City("London", CityColor.BLUE),
        City("Los Angeles", CityColor.YELLOW),
        City("Manila", CityColor.RED),
        City("Mexico City", CityColor.YELLOW),
        City("Moscow", CityColor.BLACK),
        City("New Mumbai", CityColor.BLACK),
        City("New York", CityColor.BLUE),
        City("Paris", CityColor.BLUE),
        City("San Francisco", CityColor.BLUE),
        City("Santiago", CityColor.YELLOW),
        City("São Paulo", CityColor.YELLOW),
        City("Shanghai", CityColor.RED),
        City("St. Petersburg", CityColor.BLUE),
        City("Tripoli", CityColor.BLACK),
        City("Utopia", CityColor.RED),
        City("Washington", CityColor.BLUE),
        City("Wellington", CityColor.BLUE)

    )

    fun getColorFor(cityName: String): CityColor {
        return all.find { it.name == cityName }?.color ?: CityColor.UNKNOWN
    }
}
