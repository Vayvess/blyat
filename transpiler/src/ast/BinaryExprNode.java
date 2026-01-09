package ast;
import lexer.Token;

public record BinaryExprNode(ASTNode left, Token op, ASTNode right) implements ASTNode { }
