package com.example.reptrack.presentation.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reptrack.R
import com.example.reptrack.domain.workout.entities.Exercise
import com.example.reptrack.domain.workout.entities.WorkoutSet
import androidx.core.graphics.toColorInt

@Composable
fun ExerciseCard(
    exercise: Exercise,
    lastResult: List<WorkoutSet>? = null
) {
    val backgroundColor =
        exercise.backgroundColor?.let { Color(it.toColorInt()) } ?: MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(modifier = Modifier.padding(vertical = 10.dp)){
            val iconTint = try {
                exercise.iconColor?.let { Color(it.toColorInt()) }
                    ?: Color.Black
            } catch (e: Exception) {
                Color.Black
            }

            Icon(
                modifier = Modifier.size(42.dp),
                painter = painterResource(
                    id = exercise.iconRes ?: R.drawable.exercise_default_icon
                ),
                contentDescription = null,
                tint = iconTint
            )

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = exercise.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = exercise.muscleGroup.toString(),
                    fontStyle = FontStyle.Italic,
                    fontSize = 18.sp
                )
            }

            if (lastResult != null && lastResult.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    lastResult.take(3).forEach { set ->
                        val setText = if (set.weight != null && set.reps != null) {
                            "${set.index} set: ${set.weight} х ${set.reps}"
                        } else if (set.weight != null) {
                            "${set.index} set: ${set.weight} кг"
                        } else if (set.reps != null) {
                            "${set.index} set: ${set.reps} повт"
                        } else {
                            "${set.index} set"
                        }
                        Text(
                            text = setText,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
