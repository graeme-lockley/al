package al.bootstrap.parser.ast

import al.bootstrap.data.Tuple2
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

data class LiteralI32(val value: String, override val location: Location) : Expression(location) {
    override fun yaml(): Any =
        singletonMap("LiteralI32", value)
}

data class LiteralList(val elements: List<Expression>, override val location: Location) : Expression(location) {
    override fun yaml(): Any =
        singletonMap("LiteralList", elements.map { it.yaml() })
}

data class LiteralRecord(val elements: List<Tuple2<LiteralRecordKey, Expression>>, override val location: Location) : Expression(location) {
    override fun yaml(): Any =
        singletonMap("LiteralRecord", elements.map {
            mapOf(
                Pair("Key", it.a.yaml()),
                Pair("Value", it.b.yaml())
            )
        })
}

data class LiteralRecordKey(val key: String, val location: Location) : Yamlable {
    override fun yaml(): Any =
        key
}

data class LiteralString(val value: String, override val location: Location) : Expression(location) {
    override fun yaml(): Any =
        singletonMap("LiteralString", value)
}

data class Identifier(val id: String, override val location: Location) : Expression(location) {
    override fun yaml(): Any =
        singletonMap("Identifier", id)
}

data class LiteralUnit(override val location: Location) : Expression(location) {
    override fun yaml(): Any =
        "LiteralUnit"
}

data class Parenthesis(val expressions: List<Expression>, override val location: Location) : Expression(location) {
    override fun yaml(): Any =
        singletonMap("Parenthesis", expressions.map { it.yaml() })
}