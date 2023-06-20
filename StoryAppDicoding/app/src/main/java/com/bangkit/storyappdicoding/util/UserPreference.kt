package com.bangkit.storyappdicoding.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bangkit.storyappdicoding.data.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>){
    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val USER_ID_KEY = stringPreferencesKey("userId")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val PASSWORD_KEY = stringPreferencesKey("password")
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val STATE_KEY = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getUser(): Flow<User> {
        return dataStore.data.map { preferences ->
            User(
                preferences[USER_ID_KEY] ?: "",
                preferences[EMAIL_KEY] ?: "",
                preferences[PASSWORD_KEY] ?: "",
                preferences[NAME_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
                preferences[STATE_KEY] ?: false
            )
        }
    }

    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = user.userId
            preferences[EMAIL_KEY] = user.email
            preferences[PASSWORD_KEY] = user.password
            preferences[NAME_KEY] = user.name
            preferences[TOKEN_KEY] = user.token
            preferences[STATE_KEY] = user.isLogin
        }
    }

    suspend fun deleteUser() {
        dataStore.edit { preferences ->
            preferences[STATE_KEY] = false
            preferences.remove(TOKEN_KEY)
        }
    }
}