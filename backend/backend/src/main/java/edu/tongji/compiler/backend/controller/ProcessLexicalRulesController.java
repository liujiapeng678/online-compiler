package edu.tongji.compiler.backend.controller;

import edu.tongji.compiler.backend.service.ProcessLexicalRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProcessLexicalRulesController {

    @Autowired
    private ProcessLexicalRulesService processLexicalRulesService;

    @PostMapping("/process/lexical/rules/")
    public String processLexicalRules(@RequestParam String lexicalRules) {
        return processLexicalRulesService.processLexicalRules(lexicalRules);
    }
}
