package parser;

import ast.*;
import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Parser {

    private static final Set<TokenType> ASSIGNATORS = Set.of(
            TokenType.ASSIGN,
            TokenType.PLUS_ASSIGN,
            TokenType.MINUS_ASSIGN,
            TokenType.MUL_ASSIGN,
            TokenType.DIV_ASSIGN,
            TokenType.MOD_ASSIGN
    );

    private static final Set<TokenType> PREFIX_UNARIES = Set.of(
            TokenType.NOT,
            TokenType.MINUS
    );

    private int index;
    private List<Token> tokens;

    private TokenType peek() {
        if (index < tokens.size()) {
            return tokens.get(index).type();
        }

        throw new RuntimeException("PARSER: peek()");
    }

    private TokenType peek(int ahead) {
        if (index + ahead < tokens.size()) {
            return tokens.get(index + ahead).type();
        }
        throw new RuntimeException("PARSER: peek(ahead)");
    }
    
    private Token grab() {
        if (index < tokens.size()) {
            return tokens.get(index++);
        }
        throw new RuntimeException("PARSER: end of input");
    }

    private Token pull(TokenType type) {
        if (index < tokens.size()) {
            Token token = tokens.get(index++);
            if (token.type() == type) return token;

            showAround();
            String error = String.format("PARSER: %s expected instead of %s", type, token.type());
            throw new RuntimeException(error);
        }

        throw new RuntimeException("PARSER: end of input");
    }

    private Token pullif(TokenType type) {
        if (index < tokens.size()) {
            Token token = tokens.get(index);
            if (token.type() == type) {
                index += 1;
                return token;
            }
        }
        return null;
    }

    private int getPrecedence(TokenType type) {
        return switch (type) {
            // ASSIGNMENTS
            case ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN, MUL_ASSIGN, DIV_ASSIGN, MOD_ASSIGN -> 1;

            // EQUALITY
            case EQ, NEQ -> 3;

            // COMPARISON
            case LT, LTE, GT, GTE -> 4;

            // MATH
            case PLUS, MINUS -> 5;
            case STAR, SLASH, MODULO -> 6;

            default -> 0;
        };
    }

    private void showAround() {
        System.out.println("\nDUBUGING: SHOW AROUND");
        int a = Math.max(0, index);
        int b = Math.min(index + 4, tokens.size());

        for (int x = a; x < b; x++) {
            System.out.println(tokens.get(x));
        }
    }

    public StimulusNode parseStimulus() {
        pull(TokenType.GT);
        Token label = pull(TokenType.IDENTIFIER);
        pull(TokenType.LPAREN);

        List<ASTNode> args = new ArrayList<>();
        while (peek() != TokenType.RPAREN) {
            args.add(parseExpr(0));
            pullif(TokenType.COMMA);
        }

        pull(TokenType.RPAREN);
        return new StimulusNode(label, args);
    }

    public GrowNode parseGrow() {
        pull(TokenType.GROW);
        Token type = pull(TokenType.IDENTIFIER);
        pull(TokenType.LPAREN);

        List<ASTNode> args = new ArrayList<>();
        while (peek() != TokenType.RPAREN) {
            args.add(parseExpr(0));
            pullif(TokenType.COMMA);
        }

        pull(TokenType.RPAREN);
        return new GrowNode(type, args);
    }

    public MakeNode parseMake() {
        pull(TokenType.MAKE);
        Token type = pull(TokenType.IDENTIFIER);
        pull(TokenType.LPAREN);

        List<ASTNode> args = new ArrayList<>();
        while (peek() != TokenType.RPAREN) {
            args.add(parseExpr(0));
            pullif(TokenType.COMMA);
        }

        pull(TokenType.RPAREN);
        return new MakeNode(type, args);
    }

    public FunCallNode parseFunCall(ASTNode node) {
        pull(TokenType.LPAREN);
        List<ASTNode> args = new ArrayList<>();
        while (peek() != TokenType.RPAREN) {
            args.add(parseExpr(0));
            pullif(TokenType.COMMA);
        }

        pull(TokenType.RPAREN);
        return new FunCallNode(node, args);
    }

    public ASTNode parsePrimary() {
        TokenType curr = peek();
        switch (curr) {
            case GT -> {
                return parseStimulus();
            }
            case GROW -> {
                return parseGrow();
            }
            case MAKE -> {
                return parseMake();
            }
            case IDENTIFIER -> {
                Token token = pull(TokenType.IDENTIFIER);
                return new IdentifierNode(token.value());
            }
            case LIT_STR -> {
                Token token = pull(TokenType.LIT_STR);
                return new LiteralStrNode(token.value());
            }
            case LIT_INT -> {
                Token token = pull(TokenType.LIT_INT);
                int value = Integer.parseInt(token.value());
                return new LiteralIntNode(value);
            }
            case LIT_FLOAT -> {
                Token token = pull(TokenType.LIT_FLOAT);
                float value = Float.parseFloat(token.value());
                return new LiteralFloatNode(value);
            }
            case LIT_BOOL -> {
                Token token = pull(TokenType.LIT_BOOL);
                boolean value = Boolean.parseBoolean(token.value());
                return new LiteralBoolNode(value);
            }
            case LPAREN -> {
                pull(TokenType.LPAREN);
                ASTNode expr = parseExpr(0);
                pull(TokenType.RPAREN);
                return expr;
            }
            default -> {
                showAround();
                throw new RuntimeException("PARSER: parsePrimary");
            }
        }
    }

    public ASTNode parsePostfix() {
        ASTNode node = parsePrimary();

        while (true) {
            switch (peek()) {
                case DOT -> {
                    pull(TokenType.DOT);
                    Token name = pull(TokenType.IDENTIFIER);
                    node = new FieldAccessNode(node, name);
                }
                case DOUBLECOLON -> {
                    pull(TokenType.DOUBLECOLON);
                    Token name = pull(TokenType.IDENTIFIER);
                    node = new StaticAccessNode(node, name);
                }
                case LPAREN -> {
                    node = parseFunCall(node);
                }
                default -> {
                    return node;
                }
            }
        }
    }

    public ASTNode parseExpr(int minPrec) {
        ASTNode lhs = parsePostfix();
        while (true) {
            TokenType curr = peek();
            int p = getPrecedence(curr);
            if (p == 0 || p < minPrec) break;

            Token op = pull(curr);
            if (ASSIGNATORS.contains(curr)) {
                ASTNode lhr = parseExpr(0);
                lhs = new AssignmentNode(lhs, op, lhr);
            }
            else {
                ASTNode lhr = parseExpr(p + 1);
                lhs = new BinaryExprNode(lhs, op, lhr);
            }
        }
        return lhs;
    }

    public List<FieldNode> parseStructure() {
        pull(TokenType.STRUCTURE);
        pull(TokenType.LBRACE);

        List<FieldNode> fields = new ArrayList<>();
        while (peek() != TokenType.RBRACE) {
            Token type = pull(TokenType.IDENTIFIER);
            Token name = pull(TokenType.IDENTIFIER);
            pull(TokenType.SEMICOLON);
            fields.add(new FieldNode(type, name));
        }

        pull(TokenType.RBRACE);
        return fields;
    }

    public List<ParameterNode> parseParameters() {
        pull(TokenType.LPAREN);
        List<ParameterNode> params = new ArrayList<>();
        while (peek() != TokenType.RPAREN) {
            Token type = pull(TokenType.IDENTIFIER);
            Token name = pull(TokenType.IDENTIFIER);
            pullif(TokenType.COMMA);
            params.add(new ParameterNode(type, name));
        }

        pull(TokenType.RPAREN);
        return params;
    }

    public BranchNode parseBranch() {
        pull(TokenType.BRANCH);
        List<RamificationNode> ramifications = new ArrayList<>();
        while (peek() == TokenType.LBRACKET) {
            pull(TokenType.LBRACKET);
            ASTNode condition = null;
            if (peek() == TokenType.ELSE) {
                pull(TokenType.ELSE);
            }
            else {
                condition = parseExpr(0);
            }

            pull(TokenType.RBRACKET);
            List<ASTNode> block = parseBlock();
            ramifications.add(new RamificationNode(condition, block));
        }
        return new BranchNode(ramifications);
    }

    public HopsNode parseHops() {
        pull(TokenType.TO);
        ASTNode receptor = parseExpr(0);
        pull(TokenType.HOPS);
        ASTNode stimulus = parseExpr(0);
        pull(TokenType.SEMICOLON);
        return new HopsNode(receptor, stimulus);
    }

    public WhileNode parseWhile() {
        pull(TokenType.WHILE);
        pull(TokenType.LBRACKET);
        ASTNode condition = parseExpr(0);
        pull(TokenType.RBRACKET);

        List<ASTNode> block = parseBlock();
        return new WhileNode(condition, block);
    }

    public VarDeclNode parseVarDecl() {
        Token type = pull(TokenType.IDENTIFIER);
        Token name = pull(TokenType.IDENTIFIER);

        ASTNode init = null;
        if (peek() == TokenType.ASSIGN) {
            pull(TokenType.ASSIGN);
            init = parseExpr(0);
        }

        pull(TokenType.SEMICOLON);
        return new VarDeclNode(type, name, init);
    }

    public List<ASTNode> parseBlock() {
        pull(TokenType.LBRACE);

        List<ASTNode> statements = new ArrayList<>();
        while (peek() != TokenType.RBRACE) {
            switch (peek()) {
                case TO -> statements.add(parseHops());
                case BRANCH -> statements.add(parseBranch());
                case WHILE -> statements.add(parseWhile());
                case IDENTIFIER -> {
                    if (peek(1) == TokenType.IDENTIFIER) {
                        statements.add(parseVarDecl());
                    }
                    else {
                        ASTNode expr = parseExpr(0);
                        pull(TokenType.SEMICOLON);
                        statements.add(expr);
                    }
                }
                default -> {
                    throw new RuntimeException("PARSER: Unexpected statement");
                }
            }
        }

        pull(TokenType.RBRACE);
        return statements;
    }

    public MolderNode parseMolder() {
        pull(TokenType.MOLD);
        List<ParameterNode> params = parseParameters();
        List<ASTNode> block = parseBlock();
        return new MolderNode(params, block);
    }

    public ReactionNode parseReaction() {
        pull(TokenType.UPON);
        Token name = pull(TokenType.IDENTIFIER);
        List<ParameterNode> params = parseParameters();
        List<ASTNode> block = parseBlock();
        return new ReactionNode(name, params, block);
    }

    public ReceptorNode parseReceptor() {
        pull(TokenType.RECEPTOR);
        Token name = pull(TokenType.IDENTIFIER);
        pull(TokenType.LBRACE);

        List<FieldNode> fields = parseStructure();
        MolderNode molder = parseMolder();

        List<ReactionNode> reactions = new ArrayList<>();

        while (peek() == TokenType.UPON) {
            reactions.add(parseReaction());
        }

        pull(TokenType.RBRACE);
        return new ReceptorNode(name, fields, molder, reactions);
    }

    public ClassNode parseClass() {
        return new ClassNode(null, null, null, null);
    }

    public RootNode parse(List<Token> tokens) {
        this.index = 0;
        this.tokens = tokens;
        List<ASTNode> nodes = new ArrayList<>();

        while (index < tokens.size()) {
            switch (peek()) {
                case RECEPTOR -> nodes.add(parseReceptor());
                case CLASS -> nodes.add(parseClass());
            }
        }

        return new RootNode(nodes);
    }
}
