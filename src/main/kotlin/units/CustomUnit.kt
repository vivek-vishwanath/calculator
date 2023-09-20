package units

interface CustomUnit : Unit {

    val from: (Number) -> Number
    val to: (Number) -> Number

    fun convert(from: Number, to: CustomUnit) = to.to(this.from(from.toDouble())).toDouble()

}

enum class Temperature(
    override val symbol: String,
    override val from: (Number) -> Number,
    override val to: (Number) -> Number
) : CustomUnit {

    KELVIN("K", { it }, { it }), CELSIUS("C", { it.toDouble() + 273.15 }, { it.toDouble() - 273.15 }), FAHRENHEIT(
        "F",
        { (it.toDouble() - 32) * 5 / 9 + 273.15 },
        { (it.toDouble() - 273.15) * 9 / 5 + 32 });

    override fun toString() = symbol
}