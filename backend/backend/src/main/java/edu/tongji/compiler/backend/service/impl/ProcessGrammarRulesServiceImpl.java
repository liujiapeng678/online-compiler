package edu.tongji.compiler.backend.service.impl;

import org.springframework.stereotype.Service;

import edu.tongji.compiler.backend.service.ProcessGrammarRulesService;
import edu.tongji.compiler.backend.utils.Parser.Parser;

@Service
public class ProcessGrammarRulesServiceImpl implements ProcessGrammarRulesService {

    @Override
    public String processGrammarRules(String grammarRules) {
        try {
            // 确保Lexer已经设置
            if (Parser.getLexer() == null) {
                return "错误：词法分析器未初始化";
            }
            
            // 开始语法分析和中间代码生成
            Parser.parse();
            
            // 返回成功信息
            return "语法分析完成，中间代码已生成到intermediate_code.txt文件中";
        } catch (Exception e) {
            // 如果发生错误，返回错误信息
            return "语法分析错误: " + e.getMessage();
        }
    }
}
