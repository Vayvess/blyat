package transpiler;

public class BlockBuilder {
    private int level;
    private final StringBuilder builder;

    public BlockBuilder(int level) {
        this.level = level;
        this.builder = new StringBuilder();
    }

    public void indent() {
        builder.append("\t".repeat(level));
    }

    public void emit(String s) {
        builder.append(s);
    }

    public void emitIndent(String s) {
        indent();
        builder.append(s);
    }

    public void enterScope() {
        level++;
    }

    public void leaveScope() {
        level--;
    }

    public int getLevel() {
        return level;
    }

    String build() {
        return builder.toString();
    }
}
