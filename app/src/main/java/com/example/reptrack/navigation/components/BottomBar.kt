package com.example.reptrack.navigation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.reptrack.R
import com.example.reptrack.presentation.theme.LightAccentOrange

/**
 * Navigation items for bottom bar
 */
sealed class BottomNavItem(
    val route: String,
    val iconResId: Int,
    val title: String
) {
    data object Main : BottomNavItem("main", R.drawable.main_screen_icon, "Main")
    data object Library : BottomNavItem("exercises/view", R.drawable.library_icon, "Library")
    data object Timer : BottomNavItem("timer", R.drawable.timer_icon, "Timer")
    data object Profile : BottomNavItem("profile", R.drawable.profile_icon, "Profile")

    companion object {
        val items = listOf(Main, Library, Timer, Profile)
    }
}

/**
 * Bottom navigation bar with 4 icons
 *
 * @param navController Navigation controller for route handling
 */
@Composable
fun BottomBar(
    navController: NavController
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem.items.forEach { item ->
                // Extract base route for comparison (remove parameters like /view, /workout)
                val baseRoute = item.route.substringBeforeLast("/")
                val isCurrentlySelected = currentRoute?.startsWith(baseRoute) == true

                BottomBarItem(
                    item = item,
                    isSelected = isCurrentlySelected,
                    onClick = {
                        if (!isCurrentlySelected) {
                            navController.navigate(item.route) {
                                // TODO: Check if popUpTo causes issues with Profile
                                // popUpTo(navController.graph.startDestinationId) {
                                //     saveState = true
                                // }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

/**
 * Single bottom navigation item
 */
@Composable
private fun BottomBarItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        val iconSize = 38.dp

        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(12.dp))
                .then(
                    if (isSelected) {
                        Modifier.background(LightAccentOrange)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = item.iconResId),
                contentDescription = item.title,
                modifier = Modifier.size(32.dp),
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}
