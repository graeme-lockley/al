package al.bootstrap.parser

import al.bootstrap.data.Tuple2
import al.bootstrap.scanner.Token

interface Visitor<T_Program, T_Expressions, T_Expression, T_Factor, T_LiteralBool> {
    fun visitProgram(a: T_Expressions): T_Program

    fun visitExpressions(a1: T_Expression, a2: List<Tuple2<Token, T_Expression>>): T_Expressions

    fun visitExpression10(a: T_Factor): T_Expression

    fun visitFactor1(a: Token): T_Factor
    fun visitFactor2(a1: Token, a2: T_Expressions, a3: Token): T_Factor
    fun visitFactor3(a1: Token, a2: Tuple2<T_Expression, List<Tuple2<Token, T_Expression>>>?, a3: Token): T_Factor
    fun visitFactor5(a: T_LiteralBool): T_Factor
    fun visitFactor6(a: Token): T_Factor
    fun visitFactor7(a: Token): T_Factor
    fun visitFactor8(a: Token): T_Factor

    fun visitLiteralBool1(a: Token): T_LiteralBool
    fun visitLiteralBool2(a: Token): T_LiteralBool
}