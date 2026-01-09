package ast;

import lexer.Token;

public record StaticAccessNode(ASTNode node, Token name) implements ASTNode { }
