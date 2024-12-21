package edu.tongji.compiler.backend.utils.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Lexer {
    private final DFA dfa;
    private final DFAState startState;
    private List<Token> tokens;
    private final String code;
    private int index;                            //  读到什么位置

    public Lexer(DFA dfa, String code) {
        this.dfa = dfa;
        this.startState = dfa.getStartState();
        this.tokens = new ArrayList<>();
        this.code = code;
        this.index = 0;
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
        return code.charAt(index++);
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
            char c1 = getNextNotWhitespaceChar();
            currentContent.append(c1);
            nextContent.append(c1);
            nextState = getNextState(currentContent);
            char c = getNextChar();
            nextContent.append(c);
            while(getNextState(nextContent) != null){
                currentContent.append(c);
                nextState = getNextState(currentContent);
                c = getNextChar();
                nextContent.append(c);
            }
            index--;
            tokens.add(new Token(nextState.getTokenName(), currentContent.toString()));
            currentContent.setLength(0);
            nextContent.setLength(0);
        }

        return tokens;
    }
}
