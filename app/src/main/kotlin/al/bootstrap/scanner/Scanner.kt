package al.bootstrap.scanner


data class Comment(var lexeme: String, var location: Location)

enum class TToken {
    ERROR, EOS, OPEN_BLOCK, SEPARATOR, CLOSE_BLOCK,

    IDENTIFIER,

    FALSE,
    TRUE,

    COMMA,
    LBRACKET,
    LPAREN,
    LPAREN_RPAREN,
    RBRACKET,
    RPAREN
}

data class Token(var lexeme: String?, var tToken: TToken, var location: Location, var comments: List<Comment>)

data class Block(var indent: Int)

fun scan(input: String): Scanner = Scanner(input)

val keywords = hashMapOf(
    Pair("False", TToken.FALSE),
    Pair("True", TToken.TRUE),
)

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
        if (token.tToken == TToken.EOS) return false

        if (nextColumn != 0 || nextCh.isWhitespace()) {
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
                val lastIndent = if (blocks.isEmpty()) 0 else blocks.last().indent
                token = if (nextColumn > lastIndent) {
                    blocks.addLast(Block(nextColumn))
                    val lexeme = input.slice(startOffset until nextOffset)
                    Token(lexeme, TToken.OPEN_BLOCK, locationFrom(startOffset, startLine, startColumn, offset, line, column), emptyList())
                } else if (nextColumn < lastIndent) {
                    blocks.removeLast()

                    Token("", TToken.CLOSE_BLOCK, locationFrom(startOffset, startLine, startColumn, offset, line, column), emptyList())
                } else {
                    Token("", TToken.SEPARATOR, locationFrom(startOffset, startLine, startColumn, offset, line, column), emptyList())
                }

                return true
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
                    val proposedTToken = keywords[lexeme]

                    token = if (proposedTToken == null) {
                        Token(lexeme, TToken.IDENTIFIER, locationFrom(startOffset, startLine, startColumn, offset, line, column), emptyList())
                    } else {
                        Token("", proposedTToken, locationFrom(startOffset, startLine, startColumn, offset, line, column), emptyList())
                    }
                }
                '(' -> {
                    skipCharacter()
                    token = if (nextCh == ')') {
                        skipCharacter()

                        Token("", TToken.LPAREN_RPAREN, locationFrom(startOffset, startLine, startColumn, offset, line, column), emptyList())
                    } else {
                        Token("", TToken.LPAREN, locationFrom(startOffset, startLine, startColumn, offset, line, column), emptyList())
                    }
                }
                '[' -> {
                    skipCharacter()
                    token = Token("", TToken.LBRACKET, locationFrom(startOffset, startLine, startColumn, offset, line, column), emptyList())
                }
                ']' -> {
                    skipCharacter()
                    token = Token("", TToken.RBRACKET, locationFrom(startOffset, startLine, startColumn, offset, line, column), emptyList())
                }
                ',' -> {
                    skipCharacter()
                    token = Token("", TToken.COMMA, locationFrom(startOffset, startLine, startColumn, offset, line, column), emptyList())
                }
                ')' -> {
                    skipCharacter()
                    token = Token("", TToken.RPAREN, locationFrom(startOffset, startLine, startColumn, offset, line, column), emptyList())
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
