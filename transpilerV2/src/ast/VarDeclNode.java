package ast;

import lexer.Token;

public record VarDeclNode(Token type, Token name, ASTNode init) implements ASTNode { }
