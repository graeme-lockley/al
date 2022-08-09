package al.bootstrap.scanner

import kotlin.test.Test
import kotlin.test.assertEquals

class MainKtTest {
    @Test
    fun givenEmptyStringWithoutComments() {
        val tokens = scanInput("")

        assertEquals(1, tokens.size)
        assertEquals("", tokens[0].lexeme)
        assertEquals(TToken.EOS, tokens[0].tToken)
        assertEquals(PointLocation(0, 0, 0), tokens[0].location)
        assertEquals(0, tokens[0].comments.size)
    }

    @Test
    fun givenEmptyStringWithWhitespace() {
        val tokens = scanInput("    ")

        assertEquals(1, tokens.size)
        assertEquals("", tokens[0].lexeme)
        assertEquals(TToken.EOS, tokens[0].tToken)
        assertEquals(PointLocation(4, 0, 4), tokens[0].location)
        assertEquals(0, tokens[0].comments.size)
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