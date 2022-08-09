package al.bootstrap.parser

import al.bootstrap.scanner.scan
import kotlin.test.Test

class ParseTest {
    @Test
    fun givenUnitFactor() {
        parse(scan("()"))
    }
}