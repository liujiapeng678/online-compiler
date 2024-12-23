package edu.tongji.compiler.backend.utils.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Lexer {
    private final DFA dfa;
    private final DFAState startState;
    private final List<Token> tokens;
    private final String code;
    private int index;                            //  读到什么位置
    private int col;                              //  读到什么位置
    private int row;                              //  读到什么位置
    private final StringBuilder errorMessage;

    public Lexer(DFA dfa, String code) {
        this.dfa = dfa;
        this.startState = dfa.getStartState();
        this.tokens = new ArrayList<>();
        this.code = code;
        this.index = 0;
        this.errorMessage = new StringBuilder("上传成功");
        this.col = 0;
        this.row = 1;
    }
    private DFAState getNextState(StringBuilder content){
        DFAState nextState = null;
        for(Pattern pattern : startState.getTransitions().keySet()){
            if(pattern.matcher(content.toString()).matches()){
                if(nextState == null || nextState.getPriority() > startState.getTransitions().get(pattern).getPriority()){
                    nextState = startState.getTransitions().get(pattern);
                }
            }
        }
        return nextState;
    }
    private char getNextChar(){
        if(index >= code.length()){
            index++;
            return '\n';
        }
        char c = code.charAt(index++);
        col++;
        if(c == '\n'){
            row++;
            col = 0;
        }
        return c;
    }
    private char getNextNotWhitespaceChar(){
        char c = getNextChar();
        while(Character.isWhitespace(c)){
            c = getNextChar();
        }
        return c;
    }
    public List<Token> tokenize() {
        StringBuilder currentContent = new StringBuilder();
        StringBuilder nextContent = new StringBuilder();
        DFAState nextState = null;

        while(index < code.length()){
            //System.out.println(index);
            boolean flag = true;
            char c1 = getNextNotWhitespaceChar();
            currentContent.append(c1);
            nextContent.append(c1);
            nextState = getNextState(currentContent);
            if(nextState == null){
                flag = false;
            }
            char c = getNextChar();
            nextContent.append(c);
            while(getNextState(nextContent) != null){
                currentContent.append(c);
                nextState = getNextState(currentContent);
                c = getNextChar();
//                System.out.println('#');
//                System.out.println(c);
//                System.out.println(row);
//                System.out.println(col);
//                System.out.println('#');
                nextContent.append(c);
            }
            index--;
            col--;
            if(c == '\n'){
                row--;
            }
            if(nextState == null){
                errorMessage.setLength(0);
                errorMessage.append("非法字符 '");
                if(flag){
                    errorMessage.append(c);
                } else {
                    errorMessage.append(c1);
                }
                errorMessage.append('\'');
                errorMessage.append("at row ");
                errorMessage.append(row);
                errorMessage.append(" col ");
                errorMessage.append(col);
                errorMessage.append('.');
                break;
            }
            tokens.add(new Token(nextState.getTokenName(), currentContent.toString()));
            currentContent.setLength(0);
            nextContent.setLength(0);
        }

        return tokens;
    }
    public String getErrorMessage() {
        return errorMessage.toString();
    }
}
