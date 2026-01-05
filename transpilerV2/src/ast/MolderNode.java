package ast;
import java.util.List;

public record MolderNode(List<ParameterNode> params, List<ASTNode> block) implements ASTNode { }
