package edu.tongji.compiler.backend.utils.parser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tongji.compiler.backend.utils.lexer.Lexer;
import edu.tongji.compiler.backend.utils.lexer.Token;

public class Parser {
    private static Lexer lexer;
    private static List<Token> tokens;
    private static int currentTokenIndex = 0;
    private static List<String> intermediateCode;
    private static int tempVarCounter = 0;
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

    // 开始语法分析
    public static void parse() {
        program();
        saveIntermediateCode();
    }

    // 处理程序
    private static void program() {
        if (getCurrentToken().getContent().equals("PROGRAM")) {
            advance();
            if (getCurrentToken().getTokenName().equals("Identifiers")) {
                advance();
                block();
            }
        }
    }

    // 处理代码块
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

    // 处理常量声明
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

    // 处理变量声明
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

    // 处理语句
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

    // 处理赋值语句
    private static void handleAssignment() {
        String leftVar = getCurrentToken().getContent();
        advance(); // 跳过标识符
        advance(); // 跳过:=
        String rightValue = expression();
        addIntermediateCode(leftVar + " = " + rightValue);
    }

    // 处理表达式
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

    // 处理项
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

    // 处理因子
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
            result = "";
        }
        return result;
    }

    // 处理IF语句
    private static void handleIfStatement() {
        advance(); // 跳过IF
        String condition = condition();
        String label = "L" + tempVarCounter++;
        addIntermediateCode("if " + condition + " goto " + label);
        
        if (getCurrentToken().getContent().equals("THEN")) {
            advance();
            addIntermediateCode(label + ":");
            statement();
        }
    }

    // 处理WHILE语句
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

    // 处理条件
    private static String condition() {
        String expr1 = expression();
        String operator = getCurrentToken().getContent();
        advance();
        String expr2 = expression();
        String tempVar = generateTempVar();
        addIntermediateCode(tempVar + " = " + expr1 + " " + operator + " " + expr2);
        return tempVar;
    }

    // 保存中间代码到文件
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

