package edu.tongji.compiler.backend.service.impl;

import edu.tongji.compiler.backend.service.ProcessLexicalRulesService;
import org.springframework.stereotype.Service;

//输入为用户输入的词法规则，输出为词法分析器代码，
@Service
public class ProcessLexicalRulesServiceImpl implements ProcessLexicalRulesService {

    @Override
    public String processLexicalRules(String lexicalRules) {
        return "";
    }
}
