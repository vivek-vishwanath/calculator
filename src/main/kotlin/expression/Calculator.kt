package expression

import units.*

fun String.calculate(vararg variables: Pair<String, Double>) = parse(tokenize(this)).solve(hashMapOf(*variables))

fun String.calculateWithUnits() = calculateUnits().toString()

fun Pair<String, String>.convert() = first.calculateUnits().convert(second.calculateUnits().unit)

fun Char.precedence() = when (this) {
    '+', '-' -> 1
    '*', '/', '%' -> 2
    '^' -> 3
    else -> 0
}

fun Char.isOperator() = this in arrayOf('+', '-', '*', '/', '^', '%')

fun Char.isDigit() = this in '0'..'9' || this == '.'

fun Char.isLetter() = this in 'a'..'z' || this in 'A'..'Z' || this == '_'