# Al

Al is, well, a language.

Al has the following features:

- indent based
- functional based, with side effects
- statically typed
- ADTs
- pattern matching
- exceptions
- garbage collected
- modules
- type inference
- closures

Well, for a start, it'll have the above features.  It will also be compiled using LLVM and will have a bootstrapped compiler.  The bootstrap compiler will be written in Java however the intention is that the first real Al program will be a compiler for Al.

## Syntax

A characteristic of Al is that it is indent based - in other words indentation is used to signal the opening and closing of blocks much like `{` and `}` do in C-based languages.  To support this the scanner is responsible for keeping track of spaces and emitting `OPEN_BLOCK` and `CLOSE_BLOCK` tokens whenever the indentation level changes.  There is however a "trick" that needs to be considered - indentation is reset whenever entering a nested structure like `( )`, `[ ]` or `{ }`.  This means that the scanner will need to be aware of these structures and keep note of them.

Using these special tokens the grammar can be described using an EBNF grammar.

```text
Program: Expressions;

Expressions: Expression {SEPARATOR Expression};

Expression
  : LetExpression
  | VarExpression
  | AssignmentExpression
  | IfExpression
  | WhileExpression
  | TryExpression
  | SignalExpression
  | ImportExpression
  | OrExpression
  ;
  
OrExpression: AndExpression {"||" OrExpression};

AndExpression: RelationalExpression {RelationalOp RelationExpression};

RelationalExpression: ConsExpression {AdditiveOp ConsExpression};

RelationOp: "==" | "!=" | "<=" | "<" | ">=" | ">";

ConsExpression: AdditiveExpression {ConsOp AdditiveEpxression};

ConsOp: ":";

AdditiveExpression: MultiplicativeExpression {AdditiveOp MultiplicativeExpression};

AdditiveOp: "+" | "-";

MultiplicativeExpression: Factor {MultiplicativeOp Factor};

RelationOp: "*" | "/";

Factor
  : "()"
  | "(" Expressions ")"
  | "[" [Expression {"," Expression}] "]"
  | LiteralBool
  | LiteralChar
  | LiteralI32
  | LiteralString
  | LiteralFloat
  | Identifier [ "::" Type] {Expression}
  ;
  
LiteralBool: "True" | "False";
  
Type: TupleType {"->" TupleType};

TupleType: BasicType {"*" BasicType};

BasicType
  : "()"
  | "Bool"
  | "Char"
  | "I32"
  | "String"
  | "Float"
  | Identifier {Type}
  | "(" Type ")"
  ;
```

Using this grammar and the indentation rules, Al's lexical structure is defined as follows:

```text
tokens
  Identifier = id {digit | id} {"'"};
    
fragments
  digit = '0'-'9';
  id = 'a'-'z' + 'A'-'Z' + '_';
```
