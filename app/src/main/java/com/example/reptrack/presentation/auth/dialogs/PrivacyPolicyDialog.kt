package com.example.reptrack.presentation.auth.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.reptrack.R

@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit,
    onAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf("ru") }
    val scrollState = rememberLazyListState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
                .padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = PrivacyPolicyText.RU_TITLE,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close dialog",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Language selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TabRow(
                        selectedTabIndex = if (selectedLanguage == "ru") 0 else 1,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        divider = {},
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[if (selectedLanguage == "ru") 0 else 1]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedLanguage == "ru",
                            onClick = { selectedLanguage = "ru" },
                            text = {
                                Text(
                                    text = "Русский",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurface
                        )
                        Tab(
                            selected = selectedLanguage == "en",
                            onClick = { selectedLanguage = "en" },
                            text = {
                                Text(
                                    text = "English",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Content with scrollbar
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 20.dp),
                    state = scrollState
                ) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Date
                            Text(
                                text = if (selectedLanguage == "ru") PrivacyPolicyText.RU_EFFECTIVE_DATE
                                else PrivacyPolicyText.EN_EFFECTIVE_DATE,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            )

                            // Policy content
                            Text(
                                text = if (selectedLanguage == "ru") PrivacyPolicyText.RU_CONTENT
                                else PrivacyPolicyText.EN_CONTENT,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 20.sp
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(
                            text = "Отмена" + if (selectedLanguage == "en") "/Cancel" else "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "Принимаю" + if (selectedLanguage == "en") "/I Accept" else "",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}