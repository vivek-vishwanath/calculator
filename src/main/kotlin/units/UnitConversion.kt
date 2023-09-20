package units

import UnknownSymbolException
import expression.parse
import expression.tokenize

fun String.calculateUnits() = parse(tokenize(this)).solveUnit()

fun <T : CustomUnit> Int.convert(from: T, to: T): Double = from.convert(this, to)

fun <T : CustomUnit> Double.convert(from: T, to: T): Double = from.convert(this, to)

fun <T : ScalableUnit> Int.convert(from: T, to: T): Double = from.convert(this, to)

fun <T : ScalableUnit> Double.convert(from: T, to: T): Double = from.convert(this, to)

fun getUnit(symbol: String): Unit {
    if (symbols[symbol] != null) return symbols[symbol]!!
    if (symbol.length > 1) {
        for (prefix in Metric.entries) {
            if (symbol.startsWith(prefix.symbol))
                return symbols[symbol.substring(prefix.symbol.length)]?.let { MetricUnit(prefix, it as ScalableUnit) } ?:continue
        }
    }
    throw UnknownSymbolException("$symbol not found")
}

val symbols: HashMap<String, out Unit> = hashMapOf(
    // Distance
    "m" to Distance.METER,
    "mi" to Distance.MILE,
    "y" to Distance.YARD,
    "ft" to Distance.FOOT,
    "in" to Distance.INCH,
    // Time
    "s" to Time.SECOND,
    "sec" to Time.SECOND,
    "min" to Time.MINUTE,
    "hr" to Time.HOUR,
    "d" to Time.DAY,
    "w" to Time.WEEK,
    "yr" to Time.YEAR,
    // Mass
    "g" to Mass.GRAM,
    "oz" to Mass.OUNCE,
    "lb" to Mass.POUND,
    "tn" to Mass.US_TON,
    "t" to Mass.METRIC_TON,
    "lt" to Mass.IMPERIAL_TON,
    "st" to Mass.STONE,
    // Volume
    "L" to Volume.LITER,
    "gal" to Volume.GALLON,
    "qt" to Volume.QUART,
    "pt" to Volume.PINT,
    "c" to Volume.CUP,
    "fl_oz" to Volume.FL_OZ,
    "tbsp" to Volume.TBSP,
    "tsp" to Volume.TSP,
    // Temperature
    "K" to Temperature.KELVIN,
    "C" to Temperature.CELSIUS,
    "F" to Temperature.FAHRENHEIT
)