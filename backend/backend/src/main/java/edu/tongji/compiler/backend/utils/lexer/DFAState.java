package edu.tongji.compiler.backend.utils.lexer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class DFAState {
    private int id;
    private Map<Pattern, DFAState> transitions;
    private boolean isAccepting;
    private String tokenName;
    private Integer priority;
    public DFAState(int id) {
        this.id = id;
        this.transitions = new HashMap<>();
        this.isAccepting = false;
        this.tokenName = "";
        this.priority = null;
    }

    public Integer getPriority() {
        return priority;
    }
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Map<Pattern, DFAState> getTransitions() {
        return transitions;
    }
    public void setTransitions(Map<Pattern, DFAState> transitions) {
        this.transitions = transitions;
    }
    public boolean isAccepting() {
        return isAccepting;
    }
    public void setAccepting(boolean accepting) {
        isAccepting = accepting;
    }
    public String getTokenName() {
        return tokenName;
    }
    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }
}