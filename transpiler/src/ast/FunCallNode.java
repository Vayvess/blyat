package ast;
import java.util.List;

public record FunCallNode(ASTNode node, List<ASTNode> args) implements ASTNode { }
