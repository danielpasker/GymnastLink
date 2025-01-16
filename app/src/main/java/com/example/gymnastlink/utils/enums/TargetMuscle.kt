package com.example.gymnastlink.utils.enums

enum class TargetMuscle(val muscle: String) {

    UNKNOWN("Unknown"),
    ABDUCTORS("Aabductors"),
    ABS("Abs"),
    ADDUCTORS("Adductors"),
    BICEPS("Biceps"),
    CALVES("Calves"),
    CARDIOVASCULAR_SYSTEM("Cardiovascular System"),
    DELTS("Delts"),
    FOREARMS("Forearms"),
    GLUTES("glutes"),
    HAMSTRINGS("Hamstrings"),
    LATS("Lats"),
    LEVATOR_SCAPULAE("Levator Scapulae"),
    PECTORALS("Pectorals"),
    QUADS("Quads"),
    SERRATUS_ANTERIOR("Serratus Anterior"),
    SPINE("Spine"),
    TRAPS("Traps"),
    TRICEPS("Triceps"),
    UPPER_BACK("Upper Back");

    override fun toString(): String {
        return "$muscle"
    }
}