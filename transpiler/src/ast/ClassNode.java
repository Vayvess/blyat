package ast;
import lexer.Token;
import java.util.List;

public record ClassNode(
        Token name, List<FieldNode> struct,
        MolderNode molder, List<ReactionNode> reactions) implements ASTNode {
}
