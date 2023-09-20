package expression

import ImproperExpressionException
import UnknownSymbolException
import units.*
import kotlin.math.*

class Node(var token: String) {

    var parent: Node? = null
    private val children = ArrayList<Node>()

    fun addChild(token: String) = addChild(Node(token))

    private fun addChild(node: Node): Node {
        children.add(node)
        node.parent = this
        return node
    }

    fun insertParent(token: String): Node {
        val grandparent = parent
        val node = Node(token)
        node.parent = grandparent
        grandparent?.children?.set(grandparent.children.indexOf(this), node)
        node.addChild(this)
        return node
    }

    fun solve(variables: HashMap<String, Number> = hashMapOf()): Double {
        if (children.size == 0) {
            return token.toDoubleOrNull() ?: when (token) {
                "pi", "π" -> Math.PI
                "e" -> Math.E
                else -> variables[token]?.toDouble() ?: throw UnknownSymbolException("Unrecognized symbol: $token")
            }
        }
        if (children.size == 1) {
            val child = children[0].solve(variables)
            when (token) {
                "-" -> return -child
                "()" -> return child
            }
            if(token.endsWith("()") && singleFunctions[token.substring(0, token.length - 2)] != null)
                return singleFunctions[token.substring(0, token.length - 2)]!!.invoke(child)
        }
        if (children.size == 2) {
            val child1 = children[0].solve(variables)
            val child2 = children[1].solve(variables)
            when (token) {
                "+" -> return child1 + child2
                "-" -> return child1 - child2
                "*" -> return child1 * child2
                "/" -> return child1 / child2
                "%" -> return child1 % child2
                "^" -> return child1.pow(child2)
            }
        }
        if(token.endsWith("()") && multiFunctions[token.substring(0, token.length - 2)] != null)
            return multiFunctions[token.substring(0, token.length - 2)]!!.invoke(Array(children.size) { children[it].solve(variables) })
        throw UnknownSymbolException(token)
    }

    fun solveUnit(): ValuedUnit = when (children.size) {
        0 -> if (token[0].isLetter()) ValuedUnit(1.0, getUnit(token)) else ValuedUnit(token.toDouble(), FreeUnit())
        1 -> when (token) {
            "()" -> children[0].solveUnit()
            "-" -> -children[0].solveUnit()
            else -> throw UnknownSymbolException(
                "Invalid Operator: $token"
            )
        }

        2 -> {
            val c1 = children[0].solveUnit()
            val c2 = children[1].solveUnit()
            when (token) {
                "*" -> c1 * c2
                "/" -> c1 / c2
                "^" -> {
                    val n = c2.value.toInt()
                    val product = Array(abs(n)) {
                        val u = c1.unit as ScalableUnit; if (n > 0) u else if (n == 0) FreeUnit() else u.inverse()
                    }
                    ValuedUnit(c1.value.pow(c2.value), CompositeUnit.new(*product))
                }

                else -> throw UnknownSymbolException("Invalid Operator: $token")
            }
        }

        else -> throw ImproperExpressionException("Too many inputs")
    }

    private fun toString(depth: Int): String {
        val builder = StringBuilder()
        builder.append("\t".repeat(depth)).append(token)
        for (node in children) {
            builder.append("\n").append(node.toString(depth + 1))
        }
        return builder.toString()
    }

    override fun toString() = toString(0)
}

val singleFunctions = hashMapOf<String, (Double) -> Double>(
    "floor" to { floor(it) },
    "ceil" to { ceil(it) },
    "round" to { round(it) },
    "abs" to { abs(it) },
    "sqrt" to { sqrt(it) },
    "cbrt" to { cbrt(it) },
    "log" to { log(it, 10.0) },
    "exp" to { exp(it) },
    "sin" to { sin(it) },
    "cos" to { cos(it) },
    "tan" to { tan(it) },
    "csc" to { 1 / sin(it) },
    "sec" to { 1 / cos(it) },
    "cot" to { 1 / tan(it) },
    "sinh" to { sinh(it) },
    "cosh" to { cosh(it) },
    "tanh" to { tanh(it) },
    "asin" to { asin(it) },
    "acos" to { acos(it) },
    "atan" to { atan(it) },
    "arcsin" to { asin(it) },
    "arccos" to { acos(it) },
    "arctan" to { atan(it) },
)

val multiFunctions = hashMapOf<String, (Array<Double>) -> Double>(
    "mean" to { it.sum() / it.size },
    "average" to { it.sum() / it.size },
    "min" to { it.min() },
    "max" to { it.max() },
    "sum" to { it.sum() },
    "product" to { var product = 1.0; it.forEach { d -> product *= d }; product}
)