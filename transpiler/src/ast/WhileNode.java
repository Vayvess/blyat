package ast;
import java.util.List;

public record WhileNode(ASTNode condition, List<ASTNode> block) implements ASTNode { }
