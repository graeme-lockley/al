package al.bootstrap.parser.ast

import al.bootstrap.data.Yamlable
import al.bootstrap.scanner.Location

data class Program(
    val expressions: List<Expression>
) : Yamlable {
    override fun yaml(): Any =
        expressions.map { it.yaml() }
}

sealed class Expression(open val location: Location) : Yamlable

data class LiteralBool(val value: Boolean, override val location: Location) : Expression(location) {
    override fun yaml(): Any =
        singletonMap("LiteralBool", value)
}

data class LiteralChar(val value: Char, override val location: Location) : Expression(location) {
    override fun yaml(): Any =
        singletonMap("LiteralChar", value.code)
}

data class LiteralUnit(override val location: Location) : Expression(location) {
    override fun yaml(): Any =
        "LiteralUnit"
}

data class LiteralList(val elements: List<Expression>, override val location: Location) : Expression(location) {
    override fun yaml(): Any =
        singletonMap("LiteralList", elements.map { it.yaml() })
}

data class Parenthesis(val expressions: List<Expression>, override val location: Location) : Expression(location) {
    override fun yaml(): Any =
        singletonMap("Parenthesis", expressions.map { it.yaml() })
}