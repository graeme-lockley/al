package al.bootstrap.parser

import al.bootstrap.scanner.Token

interface Visitor<T_Factor> {
    fun visitFactor1(a: Token): T_Factor
}