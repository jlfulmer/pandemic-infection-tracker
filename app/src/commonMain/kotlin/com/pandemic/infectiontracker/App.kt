package com.pandemic.infectiontracker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.pandemic.infectiontracker.ui.theme.PandemicInfectionTrackerTheme

private enum class TrackerTab(val label: String) {
    RECORD_DRAW("Record Draw"),
    DISCARD("Discard Pile"),
    DECK_TOP("On Deck Top")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    PandemicInfectionTrackerTheme {
        var gameState by remember { mutableStateOf(GameState()) }
        var selectedTab by remember { mutableIntStateOf(0) }
        var showReshuffleDialog by remember { mutableStateOf(false) }
        var showResetDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Pandemic Legacy Tracker") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    actions = {
                        TextButton(onClick = { showResetDialog = true }) {
                            Text("Reset")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                SummaryBar(gameState)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    FilledTonalButton(
                        onClick = { showReshuffleDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reshuffle Event")
                    }
                }

                TabRow(selectedTabIndex = selectedTab) {
                    TrackerTab.entries.forEachIndexed { index, tab ->
                        val count = when (tab) {
                            TrackerTab.RECORD_DRAW -> gameState.totalDraws
                            TrackerTab.DISCARD -> gameState.inDiscard.size
                            TrackerTab.DECK_TOP -> gameState.onDeckTop.size
                        }
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text("${tab.label} ($count)") }
                        )
                    }
                }

                when (TrackerTab.entries[selectedTab]) {
                    TrackerTab.RECORD_DRAW -> RecordDrawList(
                        gameState = gameState,
                        onCitySelected = { cityName ->
                            gameState = gameState.recordDraw(cityName)
                        }
                    )
                    TrackerTab.DISCARD -> InstanceList(
                        instances = gameState.inDiscard,
                        tab = TrackerTab.DISCARD,
                        onInstanceClick = {}
                    )
                    TrackerTab.DECK_TOP -> InstanceList(
                        instances = gameState.onDeckTop,
                        tab = TrackerTab.DECK_TOP,
                        onInstanceClick = { instanceId ->
                            gameState = gameState.drawFromTop(instanceId)
                        }
                    )
                }
            }
        }

        if (showReshuffleDialog) {
            ReshuffleDialog(
                discardCount = gameState.inDiscard.size,
                onDismiss = { showReshuffleDialog = false },
                onConfirm = { bottomCity ->
                    gameState = gameState.reshuffleEvent(bottomCity)
                    showReshuffleDialog = false
                }
            )
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Reset game?") },
                text = { Text("All recorded draws, discard pile, and deck top tracking will be cleared.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            gameState = gameState.reset()
                            showResetDialog = false
                        }
                    ) {
                        Text("Reset")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SummaryBar(gameState: GameState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem("Total Drawn", gameState.totalDraws)
            SummaryItem("Discard", gameState.inDiscard.size)
            SummaryItem("Deck Top", gameState.onDeckTop.size)
        }
    }
}

@Composable
fun SummaryItem(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordDrawList(
    gameState: GameState,
    onCitySelected: (String) -> Unit
) {
    val groupedCities = remember {
        PandemicCities.all.groupBy { it.color }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Tap a city when it is drawn from the infection deck. " +
                    "The same city can be recorded multiple times.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        groupedCities.forEach { (color, cities) ->
            items(cities, key = { it.name }) { city ->
                val drawCount = gameState.drawCountFor(city.name)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCitySelected(city.name) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(12.dp),
                                shape = CircleShape,
                                color = city.color.toComposeColor()
                            ) {}
                            Text(
                                text = city.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (drawCount > 0) {
                                Text(
                                    text = "×$drawCount",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "Draw",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InstanceList(
    instances: List<CardInstance>,
    tab: TrackerTab,
    onInstanceClick: (Long) -> Unit
) {
    if (instances.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = when (tab) {
                    TrackerTab.DISCARD -> "No cards in the discard pile."
                    TrackerTab.DECK_TOP -> "No cards on deck top yet."
                    else -> ""
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = when (tab) {
                    TrackerTab.DISCARD ->
                        "Use Record Draw when a city is drawn, or run a Reshuffle Event."
                    TrackerTab.DECK_TOP ->
                        "Cards appear here after a reshuffle event."
                    else -> ""
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        return
    }

    val canDrawFromTop = tab == TrackerTab.DECK_TOP

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = when (tab) {
                    TrackerTab.DISCARD -> "Cities currently in the discard pile."
                    TrackerTab.DECK_TOP ->
                        "Cities on top of the deck after a reshuffle. Tap one when it is drawn."
                    else -> ""
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(instances, key = { it.id }) { instance ->
            val cityColor = PandemicCities.getColorFor(instance.cityName).toComposeColor()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (canDrawFromTop) {
                            Modifier.clickable { onInstanceClick(instance.id) }
                        } else {
                            Modifier
                        }
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(12.dp),
                            shape = CircleShape,
                            color = cityColor
                        ) {}
                        Text(
                            text = instance.cityName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (canDrawFromTop) {
                        Text(
                            text = "Draw",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReshuffleDialog(
    discardCount: Int,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedCity by remember { mutableStateOf<String?>(null) }
    val groupedCities = remember {
        PandemicCities.all.groupBy { it.color }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reshuffle Event") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "1. Pick the city on the bottom of the infection deck.\n" +
                        "2. It joins the $discardCount card(s) in the discard pile.\n" +
                        "3. The discard pile is shuffled and placed on top of the deck."
                )
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    groupedCities.forEach { (color, cities) ->
                        stickyHeader {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 2.dp
                            ) {
                                Text(
                                    text = color.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = color.toComposeColor(),
                                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                                )
                            }
                        }
                        items(cities, key = { it.name }) { city ->
                            val selected = selectedCity == city.name
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedCity = city.name },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceContainerLow
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier.size(8.dp),
                                        shape = CircleShape,
                                        color = city.color.toComposeColor()
                                    ) {}
                                    Text(
                                        text = city.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { selectedCity?.let(onConfirm) },
                enabled = selectedCity != null
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun CityColor.toComposeColor(): Color {
    return when (this) {
        CityColor.BLUE -> Color(0xFF2196F3)
        CityColor.YELLOW -> Color(0xFFFFEB3B)
        CityColor.BLACK -> Color(0xFF212121)
        CityColor.RED -> Color(0xFFF44336)
        CityColor.UNKNOWN -> Color.Gray
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}
