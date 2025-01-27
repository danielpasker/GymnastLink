package com.example.gymnastlink.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.gymnastlink.utils.Converters
import com.google.gson.annotations.SerializedName

@Entity
data class ExerciseItem(
    @PrimaryKey @SerializedName("id") val id: String,
    @SerializedName("userId") var userId: String?,
    @SerializedName("name") val name: String,
    @SerializedName("equipment") val equipment: String,
    @SerializedName("target") val target: String,
    @SerializedName("bodyPart") val bodyPart: String,
    @TypeConverters(Converters::class) @SerializedName("secondaryMuscles")
    val secondaryMuscles: List<String>,
    @TypeConverters(Converters::class) @SerializedName("instructions")
    val instructions: List<String>,
    @SerializedName("gifUrl") val gifUrl: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: listOf(),
        parcel.createStringArrayList() ?: listOf(),
        parcel.readString() ?: "",
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(userId)
        parcel.writeString(name)
        parcel.writeString(equipment)
        parcel.writeString(target)
        parcel.writeString(bodyPart)
        parcel.writeStringList(secondaryMuscles)
        parcel.writeStringList(instructions)
        parcel.writeString(gifUrl)
    }

    override fun describeContents(): Int = 0

    companion object {

        const val ID_KEY = "id"
        const val USER_ID_KEY = "userId"
        const val NAME_KEY = "name"
        const val EQUIPMENT_KEY = "equipment"
        const val TARGET_KEY = "target"
        const val BODYPART_KEY = "bodyPart"
        const val SECONDARYMUSCLES_KEY = "secondaryMuscles"
        const val INSTRUCTIONS_KEY = "instructions"
        const val GIFURL_KEY = "gifUrl"

        fun fromJSON(json: Map<String, Any>): ExerciseItem {
            val id = json[ID_KEY] as? String ?: ""
            val userId = json[USER_ID_KEY] as? String ?: ""
            val name = json[NAME_KEY] as? String ?: ""
            val equipment = json[EQUIPMENT_KEY] as? String ?: ""
            val target = json[TARGET_KEY] as? String ?: ""
            val bodyPart = json[BODYPART_KEY] as? String ?: ""
            val secondaryMuscles = json[SECONDARYMUSCLES_KEY] as? List<String> ?: listOf()
            val instructions = json[INSTRUCTIONS_KEY] as? List<String> ?: listOf()
            val gifUrl = json[GIFURL_KEY] as? String ?: ""

            return ExerciseItem(
                id = id,
                userId = userId,
                name = name,
                equipment = equipment,
                target = target,
                bodyPart = bodyPart,
                secondaryMuscles = secondaryMuscles,
                instructions = instructions,
                gifUrl = gifUrl
            )
        }

        @JvmField
        val CREATOR: Parcelable.Creator<ExerciseItem> = object : Parcelable.Creator<ExerciseItem> {
            override fun createFromParcel(parcel: Parcel): ExerciseItem {
                return ExerciseItem(parcel)
            }

            override fun newArray(size: Int): Array<ExerciseItem?> {
                return arrayOfNulls(size)
            }
        }
    }

    val json: Map<String, Any?>
        get() = mapOf(
            ID_KEY to id,
            USER_ID_KEY to userId,
            NAME_KEY to name,
            EQUIPMENT_KEY to equipment,
            TARGET_KEY to target,
            BODYPART_KEY to bodyPart,
            SECONDARYMUSCLES_KEY to secondaryMuscles,
            INSTRUCTIONS_KEY to instructions,
            GIFURL_KEY to gifUrl
        )
}
