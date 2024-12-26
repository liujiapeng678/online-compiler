package edu.tongji.compiler.backend.service.impl;

import edu.tongji.compiler.backend.service.ProcessLexicalRulesService;
import edu.tongji.compiler.backend.utils.lexer.*;
import edu.tongji.compiler.backend.utils.parser.Parser;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.Pattern;

//输入为用户输入的词法规则，输出为词法分析器代码，
@Service
public class ProcessLexicalRulesServiceImpl implements ProcessLexicalRulesService {
    @Override
    public String processLexicalRules(String lexicalRules, String sourceCode) {
        // 1. 解析词法规则
        List<LexicalRule> rules = parseLexicalRules(lexicalRules);
        // 2. 生成DFA
        DFA dfa = generateDFA(rules);
        // 3. 生成词法分析器
        Lexer lexer = new Lexer(dfa, sourceCode);
        Parser.setLexer(lexer);
        List<Token> tokens = lexer.tokenize();
        System.out.println(tokens);
        return lexer.getErrorMessage();
    }

    private List<LexicalRule> parseLexicalRules(String rules) {
        List<LexicalRule> lexicalRules = new ArrayList<>();
        String[] lines = rules.split("\n");
        for (String line : lines) {
            line = line.trim();
            // 解析规则定义，格式：tokenName: pattern
            String[] parts = line.split("->");
            String tokenName = parts[0].replaceAll("\\s+", "");
            String pattern = parts[1].replaceAll("\\s+", "");
            lexicalRules.add(new LexicalRule(tokenName, pattern));
        }
        return lexicalRules;
    }
    private DFA generateDFA(List<LexicalRule> rules) {
        DFA dfa = new DFA();
        int stateId = 0;
        // 创建总的起始状态
        DFAState startState = new DFAState(stateId++);//0
        dfa.setStartState(startState);
        for(int i = 0; i < rules.size(); i++) {
            LexicalRule rule = rules.get(i);
            Pattern pattern = Pattern.compile(rule.getPattern());
            DFAState dfaState = new DFAState(stateId++);
            dfaState.setAccepting(true);
            dfaState.setTokenName(rule.getTokenName());
            dfaState.setPriority(i);
            startState.getTransitions().put(pattern, dfaState);
            dfa.getStates().add(dfaState);
            dfa.getAcceptingStates().add(dfaState);
        }
        return dfa;
    }
}
