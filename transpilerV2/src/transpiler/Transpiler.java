package transpiler;

import ast.*;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Transpiler {

    enum TemplateKind {
        RECEPTOR,
        MOLDER,
        REACTION
    }

    Map<TemplateKind, Templater> templates;

    public Transpiler() throws IOException {
        this.templates = Map.ofEntries(
                Map.entry(TemplateKind.RECEPTOR, new Templater("receptor")),
                Map.entry(TemplateKind.MOLDER, new Templater("molder")),
                Map.entry(TemplateKind.REACTION, new Templater("reaction"))
        );
    }

    String fillTemplate(TemplateKind kind, Map<String, String> fillers) {
        Templater templater = templates.get(kind);
        return templater.generate(fillers);
    }

    void transIdentifier(IdentifierNode node, BlockBuilder builder) {
        builder.emit(node.name());
    }

    void transStr(LiteralStrNode node, BlockBuilder builder) {
        builder.emit(node.value());
    }

    void transInt(LiteralIntNode node, BlockBuilder builder) {
        builder.emit(Integer.toString(node.value()));
    }

    void transFloat(LiteralFloatNode node, BlockBuilder builder) {
        builder.emit(Float.toString(node.value()));
    }

    void transBool(LiteralBoolNode node, BlockBuilder builder) {
        builder.emit(Boolean.toString(node.value()));
    }

    void transFieldAccess(FieldAccessNode node, BlockBuilder builder) {
        transExpr(node.node(), builder);
        builder.emit("->");
        builder.emit(node.name().value());
    }

    void transBinaryExpr(BinaryExprNode node, BlockBuilder builder) {
        transExpr(node.left(), builder);
        builder.emit(" ");
        builder.emit(node.op().value());
        builder.emit(" ");
        transExpr(node.right(), builder);
    }

    void transFunCall(FunCallNode node, BlockBuilder builder) {
        // CALLEE
        transExpr(node.node(), builder);
        builder.emit("(");
        // ARGUMENTS
        for (int i = 0; i < node.args().size(); i++) {
            if (i > 0) builder.emit(", ");
            transExpr(node.args().get(i), builder);
        }
        builder.emit(")");
    }

    void transExpr(ASTNode node, BlockBuilder builder) {
        switch (node) {
            case IdentifierNode x -> transIdentifier(x, builder);
            case LiteralStrNode x -> transStr(x, builder);
            case LiteralIntNode x -> transInt(x, builder);
            case LiteralFloatNode x -> transFloat(x, builder);
            case LiteralBoolNode x -> transBool(x, builder);
            case FieldAccessNode x -> transFieldAccess(x, builder);
            case BinaryExprNode x -> transBinaryExpr(x, builder);
            case FunCallNode x -> transFunCall(x, builder);
            default -> throw new RuntimeException("TRANSPILER: UNEXPECTED EXPR " + node);
        }
    }

    void transCondClause(RamificationNode r, String keyword, BlockBuilder builder) {
        builder.emitIndent(keyword);
        builder.emit(" (");
        transExpr(r.condition(), builder);
        builder.emit(") {\n");
        String block = transBlock(r.block(), builder.getLevel());
        builder.emit(block);
        builder.emitIndent("}\n");
    }

    void transElseClause(RamificationNode r, BlockBuilder builder) {
        builder.emitIndent("else");
        builder.emit(" {\n");
        String block = transBlock(r.block(), builder.getLevel());
        builder.emit(block);
        builder.emitIndent("}\n");
    }

    void transBranch(BranchNode node, BlockBuilder builder) {
        List<RamificationNode> ramifications = node.ramifications();
        transCondClause(ramifications.getFirst(), "if", builder);

        int limit = ramifications.size() - 1;
        for (int x = 1; x < limit; x++) {
            RamificationNode r = ramifications.get(x);
            transCondClause(r, "else if", builder);
        }

        RamificationNode r = ramifications.getLast();
        if (r.condition() == null) {
            transElseClause(r, builder);
        }
    }

    void transAssign(AssignmentNode node, BlockBuilder builder) {
        builder.indent();
        transExpr(node.left(), builder);
        builder.emit(" ");
        builder.emit(node.op().value());
        builder.emit(" ");
        transExpr(node.right(), builder);
        builder.emit(";\n");
    }

    void transEmit(EmitNode node, BlockBuilder builder) {

    }

    void transWhile(WhileNode node, BlockBuilder builder) {
        builder.emitIndent("while (");
        transExpr(node.condition(), builder);
        builder.emit(") {\n");
        String block = transBlock(node.block(), builder.getLevel());
        builder.emit(block);
        builder.emitIndent("}\n");
    }

    void transVarDecl(VarDeclNode node, BlockBuilder builder) {
        builder.emitIndent(node.type().value());
        builder.emit(" ");
        builder.emit(node.name().value());
        builder.emit(" = ");
        transExpr(node.init(), builder);
        builder.emit(";\n");
    }

    String transBlock(List<ASTNode> block, int level) {
        BlockBuilder builder = new BlockBuilder(level);
        builder.enterScope();
        for (ASTNode node : block) {
            switch (node) {
                case EmitNode x -> transEmit(x, builder);
                case BranchNode x -> transBranch(x, builder);
                case WhileNode x -> transWhile(x, builder);
                case VarDeclNode x -> transVarDecl(x, builder);
                case AssignmentNode x -> transAssign(x, builder);
                case FunCallNode x -> {
                    builder.indent();
                    transFunCall(x, builder);
                    builder.emit(";\n");
                }
                default -> throw new RuntimeException("TRANSPILER: UNEXPECTED BLOCK");
            }
        }
        return builder.build();
    }

    String transMolderParams(List<ParameterNode> params) {
        List<String> built = new ArrayList<>();
        for (ParameterNode param : params) {
            String type = param.type().value();
            String name = param.name().value();
            built.add(String.format("%s %s", type, name));
        }
        return String.join(",", built);
    }

    String transMolder(String alias, MolderNode molder) {
        String params = transMolderParams(molder.params());
        String block = transBlock(molder.block(), 0);
        Map<String, String> fillers = Map.ofEntries(
                Map.entry("ALIAS", alias),
                Map.entry("PARAMS", params),
                Map.entry("BLOCK", block)
        );
        return fillTemplate(TemplateKind.MOLDER, fillers);
    }

    String transReactionParams(List<ParameterNode> params) {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < params.size(); x++) {
            ParameterNode p = params.get(x);
            builder.append("\t");
            builder.append(p.type().value());
            builder.append(" ");
            builder.append(p.name().value());
            builder.append(String.format(" = mail->args[%d];\n", x));
        }
        return builder.toString();
    }

    String transReaction(String alias, ReactionNode reaction) {
        String name = reaction.name().value();
        String params = transReactionParams(reaction.params());
        String block = transBlock(reaction.block(), 0);
        Map<String, String> fillers = Map.ofEntries(
                Map.entry("ALIAS", alias),
                Map.entry("NAME", name),
                Map.entry("PARAMS", params),
                Map.entry("BLOCK", block)
        );
        return fillTemplate(TemplateKind.REACTION, fillers);
    }

    String transReactions(String alias, List<ReactionNode> reactions) {
        List<String> built = new ArrayList<>();
        for (ReactionNode reaction : reactions) {
            built.add(transReaction(alias, reaction));
        }
        return String.join("\n", built);
    }

    String transStructure(List<FieldNode> fields) {
        List<String> built = new ArrayList<>();
        for (FieldNode field : fields) {
            String type = field.type().value();
            String name = field.name().value();
            built.add(String.format("%s %s;", type, name));
        }
        return String.join("\n\t", built);
    }

    void transReceptor(ReceptorNode node, BlockBuilder builder) throws IOException {
        String alias = node.name().value();
        String fields = transStructure(node.fields());

        String allocatorType = String.format("%sAllocator", alias);
        String allocatorName = String.format("blyat%sAllocator", alias);

        String molder = transMolder(alias, node.molder());
        String reactions = transReactions(alias, node.reactions());

        Map<String, String> fillers = Map.ofEntries(
                Map.entry("ALIAS", alias),
                Map.entry("FIELDS", fields),
                Map.entry("ALLOCATOR_TYPE", allocatorType),
                Map.entry("ALLOCATOR_NAME", allocatorName),
                Map.entry("MOLDER", molder),
                Map.entry("REACTIONS", reactions)
        );

        String transpilated = fillTemplate(TemplateKind.RECEPTOR, fillers);
        builder.emitIndent(transpilated);
    }

    void transClass(ClassNode node, BlockBuilder builder) {

    }

    public void transpile(RootNode root) throws IOException {
        BlockBuilder builder = new BlockBuilder(0);

        // HEADERS
        builder.emitIndent("#include \"runtime.h\"");
        builder.emit("\n\n");

        for (ASTNode node : root.nodes()) {
            switch (node) {
                case ReceptorNode x -> transReceptor(x, builder);
                case ClassNode x -> transClass(x, builder);
                default -> throw new RuntimeException("TRANSPILER: UNEXPECTED ROOT");
            }
            builder.emit("\n");
        }

        Files.writeString(Paths.get("./blyated/output.c"), builder.build());
    }
}
