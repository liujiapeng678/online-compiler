package edu.tongji.compiler.backend.utils.lexer;

public class Token {
    String tokenName;
    String content;

    public Token(String tokenName, String content) {
        this.tokenName = tokenName;
        this.content = content;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTokenName() {
        return tokenName;
    }
    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    @Override
    public String toString() {
        return tokenName + ":" + content + '\n';
    }
}
