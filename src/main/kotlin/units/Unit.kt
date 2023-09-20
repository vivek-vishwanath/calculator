package units

import IncompatibleUnitsException

interface Unit {

    val symbol: String

    fun root(): Unit? = this

    fun isCompatible(other: Unit) = root()?.javaClass == other.root()?.javaClass
}

class ValuedUnit(val value: Double, val unit: Unit) {

    fun convert(unit: Unit): Double {
        return if(this.unit.isCompatible(unit))
            when (unit) {
                is ScalableUnit -> convert(unit)
                is CustomUnit -> convert(unit)
                else -> throw RuntimeException("Impossible")
            }
        else throw IncompatibleUnitsException("Incompatible units to convert: ${this.unit} --> $unit")
    }

    private fun convert(unit: ScalableUnit) = value.convert(this.unit as ScalableUnit, unit)

    private fun convert(unit: CustomUnit) = value.convert(this.unit as CustomUnit, unit)

    operator fun unaryMinus() = ValuedUnit(-value, unit)

    private fun inverse() = ValuedUnit(1 / value, (unit as ScalableUnit).inverse())

    operator fun plus(valuedUnit: ValuedUnit): ValuedUnit {
        if(unit == valuedUnit.unit) return ValuedUnit(value + valuedUnit.value, unit)
        else throw IncompatibleUnitsException("Incompatible Units: $unit vs. ${valuedUnit.unit}")
    }

    operator fun minus(valuedUnit: ValuedUnit) = this + -valuedUnit

    operator fun times(valuedUnit: ValuedUnit): ValuedUnit {
        val newValue = value * valuedUnit.value
        val unit = this.unit as ScalableUnit
        val other = valuedUnit.unit as ScalableUnit
        return if (unit is CompositeUnit)
            if (other is CompositeUnit)
                ValuedUnit(newValue, CompositeUnit.new((unit.units + other.units) as HashMap))
            else
                ValuedUnit(newValue, CompositeUnit.new((unit.units + hashMapOf(other to 1)) as HashMap))
        else
            if (other is CompositeUnit)
                ValuedUnit(newValue, CompositeUnit.new((hashMapOf(unit to 1) + other.units) as HashMap))
            else
                ValuedUnit(newValue, CompositeUnit.new(unit, other))
    }

    operator fun div(valuedUnit: ValuedUnit) = this * valuedUnit.inverse()

    override fun toString() = "$value $unit"

}