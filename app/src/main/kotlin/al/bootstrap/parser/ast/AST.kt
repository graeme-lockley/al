package al.bootstrap.parser.ast

import al.bootstrap.data.Yamlable
import al.bootstrap.scanner.Location

sealed class Expression(open val location: Location) : Yamlable

data class Unit(override val location: Location) : Expression(location) {
    override fun yaml(): Any =
        "LiteralUnit"
}