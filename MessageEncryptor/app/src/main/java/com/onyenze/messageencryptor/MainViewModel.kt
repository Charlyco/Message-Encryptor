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
    val savedKeys: MutableLiveData<List<String>> = MutableLiveData(listOf())
    private val key: MutableLiveData<String> = MutableLiveData("")
    private val alphabets: MutableLiveData<String> = MutableLiveData("")
    val encryptionKey: MutableLiveData<String> = MutableLiveData("")
    val output: MutableLiveData<String> = MutableLiveData("")
    val message: MutableLiveData<String> = MutableLiveData("")

    fun generateKey(level: Levels) {
        val listOfCharacters = listOf(
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
            Levels.Standard -> { 5 }
            Levels.Ultra -> { 10 }
            else -> { 15 }
        }
        val randomChars = List(size + 10) { listOfCharacters.random() }
        val randomKey = StringBuilder()
        for (i in randomChars) {
            randomKey.append(i)
        }
        key.value = randomKey.toString()
        encryptionKey.value = randomKey.toString() + generateAlphabets(size)
    }

    private fun generateAlphabets(size: Int): String {
        val randomChars = mutableListOf('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U')
        val listOfAlphabets = listOf(
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
        randomChars.addAll(List(size) {listOfAlphabets.random()})
        val alphabetsToReplace: StringBuilder = StringBuilder()
        for (i in randomChars) {
            alphabetsToReplace.append(i)
        }
        alphabets.value = alphabetsToReplace.toString()
        return alphabetsToReplace.toString()
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
            key.value = latestItem.substring(0,keySize - 1)
            alphabets.value = latestItem.substring(keySize, latestItem.length - 1)
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
                key.value = fullKey.substring(0,keySize - 1)
            }
            alphabets.value = keySize?.let { encryptionKey.value?.substring(it, fullKey.length - 1) }
            encodeBySwap(text, key.value!!, alphabets.value!!)
        }
    }

    fun copyKey(context: Context?) {
        val keyText = key.value + alphabets.value
        val clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("keys", keyText)
        clipboardManager.setPrimaryClip(clipData)
    }

    suspend fun updateKeyList(dataStoreManager: DataStoreManager) {
        val newKey = key.value + alphabets.value
        val keyList = dataStoreManager.readEncryptData()
        keyList?.addToList(newKey)
        if (keyList == null) {
            val newEncryptionKeys = EncryptionKeys()
            newEncryptionKeys.addToList(newKey)
            dataStoreManager.writeEncryptData(newEncryptionKeys)
        }
        dataStoreManager.writeEncryptData(keyList!!)
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