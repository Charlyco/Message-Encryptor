package com.onyenze.messageencryptor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onyenze.messageencryptor.entity.EncryptionKeys
import com.onyenze.messageencryptor.utils.DataStoreManager
import com.onyenze.messageencryptor.utils.Levels

class MainViewModel: ViewModel() {
    val savedKeys: MutableLiveData<MutableList<String>> = MutableLiveData(mutableListOf())
    private val key: MutableLiveData<String> = MutableLiveData("")
    private val alphabets: MutableLiveData<String> = MutableLiveData("")
    val encryptionKey: MutableLiveData<String> = MutableLiveData("")
    val output: MutableLiveData<String> = MutableLiveData("")
    val message: MutableLiveData<String> = MutableLiveData("")

    fun generateKey(level: String?) {
        val listOfCharacters = mutableListOf(
            '!',
            '#',
            '$',
            '%',
            '&',
            '(',
            ')',
            '*',
            '+',
            '/',
            '<',
            '=',
            '>',
            '?',
            '@',
            '[',
            ']',
            '{',
            '}',
            '|',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            '0',
            'B',
            'C',
            'D',
            'F',
            'G',
            'H',
            'J',
            'K',
            'L',
            'M',
            'N',
            'P',
            'Q',
            'R',
            'S',
            'T',
            'V',
            'W',
            'X',
            'Y',
            'Z',
            'b',
            'c',
            'd',
            'f',
            'g',
            'h',
            'j',
            'k',
            'l',
            'm',
            'n',
            'p',
            'q',
            'r',
            's',
            't',
            'v',
            'w',
            'x',
            'y',
            'z'
        )
        var size = 0
        size = when(level) {
            Levels.Standard.toString() -> { 5 }
            Levels.Ultra.toString() -> { 7 }
            else -> { 9 }
        }
        val generatedAlphabets = generateAlphabets(size)
        listOfCharacters.removeAll { it in generatedAlphabets }
        val shuffledCharacters = listOfCharacters.shuffled()
        val randomKey = shuffledCharacters.take(size + 10).joinToString("")

        key.value = randomKey
        encryptionKey.value = randomKey + generatedAlphabets
    }

    private fun generateAlphabets(size: Int): String {
        val vowels = listOf('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U')
        val consonants = listOf(
            'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T',
            'V', 'W', 'X', 'Y', 'Z', 'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p',
            'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z'
        )
        val shuffledConsonants = consonants.shuffled()
        val randomChars = shuffledConsonants.take(size)
        val alphabetsToReplace = (vowels + randomChars).joinToString("")
        alphabets.value = alphabetsToReplace
        return alphabetsToReplace
    }


    private fun encodeBySwap(text: String, key: String, alphabets: String) {
        val mapping = mutableMapOf<Char, Char>()

        val minLen = minOf(key.length, alphabets.length)
        for (i in 0 until minLen) {
            mapping[key[i]] = alphabets[i]
            mapping[alphabets[i]] = key[i]
        }

        val swappedText = StringBuilder()
        text.forEach { char ->
            swappedText.append(mapping.getOrDefault(char, char))
        }
        output.value = swappedText.toString()
    }

    /*private fun encodeText(text: String, key: String, alphabets: String) {
        var result = StringBuilder(text)

        for (index in 0 until alphabets.length) {
            val char = alphabets[index]
                result = StringBuilder(
                    result.toString().replace(char.toString(), key[index].toString())
                )
            }
        output.value = result.toString()
        Log.i("Encode", result.toString())
    }*/

    fun pasteKey(context: Context) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboardManager.hasPrimaryClip()) {
            val clipData = clipboardManager.primaryClip
            val latestItem = clipData?.getItemAt(0)?.text.toString()
            val keySize = (latestItem.length).div(2)
            key.value = latestItem.substring(0,keySize)
            alphabets.value = latestItem.substring(keySize, latestItem.length)
            encryptionKey.value = key.value + alphabets.value
        }
    }

    fun pasteText(context: Context) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (clipboardManager.hasPrimaryClip()) {
            val clipData = clipboardManager.primaryClip
            val latestItem = clipData?.getItemAt(0)
            message.value = latestItem?.text.toString()
        }
    }
    fun copyOutput(context: Context, text: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("output", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    fun transpose(text: String) {
        if (encryptionKey.value?.isEmpty() == true) {
            encodeBySwap(text, key.value!!, alphabets.value!!)
        }else {
            val fullKey = encryptionKey.value
            val keySize = (fullKey?.length)?.div(2)
            if (keySize != null) {
                key.value = fullKey.substring(0,keySize)
            }
            alphabets.value = keySize?.let { encryptionKey.value?.substring(it, fullKey.length) }
            encodeBySwap(text, key.value!!, alphabets.value!!)
        }
    }

    fun copyKey(context: Context?) {
        val keyText = encryptionKey.value
        val clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("keys", keyText)
        clipboardManager.setPrimaryClip(clipData)
    }

    suspend fun updateKeyList(dataStoreManager: DataStoreManager) {
        val newKey = key.value + alphabets.value
        if (dataStoreManager.readEncryptData() == null) {
            val newEncryptionKeys = EncryptionKeys()
            newEncryptionKeys.addToList(newKey)
            dataStoreManager.writeEncryptData(newEncryptionKeys)
        } else {
            val keyList = dataStoreManager.readEncryptData()
            keyList?.addToList(newKey)
            dataStoreManager.writeEncryptData(keyList!!)
        }
    }

    fun clearText() {
        message.value = ""
    }

    suspend fun deleteKey(indexOfItem: Int, dataStoreManager: DataStoreManager): Boolean {
        val encryptionKeys = dataStoreManager.readEncryptData()
        encryptionKeys?.removeFromList(indexOfItem)
        if (encryptionKeys != null) {
            dataStoreManager.writeEncryptData(encryptionKeys)
        }
        val updatedKeyList = dataStoreManager.readEncryptData()?.keyList
        savedKeys.value = updatedKeyList
        return encryptionKeys?.keyList == updatedKeyList
    }

    fun clearKey() {
        encryptionKey.value = ""
    }
}

class MainViewModelFactory(): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel() as T
        }
       throw IllegalArgumentException("Unknown ViewModel class")
    }
}