package al.bootstrap.scanner

abstract class Location

data class PointLocation(val offset: Int, val line: Int, val column: Int) : Location()
data class RangeLocation(val start: PointLocation, val end: PointLocation) : Location()

fun locationFrom(startOffset: Int, startLine: Int, startColumn: Int, endOffset: Int, endLine: Int, endColumn: Int): Location =
    if (startOffset == endOffset)
        PointLocation(startOffset, startLine, startColumn)
    else
        RangeLocation(PointLocation(startOffset, startLine, startColumn), PointLocation(endOffset, endLine, endColumn))

data class Comment(var lexeme: String, var location: Location)

enum class TToken {
    ERROR,
    EOS,
    OPEN_BLOCK,
    CLOSE_BLOCK,
    SYMBOL
}

data class Token(var lexeme: String?, var tToken: TToken, var location: Location, var comments: List<Comment>)

fun scan(input: String): Scanner =
    Scanner(input)

class Scanner(private val input: String) {
    private val inputLength = input.length

    private var offset = 0
    private var line = 0
    private var column = 0

    private var nextOffset = 0
    private var nextLine = 0
    private var nextColumn = 0

    private var nextCh = if (inputLength == 0) ' ' else input[0]

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

        while (nextOffset < inputLength && input[nextOffset].isWhitespace()) {
            skipCharacter()
        }

        if (nextOffset == inputLength) {
            token = Token("", TToken.EOS, PointLocation(nextOffset, nextLine, nextColumn), emptyList())
        } else {
            val startOffset = nextOffset
            val startLine = nextLine
            val startColumn = nextColumn

            when (input[nextOffset]) {
                in 'a'..'z', in 'A'..'Z' -> {
                    skipCharacter()
                    while (nextCh.isLetterOrDigit() || nextCh == '_') {
                        skipCharacter()
                    }
                    while (nextCh == '\'') {
                        skipCharacter()
                    }
                    val lexeme = input.slice(startOffset until nextOffset)

                    token = Token(
                        lexeme,
                        TToken.SYMBOL,
                        locationFrom(startOffset, startLine, startColumn, offset, line, column),
                        emptyList()
                    )
                }
            }
        }

        return true
    }

    private fun skipCharacter() {
        offset = nextOffset
        line = nextLine
        column = nextColumn

        nextOffset += 1

        if (nextCh == '\n') {
            nextLine += 1
            nextColumn = 0
        } else {
            nextColumn += 1
        }

        nextCh = if (nextOffset < inputLength) {
            input[nextOffset]
        } else {
            Char(0)
        }
    }
}
