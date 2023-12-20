package com.onyenze.messageencryptor.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
class EncryptionKeys {
    @SerialName("keylist")
    val keyList = mutableListOf<String>()

    fun addToList(key: String) {
        keyList.add(key)
    }

    fun removeFromList(index: Int) {
        keyList.removeAt(index)
    }
}