package com.example.gymnastlink.utils

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.let {
            formatDate(it)
        }
    }

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let {
            LocalDate.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault()))
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
        fun formatDate(date: LocalDate, pattern: String = "dd/MM/yyyy"): String {
            return try {
                val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
                date.format(formatter)
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
