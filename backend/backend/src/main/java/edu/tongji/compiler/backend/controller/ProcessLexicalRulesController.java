package edu.tongji.compiler.backend.controller;

import edu.tongji.compiler.backend.service.ProcessLexicalRulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ProcessLexicalRulesController {

    @Autowired
    private ProcessLexicalRulesService processLexicalRulesService;

    @PostMapping("/process/lexical/rules/")
    public String processLexicalRules(@RequestParam Map<String, String> data) {
        String lexicalRules = data.get("lexicalRules");
        String sourceCode = data.get("sourceCode");
        return processLexicalRulesService.processLexicalRules(lexicalRules, sourceCode);
    }
}
