package al.bootstrap

import al.bootstrap.data.Yamlable
import al.bootstrap.scanner.TToken
import al.bootstrap.scanner.Token

sealed interface Errors : Yamlable

data class ParseError(
    val found: Token,
    val expected: Set<TToken>
) : Errors {
    override fun yaml(): Any =
        singletonMap(
            "ParseError", mapOf(
                Pair("found", found),
                Pair("expected", expected)
            )
        )
}
