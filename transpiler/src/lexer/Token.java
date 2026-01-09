package lexer;

public record Token(TokenType type, String value) {
    @Override
    public String toString() {
        return String.format("Token[%s, %s]", type.toString(), value);
    }
}
