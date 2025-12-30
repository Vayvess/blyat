from ast_nodes import *
from tokens import Token
from tokens import TokenType


class Parser:
    PRECEDENCE = {
        # ASSIGNEMENTS
        TokenType.ASSIGN: 1,
        TokenType.PLUS_ASSIGN: 1,
        TokenType.MINUS_ASSIGN: 1,
        TokenType.MUL_ASSIGN: 1,
        TokenType.DIV_ASSIGN: 1,
        TokenType.MOD_ASSIGN: 1,

        # EQUALITY
        TokenType.EQ: 3, 
        TokenType.NEQ: 3,

        # COMPARISON
        TokenType.LT: 4,
        TokenType.LTE: 4,
        TokenType.GT: 4,
        TokenType.GTE: 4,

        # MATHS
        TokenType.PLUS: 5,
        TokenType.MINUS: 5,
        TokenType.STAR: 6,
        TokenType.SLASH: 6,
        TokenType.MODULO: 6
    }

    PREFIX_UNARY = {
        TokenType.NOT,
        TokenType.MINUS,
        TokenType.PLUS,
        TokenType.INCR,
        TokenType.DECR
    }

    POSTFIX_UNARY = {
        TokenType.INCR,
        TokenType.DECR,
    }

    ASSIGNATORS = {
        TokenType.ASSIGN,
        TokenType.PLUS_ASSIGN,
        TokenType.MINUS_ASSIGN,
        TokenType.MUL_ASSIGN,
        TokenType.DIV_ASSIGN,
        TokenType.MOD_ASSIGN,
    }

    def __init__(self):
        self.idx = 0
        self.tokens = None
    
    def peek(self, n=0):
        k = self.idx + n
        if k < len(self.tokens):
            return self.tokens[k].type
        
        raise RuntimeError("PARSER: peek unexpected end")
    
    def pull(self, token_type):
        if self.idx < len(self.tokens):
            token = self.tokens[self.idx]
            if token.type == token_type:
                self.idx += 1
                return token
            
            self.show_around()
            raise RuntimeError(f"PARSER: pull mismatch: expected {token_type.name} but pulled {token.type.name}")
        
        raise RuntimeError("PARSER: pull unexpected end")

    def pullif(self, token_type):
        if self.idx < len(self.tokens):
            token = self.tokens[self.idx]
            if token.type == token_type:
                self.idx += 1
                return token
        return None
    
    def show_around(self):
        a = self.idx - 1
        b = self.idx
        c = self.idx + 1
        print(
            self.tokens[a],
            self.tokens[b],
            self.tokens[c]
        )
    
    def parse_fcall(self, node):
        args = []
        self.pull(TokenType.LPAREN)
        while self.peek() != TokenType.RPAREN:
            args.append(self.parse_expr())
            self.pullif(TokenType.COMMA)
        
        self.pull(TokenType.RPAREN)
        return FunCallNode(node, args)
    
    def parse_msgexpr(self):
        args = []
        self.pull(TokenType.GT)
        label = self.pull(TokenType.IDENT)

        self.pull(TokenType.LPAREN)
        while self.peek() != TokenType.RPAREN:
            args.append(self.parse_expr())
            self.pullif(TokenType.COMMA)
        
        self.pull(TokenType.RPAREN)
        return MessageNode(label, args)
    
    def parse_primary(self):
        token_type = self.peek()

        # === MESSAGE ===
        if token_type == TokenType.GT:
            return self.parse_msgexpr()

        # === UNARY ===
        if token_type in Parser.PREFIX_UNARY:
            op = self.pull(token_type)
            operand = self.parse_primary()
            return UnaryExprNode(op, operand, True)
        
        # === LITERALS ===
        if token_type == TokenType.LIT_INTEGER:
            return IntLiteralNode(self.pull(token_type))
        
        if token_type == TokenType.LIT_FLOAT:
            return FloatLiteralNode(self.pull(token_type))
        
        if token_type == TokenType.LIT_STRING:
            return StringLiteralNode(self.pull(token_type))
        
        if token_type == TokenType.LIT_BOOL:
            return BoolLiteralNode(self.pull(token_type))
        
        # === IDENTIFIER ===
        if token_type == TokenType.IDENT:
            return IdentifierNode(self.pull(token_type))
        
        # === PARENTHESIZED EXPRESSION ===
        if token_type == TokenType.LPAREN:
            self.pull(TokenType.LPAREN)
            expr = self.parse_expr()
            self.pull(TokenType.RPAREN)
            return expr
        
        self.show_around()
        raise SyntaxError(f"PARSER: Expected primary but got: {token_type.name}")
    
    def parse_postfix(self, node):
        while True:
            token_type = self.peek()

            if token_type in Parser.POSTFIX_UNARY:
                op = self.pull(token_type)
                node = UnaryExprNode(op, node, False)
            
            elif token_type == TokenType.LPAREN:
                node = self.parse_fcall(node)
            
            elif token_type == TokenType.LBRACKET:
                self.pull(token_type)
                index = self.parse_expr()
                self.pull(token_type.RBRACKET)
                node = IndexNode(node, index)
            
            elif token_type == TokenType.DOT:
                self.pull(token_type)
                name = self.pull(TokenType.IDENT)
                node = FieldAccessNode(node, name)
            else:
                return node
    
    def parse_expr(self, min_prec=0):
        left = self.parse_primary()
        left = self.parse_postfix(left)

        while True:
            token_type = self.peek()
            p = Parser.PRECEDENCE.get(token_type)
            if p is None or p < min_prec:
                break

            op = self.pull(token_type)
            if token_type in Parser.ASSIGNATORS:
                # TODO: CHECK IF L-VALUE
                right = self.parse_expr(p)
                left = AssignmentNode(left, op, right)
            else:
                right = self.parse_expr(p + 1)
                left = BinExprNode(left, op, right)
        
        return left
    
    def parse_branch(self):
        branches = []
        self.pull(TokenType.BRANCH)
        while self.peek() == TokenType.LBRACKET:
            self.pull(TokenType.LBRACKET)
            condition = None
            if self.peek() != TokenType.ELSE:
                condition = self.parse_expr()
            self.pull(TokenType.RBRACKET)

            body = self.parse_body()
            branches.append((condition, body))
        
        return BranchNode(branches)
    
    def parse_cast(self):
        self.pull(TokenType.TO)
        dest = self.parse_expr()
        self.pull(TokenType.CAST)
        message = self.parse_expr()
        self.pull(TokenType.SEMICOLON)
        return CastNode(dest, message)
    
    def parse_while(self):
        self.pull(TokenType.WHILE)
        self.pull(TokenType.LBRACKET)
        condition = self.parse_expr()
        self.pull(TokenType.RBRACKET)

        body = self.parse_body()
        return WhileNode(condition, body)
    
    def parse_return(self):
        self.pull(TokenType.RETURN)
        expr = self.parse_expr()
        self.pull(TokenType.SEMICOLON)
        return ReturnNode(expr)
    
    def is_vardecl(self):
        return (
            self.peek() == TokenType.IDENT and
            self.peek(1) == TokenType.IDENT
        )
    
    def parse_vardecl(self):
        var_type = self.pull(TokenType.IDENT)
        var_name = self.pull(TokenType.IDENT)

        init = None
        if self.peek() == TokenType.ASSIGN:
            self.pull(TokenType.ASSIGN)
            init = self.parse_expr()
        
        self.pull(TokenType.SEMICOLON)
        return VarDeclNode(var_type, var_name, init)
    
    def parse_body(self):
        statements = []
        self.pull(TokenType.LBRACE)
        
        token_type = self.peek()
        while token_type != TokenType.RBRACE:
            if token_type == TokenType.BRANCH:
                statements.append(self.parse_branch())
            
            elif token_type == TokenType.TO:
                statements.append(self.parse_cast())
            
            elif token_type == TokenType.WHILE:
                statements.append(self.parse_while())
            
            elif token_type == TokenType.RETURN:
                statements.append(self.parse_return())
            
            elif self.is_vardecl():
                statements.append(self.parse_vardecl())
            
            else:
                expr = self.parse_expr()
                self.pull(TokenType.SEMICOLON)
                statements.append(ExprStmtNode(expr))
            
            # PARSE ASSIGNMENT
            token_type = self.peek()
        
        self.pull(TokenType.RBRACE)
        return statements
    
    # DONE
    def parse_structure(self):
        self.pull(TokenType.STRUCTURE)
        self.pull(TokenType.LBRACE)

        struct = []
        while self.peek() != TokenType.RBRACE:
            field_type = self.pull(TokenType.IDENT)
            field_name = self.pull(TokenType.IDENT)
            self.pull(TokenType.SEMICOLON)
            struct.append((field_type, field_name))
        
        self.pull(TokenType.RBRACE)
        return struct
    
    # DONE
    def parse_molder(self):
        self.pull(TokenType.MOLD)
        name = self.pull(TokenType.IDENT)

        params = []
        self.pull(TokenType.LPAREN)
        while self.peek() != TokenType.RPAREN:
            params.append((
                self.pull(TokenType.IDENT),
                self.pull(TokenType.IDENT)
            ))
            self.pullif(TokenType.COMMA)
        self.pull(TokenType.RPAREN)

        body = self.parse_body()
        return MolderNode(name, params, body)
    
    def parse_behavior(self):
        self.pull(TokenType.UPON)
        name = self.pull(TokenType.IDENT)

        params = []
        self.pull(TokenType.LPAREN)
        while self.peek() != TokenType.RPAREN:
            params.append((
                self.pull(TokenType.IDENT),
                self.pull(TokenType.IDENT)
            ))
            self.pullif(TokenType.COMMA)
        self.pull(TokenType.RPAREN)
        
        body = self.parse_body()
        return BehaviorNode(name, params, body)
    
    def parse_receptor(self):
        self.pull(TokenType.RECEPTOR)
        name = self.pull(TokenType.IDENT)
        self.pull(TokenType.LBRACE)

        structure = self.parse_structure()

        molders = []
        behaviors = []
        curr = self.peek()
        while curr != TokenType.RBRACE:
            if curr == TokenType.MOLD:
                molders.append(self.parse_molder())
            elif curr == TokenType.UPON:
                behaviors.append(self.parse_behavior())
            else:
                raise SyntaxError(f"PARSER: error while parsing receptor")
            
            curr = self.peek()
        
        self.pull(TokenType.RBRACE)
        return ReceptorNode(name, structure, molders, behaviors)
    
    def parse(self, tokens):
        self.idx = 0
        self.tokens = tokens

        root = RootNode()
        while self.idx < len(tokens):
            token_type = self.peek()

            if token_type == TokenType.RECEPTOR:
                root.add(self.parse_receptor())
            
        return root
