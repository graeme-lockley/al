package al.bootstrap.parser

import al.bootstrap.parser.ast.Expression
import al.bootstrap.parser.ast.Unit
import al.bootstrap.scanner.Scanner
import al.bootstrap.scanner.Token

fun parse(scanner: Scanner): Expression = Parser(scanner, ParseVisitor()).factor()

class ParseVisitor : Visitor<Expression> {
    override fun visitFactor1(a: Token): Expression = Unit(a.location)
}