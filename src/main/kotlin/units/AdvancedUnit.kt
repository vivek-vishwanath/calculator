package units

import kotlin.math.abs
import kotlin.math.pow

enum class Metric(override val symbol: String, override val scale: Number) : ScalableUnit {

    BASE("", 1), KILO("K", 1000), MEGA("M", 1000000), GIGA("G", 1000000000), TERA("T", 1000000000000), PETA(
        "P",
        1000000000000000
    ),
    EXA("E", 1000000000000000000), CENTI("c", 0.01), MILLI("m", 0.001), MICRO("μ", 0.000001), NANO("n", 0.000000001),
    PICO("p", 0.000000000001), FEMTO("f", 0.000000000000001);

    override fun toString() = symbol
}

class MetricUnit(private val metricUnit: Metric, private val unit: ScalableUnit) : ScalableUnit {
    override val scale: Number
        get() = unit.scale.toDouble() * metricUnit.scale.toDouble()
    override val symbol: String
        get() = "$metricUnit${unit.symbol}"

    override fun root() = unit

    override fun toString() = symbol
}

class InverseUnit(val unit: ScalableUnit) : ScalableUnit {

    override val scale: Number
        get() = 1 / unit.scale.toDouble()
    override val symbol: String
        get() = unit.symbol

    override fun toString() = "$symbol^-1"

    override fun root() = unit

    companion object
}

class CompositeUnit(val units: HashMap<ScalableUnit, Int>) : ScalableUnit {

    override val scale: Number
        get() {
            var s = 1.0
            for (unit in units) s *= unit.key.scale.toDouble().pow(unit.value)
            return s
        }
    override val symbol: String
        get() = toString()

    override fun inverse(): ScalableUnit {
        val array = units.toArray()
        val inverse = Array(array.size) {Pair(array[it].first, -array[it].second)}
        return new(inverse.toHashMap()) as ScalableUnit
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for ((unit, n) in units) {
            if (unit is FreeUnit || n == 0) continue
            if (n < 0) builder.append(" / ")
            else builder.append(" * ")
            builder.append(unit.symbol)
            if (abs(n) != 1) builder.append("^${abs(n)}")
        }
        return builder.substring(3)
    }

    override fun root() = null

    override fun isCompatible(other: Unit): Boolean {
        if (other !is CompositeUnit) return false
        if (units.size != other.units.size) return false
        loop@for (i in units) {
            for (j in units) {
                if(i.key.root() == j.key.root() && i.value == j.value)
                    continue@loop
            }
            return false
        }
        return true
    }

    companion object {

        fun new(vararg units: ScalableUnit): Unit {
            val map = HashMap<ScalableUnit, Int>()
            for(unit in units) {
                if(unit is FreeUnit) continue
                if(unit is InverseUnit) map.subtract(unit.inverse())
                else map.add(unit)
            }
            val array = map.toArray()
            return when(map.size) {
                0 -> FreeUnit()
                1 -> if (array[0].second == 1) array[0].first else CompositeUnit(map)
                else -> CompositeUnit(map)
            }
        }

        fun new(units: HashMap<ScalableUnit, Int>): Unit {
            val map = HashMap<ScalableUnit, Int>()
            for(unit in units) {
                if(unit.key is FreeUnit) continue
                if(unit.key is InverseUnit) map.subtract(unit.key.inverse(), unit.value)
                else map.add(unit.key, unit.value)
            }
            val array = map.toArray()
            return when(map.size) {
                0 -> FreeUnit()
                1 -> if (array[0].second == 1) array[0].first else CompositeUnit(map)
                else -> CompositeUnit(map)
            }

        }

    }
}

class FreeUnit : ScalableUnit {

    override val scale: Number = 1

    override val symbol: String
        get() = "u"

    override fun toString() = symbol
}

fun <T> HashMap<T, Int>.add(t: T, n: Int = 1) {
    if(this.containsKey(t)) this[t] = this[t]!! + n
    else this[t] = n
}

fun <T> HashMap<T, Int>.subtract(t: T, n: Int = 1) = add(t, -n)

fun <K, V> Array<Pair<K, V>>.toHashMap(): HashMap<K, V> {
    val map = HashMap<K, V>()
    for(e in this) {
        map[e.first] = e.second
    }
    return map
}

fun <K, V> HashMap<K, V>.toArray(): Array<Pair<K, V>> {
    val list = toList()
    return Array(size) {list[it]}
}