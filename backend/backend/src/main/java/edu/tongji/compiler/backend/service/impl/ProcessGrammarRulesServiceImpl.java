package edu.tongji.compiler.backend.service.impl;

import edu.tongji.compiler.backend.service.ProcessGrammarRulesService;
import edu.tongji.compiler.backend.utils.parser.Parser;
import org.springframework.stereotype.Service;

@Service
public class ProcessGrammarRulesServiceImpl implements ProcessGrammarRulesService {

    @Override
    public String processGrammarRules(String grammarRules) {
        //System.out.println(Parser.getLexer());
        //System.out.println(Parser.getLexer().getTokens());
        return "";
    }
}
