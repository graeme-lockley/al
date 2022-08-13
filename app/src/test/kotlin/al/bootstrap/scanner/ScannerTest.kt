package al.bootstrap.scanner

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ScannerTest : FunSpec({
    test("empty string without comments") {
        val tokens = scanInput("")

        tokens.size shouldBe 1
        mkToken("", TToken.EOS, PointLocation(0, 0, 0)) shouldBe tokens[0]
    }

    test("empty string with whitespace") {
        val tokens = scanInput("    ")

        tokens.size shouldBe 1
        mkToken("", TToken.EOS, PointLocation(4, 0, 4)) shouldBe tokens[0]
    }

    test("give an Identifier") {
        val tokens = scanInput("Hello world")

        tokens.size shouldBe 3

        mkToken("Hello", TToken.IDENTIFIER, locationFrom(0, 0, 0, 4, 0, 4))

        tokens[1] shouldBe mkToken("world", TToken.IDENTIFIER, locationFrom(6, 0, 6, 10, 0, 10))

        tokens[2] shouldBe mkToken("", TToken.EOS, PointLocation(11, 0, 11))
    }

    test("given an indented identifier") {
        val tokens = scanInput("  Hello\n    world")

        tokens.size shouldBe 7

        tokens[0] shouldBe mkToken("  ", TToken.OPEN_BLOCK, locationFrom(0, 0, 0, 1, 0, 1))

        tokens[1] shouldBe mkToken("Hello", TToken.IDENTIFIER, locationFrom(2, 0, 2, 6, 0, 6))

        tokens[2] shouldBe mkToken("    ", TToken.OPEN_BLOCK, locationFrom(8, 1, 0, 11, 1, 3))

        tokens[3] shouldBe mkToken("world", TToken.IDENTIFIER, locationFrom(12, 1, 4, 16, 1, 8))

        tokens[4] shouldBe mkToken("", TToken.CLOSE_BLOCK, PointLocation(17, 1, 9))

        tokens[5] shouldBe mkToken("", TToken.CLOSE_BLOCK, PointLocation(17, 1, 9))

        tokens[6] shouldBe mkToken("", TToken.EOS, PointLocation(17, 1, 9))
    }

    test("given an unindented identifier") {
        val tokens = scanInput("  Hello\n    world\n    moon\n  fred\nsam\n")

        tokens.map { Pair(it.lexeme, it.tToken) } shouldBe listOf(
            Pair("  ", TToken.OPEN_BLOCK),
            Pair("Hello", TToken.IDENTIFIER),
            Pair("    ", TToken.OPEN_BLOCK),
            Pair("world", TToken.IDENTIFIER),
            Pair("", TToken.SEPARATOR),
            Pair("moon", TToken.IDENTIFIER),
            Pair("", TToken.CLOSE_BLOCK),
            Pair("fred", TToken.IDENTIFIER),
            Pair("", TToken.CLOSE_BLOCK),
            Pair("sam", TToken.IDENTIFIER),
            Pair("", TToken.EOS),
        )
    }

    test("given two successive unit value on separate lines") {
        val tokens = scanInput("()\n()\n")

        tokens.map { Pair(it.lexeme, it.tToken) } shouldBe listOf(
            Pair("", TToken.LPAREN_RPAREN),
            Pair("", TToken.SEPARATOR),
            Pair("", TToken.LPAREN_RPAREN),
            Pair("", TToken.EOS),
        )
    }

    test("given literal characters") {
        val tokens = scanInput("'a' 'b' '\\n'")

        tokens.map { Pair(it.lexeme, it.tToken) } shouldBe listOf(
            Pair("'a'", TToken.LITERAL_CHAR),
            Pair("'b'", TToken.LITERAL_CHAR),
            Pair("'\\n'", TToken.LITERAL_CHAR),
            Pair("", TToken.EOS),
        )
    }
})

private fun scanInput(input: String): List<Token> {
    val scanner = scan(input)
    val tokens = ArrayList<Token>()

    do {
        tokens.add(scanner.current)
    } while (scanner.skip())

    return tokens
}

private fun mkToken(lexeme: String, tToken: TToken, location: Location): Token =
    Token(lexeme, tToken, location, emptyList())
