package com.pandemic.infectiontracker

enum class CardLocation {
    IN_DISCARD,
    ON_DECK_TOP
}

data class CardInstance(
    val id: Long,
    val cityName: String,
    val location: CardLocation,
    val epidemicCount: Int = 0
)

data class GameState(
    val instances: List<CardInstance> = emptyList(),
    val totalEpidemics: Int = 0,
    private val nextId: Long = 1
) {
    val inDiscard: List<CardInstance> = instances.filter { it.location == CardLocation.IN_DISCARD }
    val onDeckTop: List<CardInstance> = instances.filter { it.location == CardLocation.ON_DECK_TOP }
    val totalDraws: Int = instances.size

    fun drawCountFor(cityName: String): Int = instances.count { it.cityName == cityName }

    fun recordDraw(cityName: String): GameState {
        if (PandemicCities.all.none { it.name == cityName }) return this

        return copy(
            instances = instances + CardInstance(
                id = nextId,
                cityName = cityName,
                location = CardLocation.IN_DISCARD,
                epidemicCount = 0
            ),
            nextId = nextId + 1
        )
    }

    fun drawFromTop(instanceId: Long): GameState = copy(
        instances = instances.map { instance ->
            if (instance.id == instanceId && instance.location == CardLocation.ON_DECK_TOP) {
                instance.copy(location = CardLocation.IN_DISCARD, epidemicCount = 0)
            } else {
                instance
            }
        }
    )

    fun removeFromDiscard(instanceId: Long): GameState = copy(
        instances = instances.filterNot { instance -> instance.id == instanceId && instance.location == CardLocation.IN_DISCARD }
    )

    fun epidemicEvent(bottomOfDeckCity: String): GameState {
        if (PandemicCities.all.none { it.name == bottomOfDeckCity }) return this

        val newEpidemicCount = totalEpidemics + 1

        val withBottomCard = instances + CardInstance(
            id = nextId,
            cityName = bottomOfDeckCity,
            location = CardLocation.IN_DISCARD,
            epidemicCount = newEpidemicCount
        )

        return copy(
            instances = withBottomCard.map { instance ->
                if (instance.location == CardLocation.IN_DISCARD) {
                    instance.copy(location = CardLocation.ON_DECK_TOP, epidemicCount = newEpidemicCount)
                } else {
                    instance
                }
            },
            totalEpidemics = newEpidemicCount,
            nextId = nextId + 1
        )

    }

    fun reset(): GameState = GameState()
}
