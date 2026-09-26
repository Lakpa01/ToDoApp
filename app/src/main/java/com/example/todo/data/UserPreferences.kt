package com.example.todo.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_PROFILE_IMAGE_URI = stringPreferencesKey("profile_image_uri")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_LOGGED_IN] ?: false
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_NAME] ?: "User"
    }

    val profileImageUri: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_PROFILE_IMAGE_URI]
    }

    suspend fun saveUserData(email: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_EMAIL] = email
            prefs[KEY_IS_LOGGED_IN] = true
        }
    }

    suspend fun updateProfile(name: String, imageUri: String?) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_NAME] = name
            if (imageUri != null) {
                prefs[KEY_PROFILE_IMAGE_URI] = imageUri
            }
        }
    }

    suspend fun logout() {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = false
        }
    }
}