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

data class Unit(override val location: Location) : Expression(location) {
    override fun yaml(): Any =
        "LiteralUnit"
}

data class Parenthesis(val expressions: List<Expression>, override val location: Location) : Expression(location) {
    override fun yaml(): Any =
        singletonMap("Parenthesis", expressions.map { it.yaml() })
}