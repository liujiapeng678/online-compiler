package edu.tongji.compiler.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.tongji.compiler.backend.service.ProcessGrammarRulesService;

@RestController
public class ProcessGrammarRulesController {

    @Autowired
    private ProcessGrammarRulesService processGrammarRulesService;

    @PostMapping("/process/grammar/rules/")
    public String processGrammarRules(@RequestParam String grammarRules) {
        return processGrammarRulesService.processGrammarRules(grammarRules);
    }
}
