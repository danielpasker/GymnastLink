import android.os.Parcel
import android.os.Parcelable
import com.example.gymnastlink.utils.enums.TargetMuscle
import com.example.gymnastlink.utils.enums.BodyPart
import com.example.gymnastlink.utils.enums.Equipment
import com.google.gson.annotations.SerializedName


data class ExerciseItem(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("equipment") val equipment: Equipment,
    @SerializedName("target") val target: TargetMuscle,
    @SerializedName("bodyPart") val bodyPart: BodyPart,
    @SerializedName("secondaryMuscles") val secondaryMuscles: Array<String>,
    @SerializedName("instructions") val instructions: Array<String>,
    @SerializedName("gifUrl") val gifUrl: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        Equipment.valueOf(parcel.readString() ?: Equipment.UNKNOWN.name),
        TargetMuscle.valueOf(parcel.readString() ?: TargetMuscle.UNKNOWN.name),
        BodyPart.valueOf(parcel.readString() ?: BodyPart.UNKNOWN.name),
        parcel.createStringArray() ?: arrayOf(),
        parcel.createStringArray() ?: arrayOf(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(equipment.name)
        parcel.writeString(target.name)
        parcel.writeString(bodyPart.name)
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
