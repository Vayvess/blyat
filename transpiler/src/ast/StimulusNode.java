package ast;
import lexer.Token;
import java.util.List;

public record StimulusNode(Token label, List<ASTNode> args) implements ASTNode { }
