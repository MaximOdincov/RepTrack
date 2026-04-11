package com.example.reptrack.presentation.template.list.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import com.example.reptrack.R
import com.example.reptrack.domain.workout.entities.WorkoutTemplate
import com.example.reptrack.presentation.template.components.SwipeToDeleteTemplateCard
import com.example.reptrack.presentation.template.list.stores.TemplateListStore
import com.example.reptrack.presentation.theme.LightAccentOrange
import kotlinx.coroutines.delay

/**
 * Template List screen
 *
 * @param store MVIKotlin store for state management
 * @param onNavigateToDetail Callback when navigating to template detail
 * @param onSelectTemplateAndBack Callback when selecting template (SELECT_MODE)
 * @param onNavigateToAddTemplate Callback when clicking add template button
 * @param onInitialize Callback to initialize store with mode
 */
@Composable
fun TemplateListScreen(
    store: TemplateListStore,
    onNavigateToDetail: (String) -> Unit = {},
    onSelectTemplateAndBack: (WorkoutTemplate) -> Unit = {},
    onNavigateToAddTemplate: () -> Unit = {},
    onInitialize: (TemplateListStore.TemplateListMode) -> Unit = {}
) {
    val state by store.states.collectAsState(TemplateListStore.State())

    // Use rememberSaveable to preserve search query across configuration changes
    var searchInput by rememberSaveable { mutableStateOf("") }

    // Sync searchInput with state when screen is first created or restored
    LaunchedEffect(state.searchQuery) {
        if (searchInput.isBlank() && state.searchQuery.isNotBlank()) {
            searchInput = state.searchQuery
        }
    }

    // Apply saved search query immediately after initialization
    LaunchedEffect(Unit) {
        onInitialize(TemplateListStore.TemplateListMode.VIEW_MODE)
        if (searchInput.isNotBlank()) {
            store.accept(TemplateListStore.Intent.SearchChanged(searchInput))
        }
    }

    // Collect labels
    LaunchedEffect(Unit) {
        store.labels.collect { label ->
            when (label) {
                is TemplateListStore.Label.NavigateToDetail -> {
                    onNavigateToDetail(label.templateId)
                }
                is TemplateListStore.Label.SelectTemplateAndBack -> {
                    onSelectTemplateAndBack(label.template)
                }
                is TemplateListStore.Label.NavigateToAddTemplate -> {
                    onNavigateToAddTemplate()
                }
            }
        }
    }

    TemplateListContent(
        state = state,
        searchInput = searchInput,
        onSearchInputChange = { searchInput = it },
        onTemplateClick = { template ->
            store.accept(TemplateListStore.Intent.TemplateClicked(template))
        },
        onSearchChanged = { query ->
            store.accept(TemplateListStore.Intent.SearchChanged(query))
        },
        onAddTemplateClick = {
            store.accept(TemplateListStore.Intent.AddTemplateClicked)
        },
        onDeleteTemplate = { templateId ->
            store.accept(TemplateListStore.Intent.DeleteTemplate(templateId))
        }
    )
}

/**
 * Template List content with search and list of templates
 */
@Composable
private fun TemplateListContent(
    state: TemplateListStore.State,
    searchInput: String,
    onSearchInputChange: (String) -> Unit,
    onTemplateClick: (WorkoutTemplate) -> Unit,
    onSearchChanged: (String) -> Unit,
    onAddTemplateClick: () -> Unit,
    onDeleteTemplate: (String) -> Unit
) {
    var templateToDelete by remember { mutableStateOf<WorkoutTemplate?>(null) }

    // Debounce search with 300ms delay
    LaunchedEffect(searchInput) {
        delay(300)
        if (searchInput != state.searchQuery) {
            onSearchChanged(searchInput)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTemplateClick,
                containerColor = LightAccentOrange
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_up_icon),
                    contentDescription = "Add Template",
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Search bar
                TemplateSearchBar(
                    query = searchInput,
                    onQueryChange = { newValue ->
                        onSearchInputChange(newValue)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = LightAccentOrange
                        )
                    }
                } else {
                    // Use filteredTemplates if searchInput is not empty
                    val templatesToShow = if (searchInput.isBlank()) {
                        state.templates
                    } else {
                        state.filteredTemplates
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = templatesToShow,
                            key = { it.id }
                        ) { template ->
                            SwipeToDeleteTemplateCard(
                                template = template,
                                onClick = { onTemplateClick(template) },
                                onDeleteTemplate = { templateToDelete = template },
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    templateToDelete?.let { template ->
        AlertDialog(
            onDismissRequest = { templateToDelete = null },
            title = {
                Text("Удалить шаблон?")
            },
            text = {
                Text("Вы уверены, что хотите удалить шаблон \"${template.name}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTemplate(template.id)
                        templateToDelete = null
                    }
                ) {
                    Text("Удалить", color = Color(0xFFEF5350))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { templateToDelete = null }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}

/**
 * Search bar for templates
 */
@Composable
private fun TemplateSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = {
            androidx.compose.material3.Text(
                "Поиск шаблонов...",
                color = Color.Gray
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        singleLine = true
    )
}
