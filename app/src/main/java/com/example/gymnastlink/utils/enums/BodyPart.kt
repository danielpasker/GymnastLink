package com.example.gymnastlink.utils.enums

enum class BodyPart(val bodyPart: String) {
    UNKNOWN("Unknown"),
    BACK("Back"),
    CARDIO("Cardio"),
    CHEST("Chest"),
    LOWER_ARMS("Lower Arms"),
    LOWER_LEGS("Lower Legs"),
    NECK("Neck"),
    SHOULDERS("Shoulders"),
    UPPER_ARMS("Upper Arms"),
    UPPER_LEGS("Upper Legs"),
    WAIST("waist");

    override fun toString(): String {
        return "$bodyPart"
    }
}