package edu.tongji.compiler.backend.utils.parser;

import edu.tongji.compiler.backend.utils.lexer.Lexer;

public class Parser {
    private static Lexer lexer;


    public static Lexer getLexer() {
        return lexer;
    }
    public static void setLexer(Lexer newLexer) {
        lexer = newLexer;
    }
}
