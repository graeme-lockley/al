package al.bootstrap.parser

import al.bootstrap.Errors
import al.bootstrap.ParseError
import al.bootstrap.data.Either
import al.bootstrap.data.Left
import al.bootstrap.data.Right
import al.bootstrap.data.Tuple2
import al.bootstrap.parser.ast.Expression
import al.bootstrap.parser.ast.Parenthesis
import al.bootstrap.parser.ast.Program
import al.bootstrap.parser.ast.Unit
import al.bootstrap.scanner.Scanner
import al.bootstrap.scanner.Token

//fun parse(scanner: Scanner): Program = Parser(scanner, ParseVisitor()).program()

fun parse(scanner: Scanner): Either<Errors, Program> =
    try {
        Right(Parser(scanner, ParseVisitor()).program())
    } catch (e: ParsingException) {
        Left(ParseError(e.found, e.expected))
    }

class ParseVisitor : Visitor<Program, List<Expression>, Expression, Expression> {
    override fun visitProgram(a: List<Expression>): Program =
        Program(a)

    override fun visitExpressions(a1: Expression, a2: List<Tuple2<Token, Expression>>): List<Expression> =
        listOf(a1) + a2.map { it.b }

    override fun visitExpression10(a: Expression): Expression = a

    override fun visitFactor1(a: Token): Expression = Unit(a.location)
    override fun visitFactor2(a1: Token, a2: List<Expression>, a3: Token): Expression =
        Parenthesis(a2, a1.location + a3.location)
}