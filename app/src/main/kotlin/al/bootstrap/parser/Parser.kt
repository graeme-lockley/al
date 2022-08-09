package al.bootstrap.parser

import al.bootstrap.scanner.Scanner
import al.bootstrap.scanner.TToken
import al.bootstrap.scanner.Token

class Parser<T_Factor>(
    private val scanner: Scanner,
    private val visitor: Visitor<T_Factor>
) {
    fun factor(): T_Factor {
        when (scanner.current.tToken) {
            TToken.LPAREN_RPAREN -> {
                val t1 = nextToken()
                return visitor.visitFactor1(t1)
            }
            else -> {
                throw ParsingException(scanner.current, setOf(TToken.LPAREN_RPAREN))
            }
        }
    }

    private fun nextToken(): Token {
        val token = scanner.current
        scanner.skip()
        return token
    }
}

class ParsingException(
    val found: Token,
    val expected: Set<TToken>
) : Exception()