package ast;
import lexer.Token;
import java.util.List;

public record MakeNode(Token type, List<ASTNode> args) implements ASTNode { }
