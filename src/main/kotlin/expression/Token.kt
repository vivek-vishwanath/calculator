package expression

fun tokenize(expression: String): Array<Token> {
    val tokens = ArrayList<Token>()
    var lastToken: Token? = null
    for (c in expression) {
        when {
            c.isLetter() -> when (lastToken) {
                null -> lastToken = LetterToken("$c")
                is NumberToken -> {
                    tokens.add(lastToken)
                    tokens.add(OperatorToken("*"))
                    lastToken = LetterToken("$c")
                }

                is LetterToken -> lastToken = LetterToken(lastToken.value + c)
            }

            c.isDigit() -> lastToken = lastToken?.let { NumberToken(it.value + c) } ?: NumberToken("$c")
            c == ' ' -> {
                if(lastToken != null && lastToken is LetterToken) {
                    tokens.add(lastToken)
                    lastToken = null
                }
            }

            c == '(' || c == ')' -> {
                lastToken?.let { tokens.add(it) }
                tokens.add(if (c == '(') OpenToken() else ClosedToken())
                lastToken = null
            }

            c.isOperator() -> {
                lastToken?.let { tokens.add(it) }
                tokens.add(OperatorToken("$c"))
                lastToken = null
            }
            c == ',' -> {
                if(lastToken != null) {
                    tokens.add(lastToken)
                    lastToken = null
                }
                tokens.add(CommaToken())
            }
        }
    }
    lastToken?.let { tokens.add(it) }
    return tokens.toTypedArray()
}

open class Token(val value: String)
class LetterToken(value: String) : Token(value)
class NumberToken(value: String) : Token(value)
class OperatorToken(value: String) : Token(value)
class OpenToken : Token("(")
class ClosedToken : Token(")")
class CommaToken : Token(",")

fun parse(tokens: Array<Token>): Node {
    val root = Node("(")
    var focus = root
    for (token in tokens) {
        when (token) {
            is LetterToken, is NumberToken -> focus = focus.addChild(token.value)
            is OpenToken -> if (focus.token[0].isLetter()) focus.token += "(" else focus = focus.addChild(token.value)
            is ClosedToken -> {
                while (focus.parent != null && !focus.token.endsWith("(")) focus = focus.parent!!
                if (focus.token.endsWith("(")) focus.token += ")"
            }
            is CommaToken -> while (focus.parent != null && !focus.token.endsWith("(")) focus = focus.parent!!

            is OperatorToken -> {
                if (focus.token.endsWith("(")) focus = focus.addChild(token.value)
                else {
                    while (focus.parent!!.token[0].precedence() >= token.value[0].precedence())
                        focus = focus.parent!!
                    focus = focus.insertParent(token.value)
                }
            }
        }
    }
    while (focus != root) {
        if (focus.token.endsWith("(")) focus.token += ")"
        focus = focus.parent!!
    }
    if (focus.token.endsWith("(")) focus.token += ")"
    return root
}