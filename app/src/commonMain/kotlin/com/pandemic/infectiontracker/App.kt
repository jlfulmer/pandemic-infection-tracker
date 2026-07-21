package com.pandemic.infectiontracker

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.pandemic.infectiontracker.generated.resources.*
import com.pandemic.infectiontracker.ui.theme.Dimens
import com.pandemic.infectiontracker.ui.theme.PandemicInfectionTrackerTheme
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

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
    val gameStateSaver = remember {
        Saver<GameState, String>(
            save = { Json.encodeToString(it) },
            restore = { Json.decodeFromString(it) }
        )
    }

    PandemicInfectionTrackerTheme {
        var gameState by rememberSaveable(stateSaver = gameStateSaver) { mutableStateOf(GameState()) }
        var selectedTab by rememberSaveable { mutableIntStateOf(0) }
        var dialogMode by rememberSaveable { mutableStateOf(DialogMode.NONE) }
        var showResetDialog by rememberSaveable { mutableStateOf(false) }

        // Optimization: Use derivedStateOf for filtered lists to avoid re-calculating on every recomposition
        // that doesn't affect the gameState or selectedTab.
        val currentInstances by remember(gameState, selectedTab) {
            derivedStateOf {
                if (TrackerTab.entries[selectedTab] == TrackerTab.DISCARD) gameState.inDiscard else gameState.onDeckTop
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(Res.drawable.pandemic_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Scaffold(
                containerColor = Color.Transparent,
                topBar = { PandemicTopBar() },
                bottomBar = {
                    PandemicBottomBar(onResetClick = { showResetDialog = true })
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    MainActionButtonRow(
                        onAddCityClick = { dialogMode = DialogMode.DRAW },
                        onEpidemicClick = { dialogMode = DialogMode.EPIDEMIC }
                    )

                    TabRow(
                        selectedTabIndex = selectedTab,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        TrackerTab.entries.forEachIndexed { index, tab ->
                            val count = remember(gameState, tab) {
                                if (tab == TrackerTab.DISCARD) gameState.inDiscard.size else gameState.onDeckTop.size
                            }
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text("${tab.label}\n ($count)") }
                            )
                        }
                    }

                    InstanceList(
                        instances = currentInstances,
                        tab = TrackerTab.entries[selectedTab],
                        onInstanceClick = { instanceId ->
                            gameState = if (TrackerTab.entries[selectedTab] == TrackerTab.DISCARD) {
                                gameState.removeFromDiscard(instanceId)
                            } else {
                                gameState.drawFromTop(instanceId)
                            }
                        }
                    )
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
            PandemicResetDialog(
                onConfirm = {
                    gameState = gameState.reset()
                    showResetDialog = false
                },
                onDismiss = { showResetDialog = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PandemicTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Pandemic Infection Tracker",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color(0xFFFFD200),
                        drawStyle = Stroke(width = 6f)
                    )
                )
                Text(
                    text = "Pandemic Infection Tracker",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color(0xFFE21E26)
                    )
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
private fun PandemicBottomBar(onResetClick: () -> Unit) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f),
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.height(Dimens.BottomBarHeight),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onResetClick) {
                Text("Reset Game")
            }
        }
    }
}

@Composable
private fun MainActionButtonRow(
    onAddCityClick: () -> Unit,
    onEpidemicClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.SpacingLarge, vertical = Dimens.SpacingMedium),
        horizontalArrangement = Arrangement.spacedBy(Dimens.SpacingLarge),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(
            text = "Add City",
            imageRes = Res.drawable.city_button_normal,
            onClick = onAddCityClick,
            modifier = Modifier.weight(1f)
        )
        ActionButton(
            text = "Epidemic",
            imageRes = Res.drawable.epidemic_button_normal,
            onClick = onEpidemicClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    imageRes: org.jetbrains.compose.resources.DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(Dimens.ActionButtonHeight)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
        )
    }
}

@Composable
private fun PandemicResetDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset game?") },
        text = { Text("All recorded draws, discard pile, and deck top tracking will be cleared.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Reset", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun InstanceList(
    instances: List<CardInstance>,
    tab: TrackerTab,
    onInstanceClick: (Long) -> Unit
) {
    if (instances.isEmpty()) {
        EmptyStateView(tab)
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(Dimens.SpacingLarge),
        verticalArrangement = Arrangement.spacedBy(Dimens.CardSpacing)
    ) {
        item {
            ListHeader(tab)
        }

        if (tab == TrackerTab.DECK_TOP) {
            val groupedByStack = instances.groupBy { it.epidemicCount }
                .entries
                .sortedByDescending { it.key }

            groupedByStack.forEach { entry ->
                item {
                    StackHeader(level = entry.key)
                }
                items(entry.value, key = { it.id }) { instance ->
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
private fun EmptyStateView(tab: TrackerTab) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.SpacingExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (tab == TrackerTab.DISCARD)
                "No cards in the discard pile."
            else
                "No cards on deck top yet.",

            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (tab == TrackerTab.DISCARD) "Use Add City when a city is drawn, or run an Epidemic Event." else "Cards appear here after an epidemic event.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = Dimens.SpacingMedium)
        )
    }
}

@Composable
private fun ListHeader(tab: TrackerTab) {
    Text(
        text = if (tab == TrackerTab.DISCARD)
            "Cities currently in the discard pile."
        else
            "Cities on top of the deck after an epidemic.\nTap one when it is drawn.",
        
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun StackHeader(level: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF57A60F)
    ) {
        Text(
            text = "Epidemic $level",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = Dimens.SpacingMedium, bottom = Dimens.SpacingSmall)
        )
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
            containerColor = Color.White.copy(alpha = 0.7f),
            contentColor = Color.Black
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.CardPaddingHorizontal, vertical = Dimens.CardPaddingVertical),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.CardPaddingVertical)
            ) {
                Surface(
                    modifier = Modifier.size(Dimens.CityColorIndicatorSize),
                    shape = CircleShape,
                    color = cityColor
                ) {}
                Text(
                    text = instance.cityName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
            Text(
                text = if (instance.location == CardLocation.ON_DECK_TOP) "Draw" else "Remove",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
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
    val groupedCities = remember { PandemicCities.all.groupBy { it.color } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (multiSelect) "Select Cards" else "1. Pick the city on the bottom of the infection deck.\n2. It joins the card(s) in the discard pile.\n3. The discard pile is shuffled and placed on top of the deck.",
                style = if (multiSelect) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpacingMedium)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpacingSmall)
                ) {
                    groupedCities.forEach { (_, cities) ->
                        items(cities, key = { it.name }) { city ->
                            val count = selectedCities.count { it == city.name }
                            SelectionCard(
                                city = city,
                                selectedCount = count,
                                multiSelect = multiSelect,
                                onClick = {
                                    selectedCities = if (multiSelect) selectedCities + city.name else listOf(city.name)
                                }
                            )
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
                TextButton(onClick = onDismiss) { Text("Cancel") }
                TextButton(
                    onClick = { onConfirm(selectedCities) },
                    enabled = selectedCities.isNotEmpty()
                ) { Text("Confirm") }
            }
        }
    )
}

@Composable
private fun SelectionCard(
    city: City,
    selectedCount: Int,
    multiSelect: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (selectedCount > 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.CardPaddingVertical),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.CardPaddingVertical)
            ) {
                Surface(
                    modifier = Modifier.size(Dimens.SmallIndicatorSize),
                    shape = CircleShape,
                    color = city.color.toComposeColor()
                ) {}
                Text(text = city.name, style = MaterialTheme.typography.bodyMedium)
            }
            if (selectedCount > 0 && multiSelect) {
                Text(
                    text = "x$selectedCount",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
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
