package com.example.reptrack.data.backup

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await

class FirebaseBackupDataSource(
    private val firestore: FirebaseFirestore
) {
    suspend fun listDocuments(
        userId: String,
        entityType: String
    ): QuerySnapshot {
        return firestore
            .collection("users")
            .document(userId)
            .collection(entityType)
            .get()
            .await()
    }

    suspend fun getDocument(
        userId: String,
        entityType: String,
        entityId: String
    ): DocumentSnapshot? {
        return firestore
            .collection("users")
            .document(userId)
            .collection(entityType)
            .document(entityId)
            .get()
            .await()
    }

    suspend fun uploadDocument(
        userId: String,
        entityType: String,
        entityId: String,
        data: Map<String, Any?>
    ) {
        firestore
            .collection("users")
            .document(userId)
            .collection(entityType)
            .document(entityId)
            .set(data, com.google.firebase.firestore.SetOptions.merge())
            .await()
    }

    suspend fun deleteDocument(
        userId: String,
        entityType: String,
        entityId: String
    ) {
        firestore
            .collection("users")
            .document(userId)
            .collection(entityType)
            .document(entityId)
            .delete()
            .await()
    }
}
