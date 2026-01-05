import re

from tokens import Token
from tokens import TokenType


class Lexer:
    def __init__(self, token_specs):
        self.rules = []
        for regex, token_type in token_specs:
            pattern = re.compile(regex)
            self.rules.append((pattern, token_type))
    
    def lex(self, s):
        tokens = []
        line = 0
        pos, limit = 0, len(s)

        while pos < limit:
            best_match = ''
            best_type = None

            for pattern, token_type in self.rules:
                matched = pattern.match(s, pos)
                if matched is None:
                    continue

                value = matched.group(0)
                if len(value) > len(best_match):
                    best_match = value
                    best_type = token_type
            
            if best_type is None:
                raise RuntimeError(f"LEXER: not match at line: {line}")
            
            if best_type == TokenType.NEWLINE:
                line += 1
            elif best_type != TokenType.TOSKIP:
                tokens.append(Token(best_match, best_type))
            
            pos += len(best_match)

        return tokens
