package al.bootstrap.scanner

abstract class Location

data class PointLocation(val offset: Int, val line: Int, val column: Int) : Location()

data class Comment(var lexeme: String, var location: Location)

enum class TToken {
    ERROR,
    EOS,
    OPEN_BLOCK,
    CLOSE_BLOCK
}

data class Token(var lexeme: String?, var tToken: TToken, var location: Location, var comments: List<Comment>)

fun scan(input: String): Scanner =
    Scanner(input)

class Scanner(private val input: String) {
    private val inputLength = input.length
    private var offset = 0
    private var line = 0
    private var column = 0
    private var token = Token("", TToken.ERROR, PointLocation(0, 0, 0), emptyList())

    init {
        skip()
    }

    val current: Token
        get() {
            return token
        }

    fun skip(): Boolean {
        if (token.tToken == TToken.EOS)
            return false

        while (offset < inputLength && input[offset].isWhitespace()) {
            skipCharacter()
        }

        if (offset == inputLength) {
            token = Token("", TToken.EOS, PointLocation(offset, line, column), emptyList())
        }

        return true
    }

    private fun skipCharacter() {
        val ch = input[offset]

        offset += 1
        if (ch == '\n') {
            line += 1
            column = 0
        } else {
            column += 1
        }
    }
}
