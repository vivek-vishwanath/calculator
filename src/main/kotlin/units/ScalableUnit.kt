package units

interface ScalableUnit : Unit {

    val scale: Number

    fun metric(metric: Metric) = MetricUnit(metric, this)

    fun convert(from: Number, to: ScalableUnit) = from.toDouble() * scale.toDouble() / to.scale.toDouble()

    fun inverse() = if (this is InverseUnit) this.unit else InverseUnit(this)

}

enum class Distance(override val symbol: String, override val scale: Number) : ScalableUnit {

    INCH("in", 1), FOOT("ft", 12), YARD("36", 36), MILE("mi", 12 * 5280), METER("m", 39.37007874);

    override fun toString() = symbol
}

enum class Time(override val symbol: String, override val scale: Number) : ScalableUnit {

    SECOND("s", 1), MINUTE("min", 60), HOUR("hr", 3600), DAY("d", 86400), WEEK("w", 86400 * 7), YEAR(
        "yr",
        86400 * 365.25
    );

    override fun toString() = symbol
}

enum class Mass(override val symbol: String, override val scale: Number) : ScalableUnit {

    GRAM("g", 1), OUNCE("oz", 28.35), POUND("lb", 453.5924), US_TON("tn", 907200), METRIC_TON(
        "t",
        1000000
    ),
    IMPERIAL_TON("lt", 1016047), STONE("st", 6350);

    override fun toString() = symbol

}

enum class Volume(override val symbol: String, override val scale: Number) : ScalableUnit {

    LITER("L", 768 / 3.785), GALLON("gal", 768), QUART("qt", 192), PINT("pt", 96), CUP("c", 48), FL_OZ(
        "fl_oz",
        6
    ),
    TBSP("tbsp", 3), TSP("tsp", 1), CUBIC_METER("m^3", 768000 / 3.785);

    override fun toString() = symbol
}