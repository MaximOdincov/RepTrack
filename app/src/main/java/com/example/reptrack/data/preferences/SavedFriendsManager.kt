package com.example.reptrack.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.reptrack.domain.statistics.entities.FriendConfig
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Manages persistence of added friends across app restarts
 */
class SavedFriendsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("saved_friends", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private const val KEY_SAVED_FRIENDS = "saved_friends_list"
    }

    /**
     * Save the list of friends to SharedPreferences
     */
    fun saveFriends(friends: List<FriendConfig>) {
        val friendsJson = json.encodeToString(friends)
        prefs.edit().putString(KEY_SAVED_FRIENDS, friendsJson).apply()
    }

    /**
     * Load the list of friends from SharedPreferences
     */
    fun loadFriends(): List<FriendConfig> {
        val friendsJson = prefs.getString(KEY_SAVED_FRIENDS, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<FriendConfig>>(friendsJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Add a friend to the saved list
     */
    fun addFriend(friend: FriendConfig) {
        val currentFriends = loadFriends().toMutableList()
        if (!currentFriends.any { it.friendId == friend.friendId }) {
            currentFriends.add(friend)
            saveFriends(currentFriends)
        }
    }

    /**
     * Remove a friend from the saved list
     */
    fun removeFriend(friendId: String) {
        val currentFriends = loadFriends().toMutableList()
        currentFriends.removeAll { it.friendId == friendId }
        saveFriends(currentFriends)
    }

    /**
     * Update friend color in the saved list
     */
    fun updateFriendColor(friendId: String, color: Long) {
        val currentFriends = loadFriends().toMutableList()
        val index = currentFriends.indexOfFirst { it.friendId == friendId }
        if (index != -1) {
            currentFriends[index] = currentFriends[index].copy(color = color)
            saveFriends(currentFriends)
        }
    }

    /**
     * Clear all saved friends
     */
    fun clearFriends() {
        prefs.edit().remove(KEY_SAVED_FRIENDS).apply()
    }
}
