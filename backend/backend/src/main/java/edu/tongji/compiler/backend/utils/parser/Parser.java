package edu.tongji.compiler.backend.utils.parser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tongji.compiler.backend.utils.lexer.Lexer;
import edu.tongji.compiler.backend.utils.lexer.Token;

/**
 * 编译器的语法分析器
 * 负责将词法单元(Token)序列转换为中间代码
 * 实现了一个简单的递归下降解析器
 */
public class Parser {
    // 词法分析器实例
    private static Lexer lexer;
    // 词法单元列表
    private static List<Token> tokens;
    // 当前处理的词法单元索引
    private static int currentTokenIndex = 0;
    // 存储生成的中间代码
    private static List<String> intermediateCode;
    // 临时变量计数器，用于生成唯一的临时变量名
    private static int tempVarCounter = 0;
    // 符号表，存储变量和常量的信息
    private static Map<String, String> symbolTable;

    public static Lexer setLexer(Lexer newLexer) {
        lexer = newLexer;
        tokens = lexer.tokenize();
        intermediateCode = new ArrayList<>();
        symbolTable = new HashMap<>();
        return lexer;
    }

    public static Lexer getLexer() {
        return lexer;
    }

    // 获取当前token
    private static Token getCurrentToken() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex);
        }
        return null;
    }

    // 移动到下一个token
    private static void advance() {
        currentTokenIndex++;
    }

    // 生成临时变量名
    private static String generateTempVar() {
        return "t" + (tempVarCounter++);
    }

    // 添加中间代码
    private static void addIntermediateCode(String code) {
        intermediateCode.add(code);
    }

    /**
     * 语法分析的入口方法
     * 处理整个程序的语法结构并生成中间代码
     */
    public static void parse() {
        program();
        saveIntermediateCode();
    }

    /**
     * 处理程序的最顶层结构
     * 期望的格式: PROGRAM <标识符> <程序块>
     */
    private static void program() {
        if (getCurrentToken().getContent().equals("PROGRAM")) {
            advance();
            if (getCurrentToken().getTokenName().equals("Identifiers")) {
                advance();
                block();
            }
        }
    }

    /**
     * 处理代码块结构
     * 包含常量声明、变量声明和语句部分
     */
    private static void block() {
        // 处理常量声明
        if (getCurrentToken().getContent().equals("CONST")) {
            advance();
            constDeclaration();
        }
        
        // 处理变量声明
        if (getCurrentToken().getContent().equals("VAR")) {
            advance();
            varDeclaration();
        }

        // 处理语句
        statement();
    }

    /**
     * 处理常量声明
     * 格式: CONST <标识符>:=<常量>[,<标识符>:=<常量>]...;
     */
    private static void constDeclaration() {
        do {
            if (getCurrentToken().getTokenName().equals("Identifiers")) {
                String constName = getCurrentToken().getContent();
                advance();
                if (getCurrentToken().getContent().equals(":=")) {
                    advance();
                    if (getCurrentToken().getTokenName().equals("Constants")) {
                        symbolTable.put(constName, getCurrentToken().getContent());
                        advance();
                    }
                }
            }
            if (getCurrentToken().getContent().equals(",")) {
                advance();
            }
        } while (!getCurrentToken().getContent().equals(";"));
        advance();
    }

    /**
     * 处理变量声明
     * 格式: VAR <标识符>[,<标识符>]...;
     */
    private static void varDeclaration() {
        do {
            if (getCurrentToken().getTokenName().equals("Identifiers")) {
                symbolTable.put(getCurrentToken().getContent(), "variable");
                advance();
            }
            if (getCurrentToken().getContent().equals(",")) {
                advance();
            }
        } while (!getCurrentToken().getContent().equals(";"));
        advance();
    }

    /**
     * 处理语句
     * 包括复合语句(BEGIN...END)、IF语句、WHILE语句和赋值语句
     */
    private static void statement() {
        Token currentToken = getCurrentToken();
        
        if (currentToken.getContent().equals("BEGIN")) {
            advance();
            while (!getCurrentToken().getContent().equals("END")) {
                statement();
                if (getCurrentToken().getContent().equals(";")) {
                    advance();
                }
            }
            advance();
        } else if (currentToken.getContent().equals("IF")) {
            handleIfStatement();
        } else if (currentToken.getContent().equals("WHILE")) {
            handleWhileStatement();
        } else if (currentToken.getTokenName().equals("Identifiers")) {
            handleAssignment();
        }
    }

    /**
     * 处理赋值语句
     * 格式: <标识符>:=<表达式>
     */
    private static void handleAssignment() {
        String leftVar = getCurrentToken().getContent();
        advance(); // 跳过标识符
        advance(); // 跳过:=
        String rightValue = expression();
        addIntermediateCode(leftVar + " = " + rightValue);
    }

    /**
     * 处理表达式
     * 处理加法和减法运算
     * @return 表达式的结果（可能是临时变量）
     */
    private static String expression() {
        String term1 = term();
        while (getCurrentToken() != null && 
              (getCurrentToken().getContent().equals("+") || getCurrentToken().getContent().equals("-"))) {
            String operator = getCurrentToken().getContent();
            advance();
            String term2 = term();
            String tempVar = generateTempVar();
            addIntermediateCode(tempVar + " = " + term1 + " " + operator + " " + term2);
            term1 = tempVar;
        }
        return term1;
    }

    /**
     * 处理项
     * 处理乘法和除法运算
     * @return 项的结果（可能是临时变量）
     */
    private static String term() {
        String factor1 = factor();
        while (getCurrentToken() != null && 
              (getCurrentToken().getContent().equals("*") || getCurrentToken().getContent().equals("/"))) {
            String operator = getCurrentToken().getContent();
            advance();
            String factor2 = factor();
            String tempVar = generateTempVar();
            addIntermediateCode(tempVar + " = " + factor1 + " " + operator + " " + factor2);
            factor1 = tempVar;
        }
        return factor1;
    }

    /**
     * 处理因子
     * 可以是常量、变量或括号表达式
     * @return 因子的值或对应的变量名
     */
    private static String factor() {
        Token currentToken = getCurrentToken();
        String result;
        
        if (currentToken.getTokenName().equals("Constants")) {
            result = currentToken.getContent();
            advance();
        } else if (currentToken.getTokenName().equals("Identifiers")) {
            result = currentToken.getContent();
            advance();
        } else if (currentToken.getContent().equals("(")) {
            advance();
            result = expression();
            advance(); // 跳过)
        } else {
            throw new RuntimeException("语法错误：因子必须是常量、变量或括号表达式，当前token为: " + currentToken.getContent());
        }
        return result;
    }

    /**
     * 处理IF语句
     * 格式: IF <条件> THEN <语句>
     */
    private static void handleIfStatement() {
        advance(); // 跳过IF
        String condition = condition();
        String trueLabel = "L" + tempVarCounter++;
        String endLabel = "L" + tempVarCounter++;
        
        addIntermediateCode("if " + condition + " goto " + trueLabel);
        
        if (getCurrentToken().getContent().equals("THEN")) {
            advance();
            addIntermediateCode(trueLabel + ":");
            statement();
            
            // 处理ELSE分支
            if (getCurrentToken().getContent().equals("ELSE")) {
                advance();
                addIntermediateCode("goto " + endLabel);
                statement();
                addIntermediateCode(endLabel + ":");
            }
        }
    }

    /**
     * 处理WHILE语句
     * 格式: WHILE <条件> DO <语句>
     */
    private static void handleWhileStatement() {
        advance(); // 跳过WHILE
        String startLabel = "L" + tempVarCounter++;
        String endLabel = "L" + tempVarCounter++;
        addIntermediateCode(startLabel + ":");
        
        String condition = condition();
        addIntermediateCode("if not " + condition + " goto " + endLabel);
        
        if (getCurrentToken().getContent().equals("DO")) {
            advance();
            statement();
            addIntermediateCode("goto " + startLabel);
            addIntermediateCode(endLabel + ":");
        }
    }

    /**
     * 处理条件表达式
     * 格式: <表达式> <关系运算符> <表达式>
     * @return 条件判断的结果（临时变量）
     */
    private static String condition() {
        String expr1 = expression();
        String operator = getCurrentToken().getContent();
        advance();
        String expr2 = expression();
        String tempVar = generateTempVar();
        addIntermediateCode(tempVar + " = " + expr1 + " " + operator + " " + expr2);
        return tempVar;
    }

    /**
     * 将生成的中间代码保存到文件
     * 文件名为 intermediate_code.txt
     */
    private static void saveIntermediateCode() {
        try (PrintWriter writer = new PrintWriter("intermediate_code.txt")) {
            for (String code : intermediateCode) {
                writer.println(code);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}

