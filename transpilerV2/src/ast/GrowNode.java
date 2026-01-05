package ast;
import lexer.Token;
import java.util.List;

public record GrowNode(Token type, List<ASTNode> args) implements ASTNode { }
