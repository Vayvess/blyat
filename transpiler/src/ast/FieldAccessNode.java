package ast;
import lexer.Token;

public record FieldAccessNode(ASTNode node, Token name) implements ASTNode { }
