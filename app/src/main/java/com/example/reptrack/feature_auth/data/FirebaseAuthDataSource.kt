package com.example.reptrack.feature_auth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
class FirebaseAuthDataSource(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun signUp(email: String, password: String): FirebaseUser =
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .await().user ?: throw IllegalStateException("Sign up error - user is null")

    suspend fun signIn(email: String, password: String): FirebaseUser =
        firebaseAuth.signInWithEmailAndPassword(email,password)
            .await().user ?: throw IllegalStateException("Sign in error - user is null")

    suspend fun signInAsGuest(): FirebaseUser =
        firebaseAuth.signInAnonymously()
            .await().user ?: throw IllegalStateException("Sign In Anonymously error - user is null")

    suspend fun resetPassword(email: String){
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    suspend fun signInWithGoogle(idToken: String): FirebaseUser{
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return firebaseAuth.signInWithCredential(credential)
            .await().user?: throw IllegalStateException("Sign in error - user is null")
    }

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser
}