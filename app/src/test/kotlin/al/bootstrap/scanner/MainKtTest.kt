package al.bootstrap.scanner

import kotlin.test.Test
import kotlin.test.assertEquals

class MainKtTest {
    @Test
    fun givenEmptyStringWithoutComments() {
        val tokens = scanInput("")

        assertEquals(1, tokens.size)
        assertEquals(
            Token(
                "",
                TToken.EOS,
                PointLocation(0, 0, 0),
                emptyList()
            ),
            tokens[0]
        )
    }

    @Test
    fun givenEmptyStringWithWhitespace() {
        val tokens = scanInput("    ")

        assertEquals(1, tokens.size)
        assertEquals(
            Token(
                "",
                TToken.EOS,
                PointLocation(4, 0, 4),
                emptyList()
            ),
            tokens[0]
        )
    }

    @Test
    fun givenASymbol() {
        val tokens = scanInput("Hello world")

        assertEquals(3, tokens.size)
        assertEquals(
            Token(
                "Hello",
                TToken.SYMBOL,
                locationFrom(0, 0, 0, 4, 0, 4),
                emptyList()
            ),
            tokens[0]
        )
        assertEquals(
            Token(
                "world",
                TToken.SYMBOL,
                locationFrom(6, 0, 6, 10, 0, 10),
                emptyList()
            ),
            tokens[1]
        )
        assertEquals(
            Token(
                "",
                TToken.EOS,
                PointLocation(11, 0, 11),
                emptyList()
            ),
            tokens[2]
        )
    }

    @Test
    fun givenAnIndentedSymbol() {
        val tokens = scanInput("  Hello\n    world")

        assertEquals(7, tokens.size)
        assertEquals(
            Token(
                "  ",
                TToken.OPEN_BLOCK,
                locationFrom(0, 0, 0, 1, 0, 1),
                emptyList()
            ),
            tokens[0]
        )
        assertEquals(
            Token(
                "Hello",
                TToken.SYMBOL,
                locationFrom(2, 0, 2, 6, 0, 6),
                emptyList()
            ),
            tokens[1]
        )
        assertEquals(
            Token(
                "    ",
                TToken.OPEN_BLOCK,
                locationFrom(8, 1, 0, 11, 1, 3),
                emptyList()
            ),
            tokens[2]
        )
        assertEquals(
            Token(
                "world",
                TToken.SYMBOL,
                locationFrom(12, 1, 4, 16, 1, 8),
                emptyList()
            ),
            tokens[3]
        )
        assertEquals(
            Token(
                "",
                TToken.CLOSE_BLOCK,
                PointLocation(17, 1, 9),
                emptyList()
            ),
            tokens[4]
        )
        assertEquals(
            Token(
                "",
                TToken.CLOSE_BLOCK,
                PointLocation(17, 1, 9),
                emptyList()
            ),
            tokens[5]
        )
        assertEquals(
            Token(
                "",
                TToken.EOS,
                PointLocation(17, 1, 9),
                emptyList()
            ),
            tokens[6]
        )
    }

    @Test
    fun givenAnUnindentedSymbol() {
        val tokens = scanInput("  Hello\n    world\n    moon\n  fred\nsam\n")

        assertEquals(listOf(
            Pair("  ", TToken.OPEN_BLOCK),
            Pair("Hello", TToken.SYMBOL),
            Pair("    ", TToken.OPEN_BLOCK),
            Pair("world", TToken.SYMBOL),
            Pair("moon", TToken.SYMBOL),
            Pair("", TToken.CLOSE_BLOCK),
            Pair("fred", TToken.SYMBOL),
            Pair("", TToken.CLOSE_BLOCK),
            Pair("sam", TToken.SYMBOL),
            Pair("", TToken.EOS),
        ), tokens.map { Pair(it.lexeme, it.tToken) })
    }
}

fun scanInput(input: String): List<Token> {
    val scanner = scan(input)
    val tokens = ArrayList<Token>()

    do {
        tokens.add(scanner.current)
    } while (scanner.skip())

    return tokens
}