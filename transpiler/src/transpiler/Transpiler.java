package transpiler;

import ast.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Transpiler {

    enum Section {
        HEADER,
        IMPLEM,
        PAYLOAD,
        RECEPTOR_STRUCTS,
        ALLOCATOR_STRUCTS
    }

    BlockBuilder b;
    EnumMap<Section, BlockBuilder> builders;

    public Transpiler() {
        builders = new EnumMap<>(Section.class);
        for (Section section : Section.values()) {
            builders.put(section, new BlockBuilder());
        }
        b = builders.get(Section.HEADER);
        b.emit("#include \"blyatypes.h\"\n\n");

        b = builders.get(Section.IMPLEM);
        b.emit("#include \"blyatypes.h\"\n\n");

        b = builders.get(Section.PAYLOAD);
        b.emit("typedef union {\n");
    }

    private void setBuilder(Section section) {
        b = builders.get(section);
    }

    private void transIdentifier(IdentifierNode node) {
        b.emit(node.name());
    }

    private void transStr(LiteralStrNode node) {
        b.emit(node.value());
    }

    private void transInt(LiteralIntNode node) {
        b.emit(Integer.toString(node.value()));
    }

    private void transFloat(LiteralFloatNode node) {
        b.emit(Float.toString(node.value()));
    }

    private void transBool(LiteralBoolNode node) {
        b.emit(Boolean.toString(node.value()));
    }

    private void transFieldAccess(FieldAccessNode node) {
        transExpr(node.node());
        b.emit("->");
        b.emit(node.name().value());
    }

    void transFunCall(FunCallNode node) {
        transExpr(node.node());
        b.emit("(");
        for (int i = 0; i < node.args().size(); i++) {
            if (i > 0) b.emit(", ");
            transExpr(node.args().get(i));
        }
        b.emit(")");
    }

    private void transBinaryExpr(BinaryExprNode node) {
        transExpr(node.left());
        b.emit(" ");
        b.emit(node.op().value());
        b.emit(" ");
        transExpr(node.right());
    }

    void transExpr(ASTNode node) {
        switch (node) {
            case IdentifierNode x -> transIdentifier(x);
            case LiteralStrNode x -> transStr(x);
            case LiteralIntNode x -> transInt(x);
            case LiteralFloatNode x -> transFloat(x);
            case LiteralBoolNode x -> transBool(x);
            case FieldAccessNode x -> transFieldAccess(x);
            case BinaryExprNode x -> transBinaryExpr(x);
            case FunCallNode x -> transFunCall(x);
            default -> throw new RuntimeException("TRANSPILER: UNEXPECTED EXPR " + node);
        }
    }

    private void transAssign(AssignmentNode node) {
        b.indent("");
        transExpr(node.left());
        b.emit(" ");
        b.emit(node.op().value());
        b.emit(" ");
        transExpr(node.right());
        b.emit(";\n");
    }

    private void transVarDecl(VarDeclNode node) {
        b.indent(node.type().value());
        b.emit(" ");
        b.emit(node.name().value());
        b.emit(" = ");
        transExpr(node.init());
        b.emit(";\n");
    }

    private void transWhile(WhileNode node) {
        b.indent("while (");
        transExpr(node.condition());
        b.indent(")");
        b.enterBlock();
        transBlock(node.block());
        b.leaveBlock();
    }

    private void transBranch(BranchNode node) {
        List<RamificationNode> ramifications = node.ramifications();
        int limit = ramifications.size() - 1;
        for (int x = 0; x < limit; x++) {
            RamificationNode r = ramifications.get(x);
            if (r.condition() != null) {
                b.indent("else if (");
                transExpr(r.condition());
                b.emit(")");
            }
            else {
                b.indent("else");
            }

            b.enterBlock();
            transBlock(r.block());
            b.leaveBlock();
        }
    }

    private void transHops(HopsNode node) {

    }

    public void transBlock(List<ASTNode> block) {
        // setBuilder(Section.CURRENT_IMPLEM);
        for (ASTNode node : block) {
            switch (node) {
                case HopsNode x -> transHops(x);
                case BranchNode x -> transBranch(x);
                case WhileNode x -> transWhile(x);
                case VarDeclNode x -> transVarDecl(x);
                case AssignmentNode x -> transAssign(x);
                case FunCallNode x -> {
                    b.indent("");
                    transFunCall(x);
                    b.emit(";\n");
                }
                default -> throw new RuntimeException("TRANSPILER: UNEXPECTED BLOCK");
            }
        }
    }

    public void transReceptorStruct(List<FieldNode> nodes, String t) {
        setBuilder(Section.RECEPTOR_STRUCTS);
        b.emit("typedef struct {\n");
        for (FieldNode field : nodes) {
            String type = field.type().value();
            String iden = field.name().value();
            b.emit(String.format("\t%s %s;\n", type, iden));
        }
        b.emit(String.format("} %s;\n\n", t));
    }

    public void transReceptorAllocatorStruct(String t) {
        setBuilder(Section.ALLOCATOR_STRUCTS);
        String allocatorType = String.format("%sAllocator", t);
        b.emit("typedef struct {\n");
        b.emit(String.format("\t%s *array;\n", t));
        b.emit("\tuint32_t len;\n");
        b.emit("\tuint32_t cap;\n");
        b.emit("\tuint32_t *free;\n");
        b.emit("\tuint32_t free_len;\n");
        b.emit("\tuint32_t free_cap;\n");
        b.emit(String.format("} %s;\n\n", allocatorType));
        b.emit(String.format("static %s blyat%s[NTHREADS];\n\n", allocatorType, allocatorType));
    }

    public String getMolderSignature(MolderNode node, String t) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("void %s_init(%sAllocator *alc", t, t));

        for (ParameterNode param : node.params()) {
            builder.append(", ");
            builder.append(param.type().value());
            builder.append(" ");
            builder.append(param.name().value());
        }

        builder.append(")");
        return builder.toString();
    }

    public void transReceptorMolder(MolderNode node, String t) {
        String signature = getMolderSignature(node, t);
        setBuilder(Section.HEADER);
        b.emit(String.format("%s;\n\n", signature));

        setBuilder(Section.IMPLEM);
        b.emit(String.format("#define TYPE %s\n", t));
        b.emit("#include \"allocator_template.h\"");
        b.emit("\n\n");

        b.emit(signature);
        b.enterBlock();
        b.indent(String.format("uint32_t id = %s_alloc(alc);\n", t));
        b.indent(String.format("%s *self = &alc->array[id];\n", t));
        transBlock(node.block());
        b.leaveBlock();
        b.emit("\n");
    }

    public void transReceptorReaction(ReactionNode node, String t) {
        String name = node.name().value();
        String signature = String.format("void %s_%s(Mail *mail)", t, name);

        setBuilder(Section.HEADER);
        b.emit(String.format("%s;\n\n", signature));

        setBuilder(Section.IMPLEM);
        b.emit(signature);
        b.enterBlock();
        b.indent(String.format("%s *self = &blyat%sAllocator->array[mail->target];\n", t, t));
        for (ParameterNode param : node.params()) {
            String type = param.type().value();
            String iden = param.name().value();
            b.indent(String.format("%s %s = mail->payload.%s.%s;\n", type, iden, name, iden));
        }
        transBlock(node.block());
        b.leaveBlock();

        setBuilder(Section.PAYLOAD);
        b.emit("\tstruct {\n");
        for (ParameterNode param : node.params()) {
            String type = param.type().value();
            String iden = param.name().value();
            b.emit(String.format("\t\t%s %s;\n", type, iden));
        }
        b.emit(String.format("\t} %s;\n", name));
    }

    public void transReceptor(ReceptorNode node) {
        String t = node.name().value();
        transReceptorStruct(node.struct(), t);
        transReceptorAllocatorStruct(t);
        transReceptorMolder(node.molder(), t);

        for (ReactionNode reaction : node.reactions()) {
            transReceptorReaction(reaction, t);
        }
    }

    public void transpile(RootNode root) throws IOException {
        for (ASTNode node : root.nodes()) {
            switch (node) {
                case ReceptorNode x -> transReceptor(x);
                default -> throw new RuntimeException("TRANSPILER: UNEXPECTED ROOT");
            }
        }

        // FILE GENERATION
        Templater runtimeTemplate = new Templater("runtime");
        Path pathRuntime = Paths.get("./blyated/blyatime.h");
        Files.writeString(pathRuntime, runtimeTemplate.generate(null));

        Templater allocatorTemplate = new Templater("allocator");
        Path pathAllocator = Paths.get("./blyated/allocator_template.h");
        Files.writeString(pathAllocator, allocatorTemplate.generate(null));

        Path pathHeader = Paths.get("./blyated/header.h");
        BlockBuilder header = builders.get(Section.HEADER);
        Files.writeString(pathHeader, header.build());

        Path pathImplem = Paths.get("./blyated/implem.c");
        BlockBuilder implem = builders.get(Section.IMPLEM);
        implem.emit("int main() {\n");
        implem.emit("\treturn 0;\n");
        implem.emit("}\n");
        Files.writeString(pathImplem, implem.build());

        Path pathTypes = Paths.get("./blyated/blyatypes.h");
        BlockBuilder payloads = builders.get(Section.PAYLOAD);
        BlockBuilder receptorStructs = builders.get(Section.RECEPTOR_STRUCTS);
        BlockBuilder allocatorStructs = builders.get(Section.ALLOCATOR_STRUCTS);

        payloads.emit("} Payload;");
        Templater typesTemplate = new Templater("types");
        Files.writeString(pathTypes, typesTemplate.generate(Map.ofEntries(
                Map.entry("PAYLOADS", payloads.build()),
                Map.entry("RECEPTORS", receptorStructs.build()),
                Map.entry("ALLOCATORS", allocatorStructs.build())
        )));
    }
}
