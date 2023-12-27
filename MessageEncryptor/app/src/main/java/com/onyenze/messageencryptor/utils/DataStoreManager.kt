package com.onyenze.messageencryptor.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.onyenze.messageencryptor.entity.EncryptionKeys
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DataStoreManager(private val applicationContext: Context) {
    private object PreferencesKeys {
        val authKey = stringPreferencesKey("auth")
        val encryptKey = stringPreferencesKey("encrypt")
        val encryptionLevels = stringPreferencesKey("level")
    }

    // Singleton pattern for DataStoreManager
    companion object {
        private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_datastore")
        private val Context.encryptionDataStore: DataStore<Preferences> by preferencesDataStore(name = "encrypt_dataStore")
        private val Context.levelDataStore: DataStore<Preferences> by preferencesDataStore(name = "level_datastore")

        @Volatile
        private var instance: DataStoreManager? = null

        fun getInstance(context: Context): DataStoreManager {
            return instance ?: synchronized(this) {
                instance ?: DataStoreManager(context.applicationContext).also { instance = it }
            }
        }
    }

    suspend fun readAuthData(): String? {
        return applicationContext.authDataStore.data.map { it[PreferencesKeys.authKey] }.firstOrNull()
    }

    suspend fun writeAuthData(authState: String) {
        applicationContext.authDataStore.edit { preferences ->
            preferences[PreferencesKeys.authKey] = authState
        }
    }

    suspend fun readEncryptData(): EncryptionKeys? {
        return applicationContext.encryptionDataStore.data.map {keyList ->
            val serializedKeyList = keyList[PreferencesKeys.encryptKey]
            if (serializedKeyList?.isEmpty() == true) {
                null
            } else {
                serializedKeyList?.let { Json.decodeFromString<EncryptionKeys>(it) }
            }
        }.first()
    }

    suspend fun writeEncryptData(encryptionKeys: EncryptionKeys) {
        val serializedKeyList = Json.encodeToString(encryptionKeys)
        applicationContext.encryptionDataStore.edit { keyList ->
            keyList[PreferencesKeys.encryptKey] = serializedKeyList
        }
    }

    suspend fun readLevelData(): String? {
        return applicationContext.levelDataStore.data.map { it[PreferencesKeys.encryptionLevels] }.firstOrNull()
    }

    suspend fun writeLevelData(level: Levels) {
        applicationContext.levelDataStore.edit {levelPreference ->
            levelPreference[PreferencesKeys.encryptionLevels] = level.name
        }
    }
}
