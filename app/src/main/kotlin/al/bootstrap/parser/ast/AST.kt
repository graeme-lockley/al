package al.bootstrap.parser.ast

import al.bootstrap.scanner.Location

sealed class Expression(open val location: Location)

data class Unit(override val location: Location) : Expression(location)