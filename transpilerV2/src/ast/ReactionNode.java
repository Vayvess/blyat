package ast;
import lexer.Token;
import java.util.List;

public record ReactionNode(Token name, List<ParameterNode> params, List<ASTNode> block) implements ASTNode { }
