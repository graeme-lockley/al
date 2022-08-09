package al.bootstrap.scanner

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ScannerTest : FunSpec({
    test("empty string without comments") {
        val tokens = scanInput("")

        1 shouldBe tokens.size
        tokens[0] shouldBe mkToken("", TToken.EOS, PointLocation(0, 0, 0))
    }

    test("empty string with whitespace") {
        val tokens = scanInput("    ")

        1 shouldBe tokens.size
        mkToken("", TToken.EOS, PointLocation(4, 0, 4)) shouldBe tokens[0]
    }

    test("give an Identifier") {
        val tokens = scanInput("Hello world")

        3 shouldBe tokens.size

        mkToken("Hello", TToken.IDENTIFIER, locationFrom(0, 0, 0, 4, 0, 4)) shouldBe tokens[0]

        mkToken("world", TToken.IDENTIFIER, locationFrom(6, 0, 6, 10, 0, 10)) shouldBe tokens[1]

        mkToken("", TToken.EOS, PointLocation(11, 0, 11)) shouldBe tokens[2]
    }

    test("given an indented identifier") {
        val tokens = scanInput("  Hello\n    world")

        7 shouldBe tokens.size

        mkToken("  ", TToken.OPEN_BLOCK, locationFrom(0, 0, 0, 1, 0, 1)) shouldBe tokens[0]

        mkToken("Hello", TToken.IDENTIFIER, locationFrom(2, 0, 2, 6, 0, 6)) shouldBe tokens[1]

        mkToken("    ", TToken.OPEN_BLOCK, locationFrom(8, 1, 0, 11, 1, 3)) shouldBe tokens[2]

        mkToken("world", TToken.IDENTIFIER, locationFrom(12, 1, 4, 16, 1, 8)) shouldBe tokens[3]

        mkToken("", TToken.CLOSE_BLOCK, PointLocation(17, 1, 9)) shouldBe tokens[4]

        mkToken("", TToken.CLOSE_BLOCK, PointLocation(17, 1, 9)) shouldBe tokens[5]

        mkToken("", TToken.EOS, PointLocation(17, 1, 9)) shouldBe tokens[6]
    }

    test("given an unindented identifier") {
        val tokens = scanInput("  Hello\n    world\n    moon\n  fred\nsam\n")

        listOf(
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
        ) shouldBe tokens.map { Pair(it.lexeme, it.tToken) }
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
