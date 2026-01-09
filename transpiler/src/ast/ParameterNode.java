package ast;

import lexer.Token;

public record ParameterNode(Token type, Token name) implements ASTNode { }
