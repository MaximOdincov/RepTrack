package com.example.reptrack.data.backup

/**
 * Константы для работы с Firestore структурой
 */
object FirestoreConstants {
    const val USERS_COLLECTION = "users"

    const val EXERCISES_COLLECTION = "exercises"
    const val WORKOUT_SESSIONS_COLLECTION = "workout_sessions"
    const val WORKOUT_EXERCISES_COLLECTION = "workout_exercises"
    const val WORKOUT_SETS_COLLECTION = "workout_sets"
    const val WEIGHT_RECORDS_COLLECTION = "weight_records"
    const val WORKOUT_TEMPLATES_COLLECTION = "workout_templates"
    const val TEMPLATE_EXERCISES_COLLECTION = "template_exercises"
    const val USERS_ENTITY_COLLECTION = "user_profiles"
    const val GDPR_CONSENTS_COLLECTION = "gdpr_consents"

    const val CHART_TEMPLATES_COLLECTION = "chart_templates"
    const val FRIEND_CONFIGS_COLLECTION = "friend_configs"
    const val EXERCISE_LINE_CONFIGS_COLLECTION = "exercise_line_configs"
    const val SET_CONFIGS_COLLECTION = "set_configs"

    const val FIELD_ID = "id"
    const val FIELD_USER_ID = "userId"
    const val FIELD_UPDATED_AT = "updatedAt"
    const val FIELD_DELETED_AT = "deletedAt"
}
