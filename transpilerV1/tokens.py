from enum import IntEnum, auto


class TokenType(IntEnum):

    # LITERALS
    LIT_BOOL = auto()
    LIT_FLOAT = auto()
    LIT_INTEGER = auto()
    LIT_STRING = auto()

    # KEYWORDS
    RECEPTOR = auto()
    STRUCTURE = auto()
    MOLD = auto()
    UPON = auto()
    THIS = auto()

    TO = auto()
    CAST = auto()
    BROADCAST = auto()
    
    FUNC = auto()
    RETURN = auto()

    BRANCH = auto()
    ELSE = auto()

    WHILE = auto()
    FOR = auto()

    # OPERATORS
    PLUS = auto()
    MINUS = auto()
    STAR = auto()
    SLASH = auto()
    MODULO = auto()

    INCR = auto()
    DECR = auto()

    ASSIGN = auto()
    PLUS_ASSIGN = auto()
    MINUS_ASSIGN = auto()
    MUL_ASSIGN = auto()
    DIV_ASSIGN = auto()
    MOD_ASSIGN = auto()

    EQ = auto()
    NEQ = auto()
    LT = auto()
    GT = auto()
    LTE = auto()
    GTE = auto()

    AND = auto()
    OR = auto()
    NOT = auto()

    # DELIMITERS
    DOT = auto()
    LPAREN = auto()
    RPAREN = auto()
    LBRACE = auto()
    RBRACE = auto()
    LBRACKET = auto()
    RBRACKET = auto()
    COMMA = auto()
    SEMICOLON = auto()

    # MISCELLENEOUS
    IDENT = auto()
    TOSKIP = auto()
    NEWLINE = auto()

class Token:
    def __init__(self, value, token_type):
        self.value = value
        self.type = token_type
    
    def __str__(self):
        return f"Token[{self.type.name}: {self.value}]"

TOKEN_SPECS = (
    # === KEYWORDS ===
    (r"\breceptor\b", TokenType.RECEPTOR),
    (r"\bstructure\b", TokenType.STRUCTURE),
    (r"\bmold\b", TokenType.MOLD),
    (r"\bupon\b", TokenType.UPON),
    (r"\bto\b", TokenType.TO),
    (r"\bcast\b", TokenType.CAST),
    
    # (r"\bthis\b", TokenType.THIS),
    # (r"\bbroadcast\b", TokenType.BROADCAST),

    (r"\bbranch\b", TokenType.BRANCH),
    (r"\belse\b", TokenType.ELSE),

    # (r"\bfunc\b", TokenType.FUNC),
    (r"\breturn\b", TokenType.RETURN),
    

    # === OPERATORS ===
    (r"<", TokenType.LT),
    (r">", TokenType.GT),
    (r"==", TokenType.EQ),
    (r"!=", TokenType.NEQ),
    (r"<=", TokenType.LTE),
    (r">=", TokenType.GTE),

    (r"\+\+", TokenType.INCR),
    (r"\+", TokenType.PLUS),

    (r"--", TokenType.DECR),
    (r"-", TokenType.MINUS),

    (r"\*", TokenType.STAR),
    (r"/", TokenType.SLASH),
    (r"%", TokenType.MODULO),

    (r"=", TokenType.ASSIGN),
    (r"\+=", TokenType.PLUS_ASSIGN),
    (r"-=",  TokenType.MINUS_ASSIGN),
    (r"\*=", TokenType.MUL_ASSIGN),
    (r"/=",  TokenType.DIV_ASSIGN),
    (r"%=",  TokenType.MOD_ASSIGN),

    # --- DELIMITERS ---
    (r"\(", TokenType.LPAREN),
    (r"\)", TokenType.RPAREN),
    (r"\{", TokenType.LBRACE),
    (r"\}", TokenType.RBRACE),
    (r"\[", TokenType.LBRACKET),
    (r"\]", TokenType.RBRACKET),
    (r",", TokenType.COMMA),
    (r"\.", TokenType.DOT),
    (r";", TokenType.SEMICOLON),

    # === LITERALS ===
    (r"\btrue\b", TokenType.LIT_BOOL),
    (r"\bfalse\b", TokenType.LIT_BOOL),
    (r"\d+", TokenType.LIT_INTEGER),
    (r'"[^"\n]*"', TokenType.LIT_STRING),

    # === MISCELLANEOUS ===
    (r"\n", TokenType.NEWLINE),
    (r"[ \t]+", TokenType.TOSKIP),
    (r"//[^\n]*", TokenType.TOSKIP),
    (r"[A-Za-z_][A-Za-z0-9_]*", TokenType.IDENT),
)
