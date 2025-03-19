package com.silab.data.datastore

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.datastore by preferencesDataStore(name = "datastore")

class DataStore (private val context: Context) {

    private val USER_TOKEN = stringPreferencesKey("USER_TOKEN")
    private val USER_NAME = stringPreferencesKey("USER_NAME")
    private val USER_EMAIL = stringPreferencesKey("USER_EMAIL")

    suspend fun saveToken(token: String) {
        context.datastore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    suspend fun getToken(): String? {
        val preferences = context.datastore.data.first()
        return preferences[USER_TOKEN]
    }

    suspend fun saveName(name: String) {
        context.datastore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    suspend fun getName(): String? {
        val preferences = context.datastore.data.first()
        return preferences[USER_NAME]
    }
    suspend fun saveEmail(email: String) {
        context.datastore.edit { preferences ->
            preferences[USER_EMAIL] = email
        }
    }

    suspend fun getEmail(): String? {
        val preferences = context.datastore.data.first()
        return preferences[USER_EMAIL]
    }

    suspend fun clear() {
        context.datastore.edit { preferences ->
            preferences.clear()
        }
    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: DataStore? = null

        fun getInstance(context: Context): DataStore {
            Log.d("DataStore", "Instance created")
            return INSTANCE ?: synchronized(this) {
                val instance = DataStore(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

}