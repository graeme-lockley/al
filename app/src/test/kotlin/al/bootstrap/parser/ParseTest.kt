package al.bootstrap.parser

import al.bootstrap.Errors
import al.bootstrap.data.Either
import al.bootstrap.data.Right
import al.bootstrap.parser.ast.Program
import al.bootstrap.scanner.Scanner
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.scopes.FunSpecContainerContext
import io.kotest.matchers.shouldBe
import org.yaml.snakeyaml.Yaml
import java.io.File

private val yaml = Yaml()

class ParserTests : FunSpec({
    context("Conformance Tests") {
        val content = File("./src/test/kotlin/al/bootstrap/parser/parser.yaml").readText()

        val scenarios: Any = yaml.load(content)

        if (scenarios is List<*>) {
            parserConformanceTest(this, scenarios)
        }
    }
})

fun parse(input: String): Either<Errors, Program> =
    parse(Scanner(input))

suspend fun parserConformanceTest(ctx: FunSpecContainerContext, scenarios: List<*>) {
    scenarios.forEach { scenario ->
        val s = scenario as Map<*, *>

        val nestedScenario = s["scenario"] as Map<*, *>?
        if (nestedScenario == null) {
            val name = s["name"] as String
            val input = s["input"] as String
            val output = s["output"]

            ctx.test(name) {
                val lhs =
                    parse(input).map { it.yaml() }.toString()

                val rhs =
                    Right<Errors, Any>(output as Any).toString()

                lhs shouldBe rhs
            }
        } else {
            val name = nestedScenario["name"] as String
            val tests = nestedScenario["tests"] as List<*>
            ctx.context(name) {
                parserConformanceTest(this, tests)
            }
        }
    }
}