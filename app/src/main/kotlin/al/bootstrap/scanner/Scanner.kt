package al.bootstrap.scanner


data class Comment(var lexeme: String, var location: Location)

enum class TToken {
    ERROR,
    EOS,
    OPEN_BLOCK,
    CLOSE_BLOCK,
    IDENTIFIER
}

data class Token(var lexeme: String?, var tToken: TToken, var location: Location, var comments: List<Comment>)

data class Block(var indent: Int)

fun scan(input: String): Scanner =
    Scanner(input)

class Scanner(private val input: String) {
    private val inputLength = input.length

    private val blocks = ArrayDeque<Block>()

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

        run {
            var indent = false
            var startOffset = nextOffset
            var startLine = nextLine
            var startColumn = nextColumn
            while (true) {
                if (nextColumn == 0) {
                    indent = true
                    startOffset = nextOffset
                    startLine = nextLine
                    startColumn = 0
                }

                if (nextOffset < inputLength && nextCh.isWhitespace()) {
                    skipCharacter()
                } else {
                    break
                }
            }

            if (indent && nextOffset < inputLength) {
                if (nextColumn > if (blocks.isEmpty()) 0 else blocks.last().indent) {
                    blocks.addLast(Block(nextColumn))
                    val lexeme = input.slice(startOffset until nextOffset)
                    token = Token(lexeme, TToken.OPEN_BLOCK, locationFrom(startOffset, startLine, startColumn, offset, line, column), emptyList())
                    return true
                } else if (blocks.isNotEmpty() && nextColumn < blocks.last().indent) {
                    blocks.removeLast()

                    token = Token("", TToken.CLOSE_BLOCK, locationFrom(startOffset, startLine, startColumn, offset, line, column), emptyList())
                    return true
                }
            }
        }

        if (nextOffset == inputLength) {
            token = if (blocks.isEmpty()) {
                Token("", TToken.EOS, PointLocation(nextOffset, nextLine, nextColumn), emptyList())
            } else {
                blocks.removeLast()
                Token("", TToken.CLOSE_BLOCK, PointLocation(nextOffset, nextLine, nextColumn), emptyList())
            }
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
                        TToken.IDENTIFIER,
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
