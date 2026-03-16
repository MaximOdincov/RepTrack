package com.example.reptrack.presentation.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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
    muscleGroupColor: Color = Color.Black
) {
    val iconTint = try {
        exercise.iconColor?.let { Color(it.toColorInt()) }
            ?: Color.Black
    } catch (e: Exception) {
        Color.Black
    }

    val backgroundColor = muscleGroupColor.copy(alpha = 0.10f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(modifier = Modifier.padding(vertical = 10.dp)) {
            Box(
                modifier = Modifier.size(42.dp)
            ) {
                // Blurred background
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            color = iconTint.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .blur(radius = 0.5.dp)
                )

                // Sharp icon on top
                Icon(
                    modifier = Modifier
                        .padding(4.dp)
                        .matchParentSize(),
                    painter = painterResource(
                        id = exercise.iconRes ?: R.drawable.exercise_default_icon
                    ),
                    contentDescription = null,
                    tint = iconTint
                )
            }

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
        }
    }
}
