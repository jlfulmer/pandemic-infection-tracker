package com.pandemic.infectiontracker

enum class CityColor {
    BLUE, YELLOW, BLACK, RED, UNKNOWN
}

data class City(val name: String, val color: CityColor)

object PandemicCities {
    val all: List<City> = listOf(
        City("Algiers", CityColor.BLACK),
        City("Atlanta", CityColor.BLUE),
        City("Baghdad", CityColor.BLACK),
        City("Bangkok", CityColor.RED),
        City("Beijing", CityColor.RED),
        City("Bogotá", CityColor.YELLOW),
        City("Buenos Aires", CityColor.YELLOW),
        City("Cairo", CityColor.BLACK),
        City("Casablanca", CityColor.BLACK),
        City("Chennai", CityColor.BLACK),
        City("Chicago", CityColor.BLUE),
        City("Dallas", CityColor.BLUE),
        City("Delhi", CityColor.BLACK),
        City("Denver", CityColor.BLUE),
        City("Essen", CityColor.BLUE),
        City("Ho Chi Minh City", CityColor.RED),
        City("Hong Kong", CityColor.RED),
        City("Istanbul", CityColor.BLACK),
        City("Jakarta", CityColor.RED),
        City("Johannesburg", CityColor.BLACK),
        City("Karachi", CityColor.BLACK),
        City("Khartoum", CityColor.BLACK),
        City("Kinshasa", CityColor.BLACK),
        City("Kolkata", CityColor.BLACK),
        City("Lagos", CityColor.YELLOW),
        City("Lima", CityColor.YELLOW),
        City("London", CityColor.BLUE),
        City("Los Angeles", CityColor.YELLOW),
        City("Madrid", CityColor.BLUE),
        City("Manila", CityColor.RED),
        City("Mexico City", CityColor.YELLOW),
        City("Miami", CityColor.YELLOW),
        City("Milan", CityColor.BLUE),
        City("Montreal", CityColor.BLUE),
        City("Moscow", CityColor.BLACK),
        City("New York", CityColor.BLUE),
        City("Paris", CityColor.BLUE),
        City("Riyadh", CityColor.BLACK),
        City("San Francisco", CityColor.BLUE),
        City("Santiago", CityColor.YELLOW),
        City("São Paulo", CityColor.YELLOW),
        City("Seoul", CityColor.RED),
        City("Shanghai", CityColor.RED),
        City("St. Petersburg", CityColor.BLUE),
        City("Sydney", CityColor.RED),
        City("Taipei", CityColor.RED),
        City("Tokyo", CityColor.RED),
        City("Washington", CityColor.BLUE)
    )

    fun getColorFor(cityName: String): CityColor {
        return all.find { it.name == cityName }?.color ?: CityColor.UNKNOWN
    }
}
