package lexer;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    public record TokenRule(Pattern pattern, TokenType type){}

    private final List<TokenRule> rules;
    public Lexer() {
        this.rules = new ArrayList<>();
        build();
    }

    private void consider(String regex, TokenType type) {
        Pattern pattern = Pattern.compile(regex);
        rules.add(new TokenRule(pattern, type));
    }

    private void build() {
        consider("\\bgrow\\b", TokenType.GROW);
        consider("\\breceptor\\b", TokenType.RECEPTOR);
        consider("\\bstructure\\b", TokenType.STRUCTURE);
        consider("\\bmold\\b", TokenType.MOLD);
        // consider("\\bself\\b", TokenType.SELF);
        consider("\\bupon\\b", TokenType.UPON);

        consider("\\bto\\b", TokenType.TO);
        consider("\\bemit\\b", TokenType.EMIT);
        // consider("\\bmyself\\b", TokenType.MYSELF);

        consider("\\bwhile\\b", TokenType.WHILE);
        consider("\\bbranch\\b", TokenType.BRANCH);
        consider("\\belse\\b", TokenType.ELSE);

        // consider("\\bfunc\\b", TokenType.FUNC);
        // consider("\\breturn\\b", TokenType.RETURN);

        // === ASSIGNATORS ==
        consider("=", TokenType.ASSIGN);
        consider("\\+=", TokenType.PLUS_ASSIGN);
        consider("-=", TokenType.MINUS_ASSIGN);
        consider("\\*=", TokenType.MUL_ASSIGN);
        consider("/=", TokenType.DIV_ASSIGN);
        consider("%=", TokenType.MOD_ASSIGN);

        // === OPERATORS ===
        consider("\\.", TokenType.DOT);
        consider("::", TokenType.DOUBLECOLON);

        consider("==", TokenType.EQ);
        consider("!=", TokenType.NEQ);
        consider("<=", TokenType.LTE);
        consider(">=", TokenType.GTE);
        consider("<", TokenType.LT);
        consider(">", TokenType.GT);

        consider("\\+", TokenType.PLUS);
        consider("-", TokenType.MINUS);
        consider("\\*", TokenType.STAR);
        consider("/", TokenType.SLASH);
        consider("%", TokenType.MODULO);

        consider("!", TokenType.NOT);
        consider("&&", TokenType.AND);
        consider("\\|\\|", TokenType.OR);

        // === DELIMITERS ===
        consider("\\{", TokenType.LBRACE);
        consider("\\}", TokenType.RBRACE);
        consider("\\(", TokenType.LPAREN);
        consider("\\)", TokenType.RPAREN);
        consider("\\[", TokenType.LBRACKET);
        consider("\\]", TokenType.RBRACKET);
        consider(",", TokenType.COMMA);
        consider(";", TokenType.SEMICOLON);

        // === TO SKIP ===
        consider("\\s+", TokenType.TOSKIP);
        consider("//.*", TokenType.TOSKIP);

        // === LITERALS ===
        consider("\\btrue\\b", TokenType.LIT_BOOL);
        consider("\\bfalse\\b", TokenType.LIT_BOOL);

        // consider("\\d*\\.\\d+([eE][+-]?\\d+)?", TokenType.LIT_FLOAT);
        consider("\\d+", TokenType.LIT_INT);
        consider("\"(\\\\.|[^\"])*\"", TokenType.LIT_STR);

        consider("[a-zA-Z_][a-zA-Z0-9_]*", TokenType.IDENTIFIER);
    }

    public String tryMatch(TokenRule rule, String s, int a, int b) {
        Matcher matcher = rule.pattern().matcher(s);
        matcher.region(a, b);
        return matcher.lookingAt() ? matcher.group() : "";
    }

    private Token munch(String s, int pos, int end) {
        String maxMunched = "";
        TokenType maxMunchedType = null;

        for (TokenRule rule : rules) {
            String matched = tryMatch(rule, s, pos, end);

            if (maxMunched.length() < matched.length()) {
                maxMunched = matched;
                maxMunchedType = rule.type();
            }
        }

        return new Token(maxMunchedType, maxMunched);
    }

    public List<Token> lex(String s) {
        int pos = 0;
        int end = s.length();
        List<Token> tokens = new ArrayList<>();

        while (pos < end) {
            Token munched = munch(s, pos, end);

            if (munched.type() == null) {
                System.out.println(s.substring(Math.max(0, pos - 2), Math.min(end, pos + 2)));
                throw new RuntimeException("LEXER ERROR: NO MATCH");
            }

            if (munched.type() != TokenType.TOSKIP) {
                tokens.add(munched);
            }

            pos += munched.value().length();
        }

        return tokens;
    }
}