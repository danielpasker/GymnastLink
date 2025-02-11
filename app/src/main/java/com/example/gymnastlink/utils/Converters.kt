package com.example.gymnastlink.utils

import android.util.Base64
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class Converters {

    @TypeConverter
    fun fromLocalDateTime(dateTime: LocalDateTime?): String? {
        return dateTime?.let {
            formatDateTime(it)
        }
    }

    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let {
            LocalDateTime.parse(
                it,
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.getDefault())
            )
        }
    }

    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }

    companion object {
        fun formatDateTime(dateTime: LocalDateTime, pattern: String = "dd/MM/yyyy HH:mm"): String {
            return try {
                val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
                dateTime.format(formatter)
            } catch (e: Exception) {
                println("Error formatting date: ${e.message}")
                ""
            }
        }

        fun encodeImageToBase64(image: ByteArray): String {
            return Base64.encodeToString(image, Base64.DEFAULT)
        }

        fun decodeImageFromBase64(imageString: String): ByteArray {
            return Base64.decode(imageString, Base64.DEFAULT)
        }
    }
}
