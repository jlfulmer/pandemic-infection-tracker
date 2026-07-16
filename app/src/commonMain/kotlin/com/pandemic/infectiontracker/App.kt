package com.pandemic.infectiontracker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.pandemic.infectiontracker.ui.theme.PandemicInfectionTrackerTheme
import org.jetbrains.compose.resources.painterResource
import pandemic_infection_tracker.app.generated.resources.Res
import pandemic_infection_tracker.app.generated.resources.pandemic_background

private enum class TrackerTab(val label: String) {
    DISCARD("Discard Pile"),
    DECK_TOP("On Deck Top")
}

private enum class DialogMode {
    NONE, EPIDEMIC, DRAW
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    PandemicInfectionTrackerTheme {
        var gameState by remember { mutableStateOf(GameState()) }
        var selectedTab by remember { mutableIntStateOf(0) }
        var dialogMode by remember { mutableStateOf(DialogMode.NONE) }
        var showResetDialog by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(Res.drawable.pandemic_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // This prevents distortion
            )

            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Box(contentAlignment = Alignment.Center) {
                                // Yellow Outline
                                Text(
                                    text = "Pandemic Infection Tracker",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = Color(0xFFFFD200), // Pandemic Yellow
                                        drawStyle = Stroke(width = 6f)
                                    )
                                )
                                // Red Interior
                                Text(
                                    text = "Pandemic Infection Tracker",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = Color(0xFFE21E26) // Pandemic Red
                                    )
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                },
                bottomBar = {
                    BottomAppBar(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.height(48.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { showResetDialog = true }
                            ) {
                                Text("Reset")
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        val epidemicGradient = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0B7519),
                                Color(0xFF57A60F),
                                Color(0xFF021E0F),
                                Color(0xFF042618)
                            )
                        )
                        Button(
                            onClick = { dialogMode = DialogMode.EPIDEMIC },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(brush = epidemicGradient, shape = ButtonDefaults.shape)
                                .clip(ButtonDefaults.shape),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color(0xFFFF4444)
                            ),
                            contentPadding = PaddingValues()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                // Yellow Outline
                                Text(
                                    text = "Epidemic",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = Color(0xFFFFD200), // Pandemic Yellow
                                        drawStyle = Stroke(width = 6f)
                                    )
                                )
                                // Red Interior
                                Text(
                                    text = "Epidemic",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = Color(0xFFE21E26) // Pandemic Red
                                    )
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        Button(
                            onClick = { dialogMode = DialogMode.DRAW },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(ButtonDefaults.shape),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues()
                        ) {
                            Text(
                                text = "New City",
                                style = MaterialTheme.typography.labelLarge.copy()
                            )

                        }
                    }

                    TabRow(selectedTabIndex = selectedTab) {
                        TrackerTab.entries.forEachIndexed { index, tab ->
                            val count = when (tab) {
                                TrackerTab.DISCARD -> gameState.inDiscard.size
                                TrackerTab.DECK_TOP -> gameState.onDeckTop.size
                            }
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text("${tab.label}\n ($count)") }
                            )
                        }
                    }

                    when (TrackerTab.entries[selectedTab]) {
                        TrackerTab.DISCARD -> InstanceList(
                            instances = gameState.inDiscard,
                            tab = TrackerTab.DISCARD,
                            onInstanceClick = { instanceId ->
                                gameState = gameState.removeFromDiscard(instanceId)
                            }
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
        }


        if (dialogMode != DialogMode.NONE) {
            CardSelectionDialog(
                multiSelect = dialogMode == DialogMode.DRAW,
                onDismiss = { dialogMode = DialogMode.NONE },
                onConfirm = { cityNames ->
                    var currentGameState = gameState
                    cityNames.forEach { cityName ->
                        currentGameState = when (dialogMode) {
                            DialogMode.EPIDEMIC -> currentGameState.epidemicEvent(cityName)
                            DialogMode.DRAW -> currentGameState.recordDraw(cityName)
                            else -> currentGameState
                        }
                    }
                    gameState = currentGameState
                    dialogMode = DialogMode.NONE
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

@OptIn(ExperimentalFoundationApi::class)
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
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = when (tab) {
                    TrackerTab.DISCARD ->
                        "Use New City when a city is drawn, or run an Epidemic Event."
                    TrackerTab.DECK_TOP ->
                        "Cards appear here after an epidemic event."
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = when (tab) {
                    TrackerTab.DISCARD -> "Cities currently in the discard pile."
                    TrackerTab.DECK_TOP ->
                        "Cities on top of the deck after an epidemic. Tap one when it is drawn."
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (tab == TrackerTab.DECK_TOP) {
            val groupedByStack = instances.groupBy { it.epidemicCount }
                .entries
                .sortedByDescending { it.key }

            groupedByStack.forEach { entry ->
                val level = entry.key
                val stackInstances = entry.value
                stickyHeader {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = "Epidemic $level",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                }
                items(stackInstances, key = { it.id }) { instance ->
                    InstanceCard(instance, onInstanceClick)
                }
            }
        } else {
            items(instances, key = { it.id }) { instance ->
                InstanceCard(instance, onInstanceClick)
            }
        }
    }
}

@Composable
private fun InstanceCard(
    instance: CardInstance,
    onInstanceClick: (Long) -> Unit
) {
    val cityColor = PandemicCities.getColorFor(instance.cityName).toComposeColor()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInstanceClick(instance.id) },
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text =  if(instance.location == CardLocation.ON_DECK_TOP) {
                        "Draw"
                    } else {
                        "Remove"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardSelectionDialog(
    multiSelect: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    var selectedCities by remember { mutableStateOf(emptyList<String>()) }
    val groupedCities = remember {
        PandemicCities.all.groupBy { it.color }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text (
                text = if (multiSelect) {
                    "Select Cards"
                } else {
                    "1. Pick the city on the bottom of the infection deck.\n" +
                            "2. It joins the card(s) in the discard pile.\n" +
                            "3. The discard pile is shuffled and placed on top of the deck."
                },
                style = if (multiSelect) {
                    MaterialTheme.typography.headlineSmall
                } else {
                    MaterialTheme.typography.bodyMedium
                },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    groupedCities.forEach { (_, cities) ->
                        items(cities, key = { it.name }) { city ->
                            val count = selectedCities.count { it == city.name }
                            val selected = count > 0
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedCities = if (multiSelect) {
                                            selectedCities + city.name
                                        } else {
                                            listOf(city.name)
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceContainerLow
                                    }
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
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
                                    if (selected && multiSelect) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "x$count",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                TextButton(
                    onClick = { onConfirm(selectedCities) },
                    enabled = selectedCities.isNotEmpty()
                ) {
                    Text("Confirm")
                }
            }
        },
        dismissButton = null
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
