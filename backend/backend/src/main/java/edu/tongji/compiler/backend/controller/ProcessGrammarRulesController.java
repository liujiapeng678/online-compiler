package edu.tongji.compiler.backend.controller;

import edu.tongji.compiler.backend.service.ProcessGrammarRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessGrammarRulesController {

    @Autowired
    private ProcessGrammarRulesService processGrammarRulesService;

    @PostMapping("/process/grammar/rules/")
    public String processGrammarRules(@RequestParam String grammarRules) {
        return processGrammarRulesService.processGrammarRules(grammarRules);
    }
}
