package com.sourcepointmeta.metaapp.util

import android.content.Context
import android.net.Uri
import com.sourcepointmeta.metaapp.core.getOrNull
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

fun Context.readFileContent(uri: Uri): String? {
    var inputStream: InputStream? = null
    try {
        inputStream = contentResolver.openInputStream(uri)
        if (inputStream != null) {
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String? = reader.readLine()
            while (line != null) {
                stringBuilder.append(line).append('\n')
                line = reader.readLine()
            }
            return stringBuilder.toString()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return null
}

fun String.string2Json(): JSONObject? = check { JSONObject(this) }.getOrNull()
