package ast;
import lexer.Token;

public record AssignmentNode(ASTNode left, Token op, ASTNode right) implements ASTNode{ }
