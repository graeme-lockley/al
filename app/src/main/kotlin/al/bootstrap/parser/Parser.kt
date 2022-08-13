package al.bootstrap.parser

import al.bootstrap.data.Tuple2
import al.bootstrap.scanner.Scanner
import al.bootstrap.scanner.TToken
import al.bootstrap.scanner.Token

class Parser<T_Program, T_Expressions, T_Expression, T_Factor, T_LiteralBool>(
    private val scanner: Scanner,
    private val visitor: Visitor<T_Program, T_Expressions, T_Expression, T_Factor, T_LiteralBool>
) {
    fun program(): T_Program {
        val a1 = expressions()
        matchToken(TToken.EOS)

        return visitor.visitProgram(a1)
    }

    private fun expressions(): T_Expressions {
        val a1 = expression()
        val a2 = mutableListOf<Tuple2<Token, T_Expression>>()
        while (isToken(TToken.SEPARATOR)) {
            val a21 = nextToken()
            val a22 = expression()

            a2.add(Tuple2(a21, a22))
        }

        return visitor.visitExpressions(a1, a2)
    }

    private fun expression(): T_Expression {
        return visitor.visitExpression10(factor())
    }

    private fun factor(): T_Factor {
        when {
            isToken(TToken.LPAREN_RPAREN) -> {
                val a1 = nextToken()
                return visitor.visitFactor1(a1)
            }
            isToken(TToken.LPAREN) -> {
                val a1 = nextToken()
                val a2 = expressions()
                val a3 = matchToken(TToken.RPAREN)

                return visitor.visitFactor2(a1, a2, a3)
            }
            isToken(TToken.LBRACKET) -> {
                val a1 = nextToken()
                val a2 = if (isToken(TToken.RBRACKET)) {
                    null
                } else {
                    val a21 = expression()
                    val a22 = mutableListOf<Tuple2<Token, T_Expression>>()

                    while (isToken(TToken.COMMA)) {
                        val a221 = nextToken()
                        val a222 = expression()

                        a22.add(Tuple2(a221, a222))
                    }

                    Tuple2(a21, a22 as List<Tuple2<Token, T_Expression>>)
                }
                val a3 = matchToken(TToken.RBRACKET)

                return visitor.visitFactor3(a1, a2, a3)
            }
            isInTokenSet(firstOfLiteralBool) -> {
                val a = literalBool()

                return visitor.visitFactor5(a)
            }
            isToken(TToken.LITERAL_CHAR) -> {
                val a = nextToken()

                return visitor.visitFactor6(a)
            }
            isToken(TToken.LITERAL_INT) -> {
                val a = nextToken()

                return visitor.visitFactor7(a)
            }
            isToken(TToken.LITERAL_STRING) -> {
                val a = nextToken()

                return visitor.visitFactor8(a)
            }
            isToken(TToken.IDENTIFIER) -> {
                val a = nextToken()

                return visitor.visitFactor9(a)
            }
            else -> {
                throw ParsingException(scanner.current, setOf(TToken.LPAREN, TToken.LPAREN_RPAREN))
            }
        }
    }

    private fun literalBool(): T_LiteralBool =
        when (scanner.current.tToken) {
            TToken.TRUE -> {
                val a = nextToken()
                visitor.visitLiteralBool1(a)
            }
            TToken.FALSE -> {
                val a = nextToken()
                visitor.visitLiteralBool2(a)
            }
            else -> {
                throw ParsingException(scanner.current, firstOfLiteralBool)
            }
        }

    private fun nextToken(): Token {
        val token = scanner.current
        scanner.skip()
        return token
    }

    private fun matchToken(tToken: TToken): Token {
        val nextToken = scanner.current

        return when (nextToken.tToken) {
            tToken -> nextToken()
            else -> throw ParsingException(nextToken, setOf(tToken))
        }
    }

    private fun isToken(tToken: TToken): Boolean =
        scanner.current.tToken == tToken

    private fun isInTokenSet(tTokens: Set<TToken>): Boolean =
        tTokens.contains(scanner.current.tToken)

}

val firstOfLiteralBool = setOf(TToken.FALSE, TToken.TRUE)

class ParsingException(
    val found: Token,
    val expected: Set<TToken>
) : Exception()
