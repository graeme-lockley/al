package al.bootstrap.parser

import al.bootstrap.data.Tuple2
import al.bootstrap.scanner.Token

interface Visitor<T_Program, T_Expressions, T_Expression, T_Factor> {
    fun visitProgram(a: T_Expressions): T_Program

    fun visitExpressions(a1: T_Expression, a2: List<Tuple2<Token, T_Expression>>): T_Expressions

    fun visitExpression10(a: T_Factor): T_Expression

    fun visitFactor1(a: Token): T_Factor
}