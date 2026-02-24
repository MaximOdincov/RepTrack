package com.example.reptrack.presentation.main.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.reptrack.domain.workout.entities.Exercise

private const val DEFAULT_ICON = 3 //add real icon_id
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseCard(exercise: Exercise){
    Box(modifier = Modifier.fillMaxWidth()){
        Row(modifier = Modifier){
            //Иконка
            Icon(
                modifier = Modifier.size(30.dp),
                painter = painterResource(exercise.iconRes ?: DEFAULT_ICON),
                contentDescription = null
            )

            //Название + группа мыщц
            Column(modifier = Modifier) {
                Text(exercise.name)
                Text(exercise.muscleGroup.toString())
            }
        }
    }
}