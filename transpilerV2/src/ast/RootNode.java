package ast;
import java.util.List;

public record RootNode(List<ASTNode> nodes) implements ASTNode { }
