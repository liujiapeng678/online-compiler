package edu.tongji.compiler.backend.utils.lexer;

import java.util.HashSet;
import java.util.Set;

public class DFA {
    private DFAState startState;
    private Set<DFAState> states;
    private Set<DFAState> acceptingStates;


    public DFA() {
        this.startState = null;
        this.states = new HashSet<>();
        this.acceptingStates = new HashSet<>();
    }
    public DFAState getStartState() {
        return startState;
    }
    public void setStartState(DFAState startState) {
        this.startState = startState;
    }
    public Set<DFAState> getStates() {
        return states;
    }
    public void setStates(Set<DFAState> states) {
        this.states = states;
    }
    public Set<DFAState> getAcceptingStates() {
        return acceptingStates;
    }
    public void setAcceptingStates(Set<DFAState> acceptingStates) {
        this.acceptingStates = acceptingStates;
    }
}