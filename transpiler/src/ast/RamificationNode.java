package ast;
import java.util.List;

public record RamificationNode(ASTNode condition, List<ASTNode> block) implements ASTNode { }
