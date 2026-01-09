import ast.RootNode;
import lexer.Lexer;
import lexer.Token;
import parser.Parser;
import transpiler.Transpiler;

String get_source() throws IOException {
    Path fpath = Paths.get("./samples/creature.blyat");
    return Files.readString(fpath);
}

void main() throws IOException {
    String source = get_source();

    Lexer lexer = new Lexer();
    List<Token> tokens = lexer.lex(source);

    Parser parser = new Parser();
    RootNode root = parser.parse(tokens);

    Transpiler transpiler = new Transpiler();
    transpiler.transpile(root);
}
