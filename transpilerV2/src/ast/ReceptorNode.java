package ast;
import lexer.Token;
import java.util.List;

public record ReceptorNode(
        Token name, List<FieldNode> fields,
        MolderNode molder, List<ReactionNode> reactions
) implements ASTNode { }
