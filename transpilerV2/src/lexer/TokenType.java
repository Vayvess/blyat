package lexer;

public enum TokenType {
    // LITERALS
    LIT_STR,
    LIT_INT,
    LIT_FLOAT,
    LIT_BOOL,

    // KEYWORDS
    GROW,
    RECEPTOR,
    STRUCTURE,
    MOLD,
    SELF,
    UPON,

    TO,
    EMIT,
    MYSELF,

    MAKE,
    CLASS,
    METH,

    FUNC,
    RETURN,

    WHILE,
    BRANCH,
    ELSE,

    // ASSIGNATORS
    ASSIGN,
    PLUS_ASSIGN,
    MINUS_ASSIGN,
    MUL_ASSIGN,
    DIV_ASSIGN,
    MOD_ASSIGN,

    // OPERATORS
    DOT,
    PLUS,
    MINUS,
    STAR,
    SLASH,
    MODULO,
    BANG,
    DOUBLECOLON,

    EQ,
    NEQ,
    LT,
    GT,
    LTE,
    GTE,

    OR,
    AND,
    NOT,

    // DELIMITERS
    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACKET,
    RBRACKET,
    COMMA,
    SEMICOLON,

    // MISCELLENEOUS
    TOSKIP,
    NEWLINE,
    IDENTIFIER
}
