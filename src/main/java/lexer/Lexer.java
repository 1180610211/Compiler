package lexer;

import parser.LR1Generator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Lexer {
    private BufferedReader bufferedReader;  //输入缓冲区
    private final ArrayList<Token> tokenList = new ArrayList<>();   //词法分析得到的Token序列
    private final ArrayList<String> errorList = new ArrayList<>();  //词法分析检测出的错误
    private int lineNumber;     //当前的行号
    private int x;              //bufferedReader读入的字符的整数值,x=-1表示输入结束
    private char peek;          //当前要解析的字符

    public Lexer(String path) {
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            lineNumber = 1;
            run();

            System.out.println("==========tokenList==========");
            for (Token token : tokenList) {
                System.out.println(token);
            }
            System.out.println("==========tokenList==========");
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在");
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            char lookahead;     //超前搜索的一个字符
            x = bufferedReader.read();
            peek = (char) x;

            while (x != -1) {

                if (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r') {
                    //  处理空白符号
                    if (peek == '\n') lineNumber++;
                    x = bufferedReader.read();
                    peek = (char) x;
                } else if (Character.isLetter(peek) || peek == '_') {
                    //  处理标识符和关键字，以字母或下划线开头
                    idAndKeywordDFA();
                } else if (Character.isDigit(peek)) {
                    //  处理数字常量：整型常量，浮点型常量（含科学计数法），以数字开头
                    numberDFA();
                } else if (peek == '"') {
                    //  处理字符串常量，以引号开头
                    stringDFA();
                } else if (needLookAhead(peek)) {
                    //  处理需要超前搜索的符号
                    x = bufferedReader.read();
                    lookahead = (char) x;

                    //  处理两种注释
                    if (peek == '/' && lookahead == '*') commentDFA1();
                    else if (peek == '/' && lookahead == '/') commentDFA2();
                    else {
                        //  处理复合运算符，如>=,!=等
                        String temp = "" + peek + lookahead;
                        if (Token.OPERATIONS.contains(temp)) {
                            tokenList.add(new Token(lineNumber, Token.TOKEN_STRING_2_NUMBER.get(temp), null));
                            x = bufferedReader.read();
                            peek = (char) x;
                        } else {
                            tokenList.add(new Token(lineNumber, Token.TOKEN_STRING_2_NUMBER.get("" + peek), null));
                            peek = lookahead;
                        }
                    }
                } else if (Token.OPERATIONS.contains("" + peek)) {
                    //  处理简单运算符
                    tokenList.add(new Token(lineNumber, Token.TOKEN_STRING_2_NUMBER.get("" + peek), null));
                    x = bufferedReader.read();
                    peek = (char) x;
                } else if (Token.DELIMITERS.contains(peek)) {
                    //  处理定界符
                    tokenList.add(new Token(lineNumber, Token.TOKEN_STRING_2_NUMBER.get("" + peek), null));
                    x = bufferedReader.read();
                    peek = (char) x;
                } else {
                    //  非法字符错误
                    //  恐慌模式：忽略输入，继续进行编译
                    errorList.add("Lexical error at Line " + lineNumber + ":非法字符" + peek);
                    x = bufferedReader.read();
                    peek = (char) x;
                }
            }

            tokenList.add(new Token(lineNumber, LR1Generator.DOLLAR, null));
        } catch (IOException e) {
            System.out.println("读写异常");
            e.printStackTrace();
        }
    }

    //是否为十六进制的数字
    private boolean isHexNumber(char c) {
        return Character.isDigit(c) || c == 'A' || c == 'B' || c == 'C' || c == 'D' || c == 'E' ||
                c == 'a' || c == 'b' || c == 'c' || c == 'd' || c == 'e';
    }

    //是否需要进行超前搜索
    private boolean needLookAhead(char c) {
        return c == '/' || c == '>' || c == '<' || c == '=' || c == '!' || c == '&' || c == '|';
    }

    //识别标识符和关键字的DFA
    private void idAndKeywordDFA() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(peek);
        while ((x = bufferedReader.read()) != -1) {
            peek = (char) x;
            if (Character.isDigit(peek) || Character.isLetter(peek) || peek == '_') {
                builder.append(peek);
            } else {
                break;
            }
        }
        String key = builder.toString();
        if (Token.KEYWORDS.contains(key)) {
            tokenList.add(new Token(lineNumber, Token.TOKEN_STRING_2_NUMBER.get(key), null));
        } else {
            tokenList.add(new Token(lineNumber, Token.TOKEN_STRING_2_NUMBER.get("ID"), key));
        }
    }

    //识别数字常数的DFA
    private void numberDFA() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(peek);

        int state;
        if (peek == '.') state = 1;
        else if (peek == '0') state = 7;
        else state = 6;

        outer:
        while ((x = bufferedReader.read()) != -1) {
            peek = (char) x;
            switch (state) {
                case 1:
                    if (Character.isDigit(peek)) state = 2;
                    builder.append(peek);
                    break;
                case 2:
                    if (peek == 'E' || peek == 'e') state = 3;
                    else if (!Character.isDigit(peek)) break outer;
                    builder.append(peek);
                    break;
                case 3:
                    if (peek == '+' || peek == '-') state = 4;
                    else if (Character.isDigit(peek)) state = 5;
                    builder.append(peek);
                    break;
                case 4:
                    if (Character.isDigit(peek)) state = 5;
                    builder.append(peek);
                    break;
                case 5:
                    if (!Character.isDigit(peek)) break outer;
                    builder.append(peek);
                    break;
                case 6:
                    if (peek == '.') state = 1;
                    else if (peek == 'E' || peek == 'e') state = 3;
                    else if (!Character.isDigit(peek)) break outer;
                    builder.append(peek);
                    break;
                case 7:
                    if (peek == '.') state = 1;
                    else if (peek == 'E' || peek == 'e') state = 3;
                    else if (peek == 'X' || peek == 'x') state = 9;
                    else if (Character.isDigit(peek) && peek != '8' && peek != '9') state = 8;
                    else break outer;
                    builder.append(peek);
                    break;
                case 8:
                    if (!Character.isDigit(peek) || peek == '8' || peek == '9') break outer;
                    builder.append(peek);
                    break;
                case 9:
                    if (isHexNumber(peek)) state = 10;
                    builder.append(peek);
                    break;
                case 10:
                    if (!isHexNumber(peek)) break outer;
                    builder.append(peek);
                    break;
            }
        }

        String number = builder.toString();
        if (state == 6 || state == 7) {
            tokenList.add(new Token(lineNumber, Token.TOKEN_STRING_2_NUMBER.get("INTNUM"), Integer.parseInt(number)));
        } else if (state == 2 || state == 5) {
            tokenList.add(new Token(lineNumber, Token.TOKEN_STRING_2_NUMBER.get("REALNUM"), Double.parseDouble(number)));
        } else if (state == 8) {
            tokenList.add(new Token(lineNumber, Token.TOKEN_STRING_2_NUMBER.get("INTNUM"), Integer.parseInt(number, 8)));
        } else if (state == 10) {
            tokenList.add(new Token(lineNumber, Token.TOKEN_STRING_2_NUMBER.get("INTNUM"), Integer.parseInt(number.substring(2), 16)));
        } else {
            errorList.add("Lexical error at Line " + lineNumber + ":错误数字常数" + number);
        }
    }

    //识别字符串常数的DFA
    private void stringDFA() throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(peek);
        int state = 1;
        outer:
        while ((x = bufferedReader.read()) != -1) {
            peek = (char) x;

            switch (state) {
                case 1:
                    if (peek == '\\') state = 2;
                    if (peek == '"') state = 3;
                    builder.append(peek);
                    break;
                case 2:
                    if (peek == '"') state = 1;
                    builder.append(peek);
                    break;
                case 3:
                    break outer;
            }
        }

        String s = builder.toString();
        if (state == 3) {
            tokenList.add(new Token(lineNumber, Token.TOKEN_STRING_2_NUMBER.get("STRING"), s));
        } else {
            errorList.add("Lexical error at Line " + lineNumber + ":字符串常量未封闭");
        }
    }

    //识别多行注释的DFA
    private void commentDFA1() throws IOException {
        int state = 2;
        outer:
        while ((x = bufferedReader.read()) != -1) {
            peek = (char) x;

            if (peek == '\n') lineNumber++;
            switch (state) {
                case 2:
                    if (peek == '*') state = 3;
                    break;
                case 3:
                    if (peek == '/') state = 4;
                    else if (peek != '*') state = 2;
                    break;
                case 4:
                    break outer;
            }
        }
        if (state != 4) {
            errorList.add("Lexical error at Line " + lineNumber + ":多行注释未封闭");
        }
    }

    //识别单行注释的DFA
    private void commentDFA2() throws IOException {
        while ((x = bufferedReader.read()) != -1) {
            peek = (char) x;
            if (peek == '\n') {
                lineNumber++;
                x = bufferedReader.read();
                peek = (char) x;
                break;
            }
        }
    }

    public ArrayList<Token> getTokenList() {
        return tokenList;
    }

    public ArrayList<String> getErrorList() {
        return errorList;
    }

    public static void main(String[] args) {
        Lexer lexer = new Lexer("src/main/resources/test/syntax_test.c");
    }
}