package com.example.gymnastlink.utils

import androidx.room.TypeConverter
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.util.Base64

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

        fun formatNumber(number: Number, locale: Locale = Locale.getDefault()): String {
            val numberFormat = NumberFormat.getNumberInstance(locale)

            return numberFormat.format(number)
        }

        fun encodeImageToBase64(image: ByteArray): String {
            return Base64.encodeToString(image, Base64.DEFAULT)
        }

        fun decodeImageFromBase64(imageString: String): ByteArray {
            return Base64.decode(imageString, Base64.DEFAULT)
        }
    }
}
