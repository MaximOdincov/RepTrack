package com.example.reptrack.di

import com.example.reptrack.data.workout.repositories.TrainingSessionRepositoryImpl
import com.example.reptrack.data.workout.repositories.TrainingTemplateRepository
import com.example.reptrack.domain.workout.repositories.TrainingSessionRepository
import com.example.reptrack.domain.workout.repositories.TrainingTemplateRepository as TrainingTemplateRepositoryInterface
import com.example.reptrack.domain.workout.usecases.calendar.CalendarUseCase
import com.example.reptrack.domain.workout.usecases.exercises.*
import com.example.reptrack.domain.workout.usecases.sessions.*
import com.example.reptrack.domain.workout.usecases.templates.*
import org.koin.dsl.module

val workoutModule = module {
    // Repositories
    single<TrainingSessionRepository> {
        TrainingSessionRepositoryImpl(workoutDao = get())
    }
    single<TrainingTemplateRepositoryInterface> {
        TrainingTemplateRepository(templateDao = get())
    }

    // Calendar Use Cases
    factory { CalendarUseCase(get()) }

    // Exercise Use Cases
    factory { GetAllExercisesUseCase(get()) }
    factory { GetExerciseByIdUseCase(get()) }
    factory { CreateExerciseUseCase(get()) }
    factory { UpdateExerciseUseCase(get()) }
    factory { DeleteExerciseUseCase(get()) }
    factory { CreateWorkoutExerciseUseCase(get()) }
    factory { UpdateWorkoutExerciseUseCase(get()) }
    factory { DeleteWorkoutExerciseUseCase(get()) }
    factory { GetWorkoutExerciseByIdUseCase(get()) }
    factory { GetLastExerciseProgressUseCase(get()) }

    // Session Use Cases
    factory { GetWorkoutSessionByIdUseCase(get()) }
    factory { CreateWorkoutSessionUseCase(get()) }
    factory { CreateWorkoutSessionFromTemplateUseCase(get()) }
    factory { UpdateWorkoutSessionUseCase(get()) }
    factory { DeleteWorkoutSessionUseCase(get()) }

    // Template Use Cases
    factory { GetAllWorkoutTemplatesUseCase(get()) }
    factory { GetWorkoutTemplateByIdUseCase(get()) }
    factory { CreateWorkoutTemplateUseCase(get()) }
    factory { UpdateWorkoutTemplateUseCase(get()) }
    factory { DeleteWorkoutTemplateUseCase(get()) }
    factory { GetWorkoutCalendarUseCase(get()) }
}
