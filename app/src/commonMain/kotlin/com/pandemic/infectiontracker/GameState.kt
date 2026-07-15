package com.pandemic.infectiontracker

enum class CardLocation {
    IN_DISCARD,
    ON_DECK_TOP
}

data class CardInstance(
    val id: Long,
    val cityName: String,
    val location: CardLocation
)

data class GameState(
    val instances: List<CardInstance> = emptyList(),
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
                location = CardLocation.IN_DISCARD
            ),
            nextId = nextId + 1
        )
    }

    fun drawFromTop(instanceId: Long): GameState = copy(
        instances = instances.map { instance ->
            if (instance.id == instanceId && instance.location == CardLocation.ON_DECK_TOP) {
                instance.copy(location = CardLocation.IN_DISCARD)
            } else {
                instance
            }
        }
    )

    fun reshuffleEvent(bottomOfDeckCity: String): GameState {
        if (PandemicCities.all.none { it.name == bottomOfDeckCity }) return this

        val withBottomCard = instances + CardInstance(
            id = nextId,
            cityName = bottomOfDeckCity,
            location = CardLocation.IN_DISCARD
        )

        return copy(
            instances = withBottomCard.map { instance ->
                if (instance.location == CardLocation.IN_DISCARD) {
                    instance.copy(location = CardLocation.ON_DECK_TOP)
                } else {
                    instance
                }
            },
            nextId = nextId + 1
        )
    }

    fun reset(): GameState = GameState()
}
