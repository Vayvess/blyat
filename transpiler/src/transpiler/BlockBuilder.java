package transpiler;

public class BlockBuilder {

    private int level;
    private StringBuilder builder;

    public BlockBuilder() {
        level = 0;
        builder = new StringBuilder();
    }

    void indent(String s) {
        builder.append("\t".repeat(level));
        builder.append(s);
    }

    void emit(String s) {
        builder.append(s);
    }

    void enterBlock() {
        level++;
        builder.append(" {\n");
    }

    public void leaveBlock() {
        level--;
        builder.append("\t".repeat(level));
        builder.append("}\n");
    }

    public String build() {
        return builder.toString();
    }
}
