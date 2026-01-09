package ast;
import java.util.List;

public record BranchNode(List<RamificationNode> ramifications) implements ASTNode {
}
