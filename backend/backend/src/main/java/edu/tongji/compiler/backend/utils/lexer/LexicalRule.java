package edu.tongji.compiler.backend.utils.lexer;

public class LexicalRule {
    private String tokenName;        // token名称
    private String pattern;          // 正则表达式



    public String getTokenName() {
        return tokenName;
    }
    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }
    public String getPattern() {
        return pattern;
    }
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    public LexicalRule(String tokenName, String pattern) {
        this.tokenName = tokenName;
        this.pattern = pattern;
    }

    @Override
    public String toString() {
        return tokenName + ":" + pattern;
    }
}