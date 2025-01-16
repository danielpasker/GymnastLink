package com.example.gymnastlink.utils.enums

enum class Equipment(val equipment: String) {
    UNKNOWN("Unknown"),
    ASSISTED("Assisted"),
    BAND("Band"),
    BARBELL("Barbell"),
    BODY_WEIGHT("Body Weight"),
    BOSU_BALL("Bosu Ball"),
    CABLE("Cable"),
    DUMBBELL("Dumbbell"),
    ELLIPTICAL_MACHINE("Elliptical Machine"),
    EZ_BARBELL("EZ Barbell"),
    HAMMER("Hammer"),
    KETTLEBELL("Kettlebell"),
    LEVERAGE_MACHINE("Leverage Machine"),
    MEDICINE_BALL("Medicine Ball"),
    OLYMPIC_BARBELL("Olympic Barbell"),
    RESISTANCE_BAND("Resistance Band"),
    ROLLER("Roller"),
    ROPE("Rope"),
    SKIERG_MACHINE("Skierg Machine"),
    SLED_MACHINE("Sled Machine"),
    SMITH_MACHINE("Smith Machine"),
    STABILITY_BALL("Stability Ball"),
    STATIONARY_BIKE("Stationary Bike"),
    STEPMILL_MACHINE("Stepmill Machine"),
    TIRE("Tire"),
    TRAP_BAR("Trap Bar"),
    UPPER_BODY_ERGOMETER("Upper Body Ergometer"),
    WEIGHTED("Weighted"),
    WHEEL_ROLLER("Wheel Roller");

    override fun toString(): String {
        return "$equipment"
    }
}