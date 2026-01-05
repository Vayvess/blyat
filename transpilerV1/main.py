import sys
import argparse

from tokens import TOKEN_SPECS
from lexer import Lexer
from parser import Parser
from printer import Printer
from transpiler import Transpiler

def get_source():
    filename = "./samples/sample.blyat"
    try:    
        with open(filename, "r", encoding="utf-8") as f:
            return f.read()
    except OSError as err:
        print(f"Error reading {filename}: {err}")
        sys.exit(1)

def main():
    source = get_source()
    lexer = Lexer(TOKEN_SPECS)
    tokens = lexer.lex(source)

    for token in tokens:
        print(token)
    
    # parser = Parser()
    # ast = parser.parse(tokens)

    # printer = Printer()
    # printer.print(ast)

    # transpiler = Transpiler()
    # transpiler.transpile(ast)

if __name__ == '__main__':
    main()