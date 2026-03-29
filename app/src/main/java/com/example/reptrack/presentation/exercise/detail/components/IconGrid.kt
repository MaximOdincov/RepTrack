package com.example.reptrack.presentation.exercise.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.reptrack.R

/**
 * Grid of available exercise icons
 *
 * @param selectedIconRes The currently selected icon resource
 * @param onIconSelected Callback when an icon is selected
 */
@Composable
fun IconGrid(
    selectedIconRes: Int?,
    onIconSelected: (Int) -> Unit
) {
    // Remember the icons list to avoid recreating it on every recomposition
    val icons = remember {
        listOf(
            // Exercise specific icons
            R.drawable.exercise_bench_press,
            R.drawable.exercise_default_icon,
            R.drawable.exercise_icon_3,
            R.drawable.exercise_icon_4,
            R.drawable.exercis_icon_2,
            R.drawable.bench_press,
            R.drawable.barbell_energy,
            R.drawable.dumbell,
            R.drawable.bic_dumbell,
            R.drawable.dumbel_with_a_hand,
            R.drawable.weights,
            R.drawable.weight_lifting,

            // Muscle group icons
            R.drawable.muscle_icon_chest,
            R.drawable.muscle_icon_back,
            R.drawable.muscle_icon_legs,
            R.drawable.muscle_icon_arms,
            R.drawable.muscle_icon_abs,
            R.drawable.muscle_icon_cardio,
            R.drawable.back_muscles,
            R.drawable.muscles,
            R.drawable.abs,

            // Fitness and cardio icons
            R.drawable.fitness,
            R.drawable.fitness_women,
            R.drawable.stationary_bike,
            R.drawable.treadmill,
            R.drawable.rowing,
            R.drawable.bicycle,
            R.drawable.leg_push,
            R.drawable.calories,
            R.drawable.heart_dumbell,
            R.drawable.fins,

            // Body part icons
            R.drawable.foot,
            R.drawable.leg,

            // Fun/character icons
            R.drawable.bear,
            R.drawable.bear_big,
            R.drawable.wolf,
            R.drawable.moose,
            R.drawable.hedgehog,
            R.drawable.elephant,
            R.drawable.deer,
            R.drawable.duck,
            R.drawable.walrus,
            R.drawable.teddy_bear,

            // Object/weapon icons
            R.drawable.chess_sword,
            R.drawable.sword,
            R.drawable.tank,
            R.drawable.plane,
            R.drawable.robot,
            R.drawable.car,
            R.drawable.castle,

            // Achievement icons
            R.drawable.trophy,
            R.drawable.star,
            R.drawable.goal,
            R.drawable.like,
            R.drawable.best_choice,
            R.drawable.rocket,
            R.drawable.idea,

            // Misc icons
            R.drawable.skull,
            R.drawable.thunder,
            R.drawable.fire,
            R.drawable.eye,
            R.drawable.heart,
            R.drawable.dna,
            R.drawable.focus,
            R.drawable.speedometr,
            R.drawable.chronometr,
            R.drawable.sand_clock,

            // UI icons (can be used creatively)
            R.drawable.main_screen_icon,
            R.drawable.library_icon,
            R.drawable.timer_icon,
            R.drawable.profile_icon,
            R.drawable.arrow_up_icon
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        items(icons.size, key = { index -> icons[index] }) { index ->
            val iconRes = icons[index]
            val isSelected = iconRes == selectedIconRes

            Surface(
                shape = RoundedCornerShape(12.dp),
                border = if (isSelected) {
                    BorderStroke(2.dp, Color(0xFF6200EE))
                } else {
                    BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
                },
                color = if (isSelected) {
                    Color(0xFF6200EE).copy(alpha = 0.1f)
                } else {
                    Color.White
                },
                modifier = Modifier
                    .clickable { onIconSelected(iconRes) }
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = "Exercise icon",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }
    }
}
