package al.bootstrap.parser

import al.bootstrap.Errors
import al.bootstrap.InternalError
import al.bootstrap.ParseError
import al.bootstrap.data.*
import al.bootstrap.parser.ast.*
import al.bootstrap.scanner.Scanner
import al.bootstrap.scanner.Token

fun parse(scanner: Scanner): Either<Errors, Program> =
    try {
        Right(Parser(scanner, ParseVisitor()).program())
    } catch (e: ParsingException) {
        Left(ParseError(e.found, e.expected))
    } catch (e: InternalException) {
        Left(InternalError(e.token, e.reason))
    }

class InternalException(
    val token: Token,
    val reason: String
) : Exception()

class ParseVisitor : Visitor<Program, List<Expression>, Expression, Expression, Expression, Expression> {
    override fun visitProgram(a: List<Expression>): Program =
        Program(a)

    override fun visitExpressions(a1: Expression, a2: List<Tuple2<Token, Expression>>): List<Expression> =
        listOf(a1) + a2.map { it.b }

    override fun visitExpression10(a: Expression): Expression = a

    override fun visitInvocationExpression(a1: Expression, a2: List<Expression>): Expression =
        if (a2.isEmpty())
            a1
        else
            Invocation(listOf(a1) + a2, a1.location + a2.last().location)

    override fun visitFactor1(a: Token): Expression = LiteralUnit(a.location)

    override fun visitFactor2(a1: Token, a2: List<Expression>, a3: Token): Expression =
        Parenthesis(a2, a1.location + a3.location)

    override fun visitFactor3(a1: Token, a2: Tuple2<Expression, List<Tuple2<Token, Expression>>>?, a3: Token): Expression =
        if (a2 == null)
            LiteralList(emptyList(), a1.location + a3.location)
        else
            LiteralList(listOf(a2.a) + a2.b.map { it.b }, a1.location + a3.location)

    override fun visitFactor4(
        a1: Token,
        a2: Tuple4<Token, Token, Expression, List<Tuple4<Token, Token, Token, Expression>>>?,
        a3: Token
    ): Expression =
        if (a2 == null)
            LiteralRecord(emptyList(), a1.location + a3.location)
        else
            LiteralRecord(
                listOf(Tuple2(LiteralRecordKey(a2.a.lexeme, a2.a.location), a2.c)) + a2.d.map {
                    Tuple2(
                        LiteralRecordKey(it.b.lexeme, it.b.location),
                        it.d
                    )
                }, a1.location + a3.location
            )

    override fun visitFactor5(a: Expression): Expression =
        a

    override fun visitFactor6(a: Token): Expression =
        when (a.lexeme.length) {
            3 -> LiteralChar(a.lexeme[1].code.toChar(), a.location)
            4 -> {
                val code =
                    when (val ch = a.lexeme[2]) {
                        'n' -> 10
                        else -> ch.code
                    }

                LiteralChar(code.toChar(), a.location)
            }
            else -> throw InternalException(a, "Literal char lexeme should be of length 3 or 4 characters")
        }

    override fun visitFactor7(a: Token): Expression =
        LiteralI32(a.lexeme, a.location)

    override fun visitFactor8(a: Token): Expression =
        LiteralString(a.lexeme, a.location)

    override fun visitFactor9(a: Token): Expression =
        Identifier(a.lexeme, a.location)

    override fun visitLiteralBool1(a: Token): Expression =
        LiteralBool(true, a.location)

    override fun visitLiteralBool2(a: Token): Expression =
        LiteralBool(false, a.location)
}