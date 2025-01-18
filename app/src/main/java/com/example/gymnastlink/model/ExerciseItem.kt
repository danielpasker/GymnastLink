package com.example.gymnastlink.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class ExerciseItem(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("equipment") val equipment: String,
    @SerializedName("target") val target: String,
    @SerializedName("bodyPart") val bodyPart: String,
    @SerializedName("secondaryMuscles") val secondaryMuscles: Array<String>,
    @SerializedName("instructions") val instructions: Array<String>,
    @SerializedName("gifUrl") val gifUrl: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArray() ?: arrayOf(),
        parcel.createStringArray() ?: arrayOf(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(equipment)
        parcel.writeString(target)
        parcel.writeString(bodyPart)
        parcel.writeStringArray(secondaryMuscles)
        parcel.writeStringArray(instructions)
        parcel.writeString(gifUrl)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ExerciseItem> {
        override fun createFromParcel(parcel: Parcel): ExerciseItem {
            return ExerciseItem(parcel)
        }

        override fun newArray(size: Int): Array<ExerciseItem?> {
            return arrayOfNulls(size)
        }
    }
}
