package lexer;

import java.util.*;

public class Token {
    private int lineNumber;
    private int tokenNumber;
    private Object attribute;

    // 所有的Token对应于Terminals
    public static Map<String, Integer> TOKEN_STRING_2_NUMBER = new HashMap<>();
    public static Map<Integer, String> TOKEN_NUMBER_2_STRING = new HashMap<>();
    public static Set<String> TERMINAL_STRING;
    public static Set<Integer> TERMINAL_INTEGER;

    public static ArrayList<String> KEYWORDS = new ArrayList<>(Arrays.asList(
            "bool", "char", "int", "float", "double", "struct", "if", "else",
            "do", "while", "call", "function", "return", "true", "false"));

    public static ArrayList<Character> DELIMITERS = new ArrayList<>(Arrays.asList(
            '(', ')', '[', ']', '{', '}', ';', ',', '='
    ));

    public static ArrayList<String> OPERATIONS = new ArrayList<>(Arrays.asList(
            "+", "-", "*", "/", "%",
            ">", ">=", "<", "<=", "==", "!=",
            "&&", "||", "!", "&", "|", "^", "~"
    ));

    // 静态代码块，填充TOKEN_STRING_2_NUMBER和TOKEN_NUMBER_2_STRING
    static {
//        TOKEN_STRING_2_NUMBER.put("$", -1);
//        TOKEN_NUMBER_2_STRING.put(-1, "$");
        //标识符
        TOKEN_STRING_2_NUMBER.put("ID", 0);
        TOKEN_NUMBER_2_STRING.put(0, "ID");
        //常数
        TOKEN_STRING_2_NUMBER.put("INTNUM", 1);
        TOKEN_NUMBER_2_STRING.put(1, "INTNUM");
        TOKEN_STRING_2_NUMBER.put("REALNUM", 2);
        TOKEN_NUMBER_2_STRING.put(2, "REALNUM");
        TOKEN_STRING_2_NUMBER.put("STRING", 3);
        TOKEN_NUMBER_2_STRING.put(3, "STRING");
        for (int i = 0; i < DELIMITERS.size(); i++) {
            TOKEN_STRING_2_NUMBER.put(DELIMITERS.get(i).toString(), i + 4);
            TOKEN_NUMBER_2_STRING.put(i + 4, DELIMITERS.get(i).toString());
        }
        for (int i = 0; i < KEYWORDS.size(); i++) {
            TOKEN_STRING_2_NUMBER.put(KEYWORDS.get(i), i + 20);
            TOKEN_NUMBER_2_STRING.put(i + 20, KEYWORDS.get(i));
        }
        for (int i = 0; i < OPERATIONS.size(); i++) {
            TOKEN_STRING_2_NUMBER.put(OPERATIONS.get(i), i + 40);
            TOKEN_NUMBER_2_STRING.put(i + 40, OPERATIONS.get(i));
        }

        TERMINAL_STRING = TOKEN_STRING_2_NUMBER.keySet();
        TERMINAL_INTEGER = TOKEN_NUMBER_2_STRING.keySet();
    }

    public Token(int lineNumber, int tokenNumber, Object attribute) {
        this.lineNumber = lineNumber;
        this.tokenNumber = tokenNumber;
        this.attribute = attribute;
    }

    public static void main(String[] args) {
        for (Integer key : TOKEN_NUMBER_2_STRING.keySet()) {
            System.out.print(key + ":" + TOKEN_NUMBER_2_STRING.get(key) + " ");
        }
//        System.out.println(Double.parseDouble(".1415926"));
//        System.out.println(Integer.parseInt("012",8));
//        System.out.println(Integer.parseInt("a", 16));
    }

    @Override
    public String toString() {
        return "<Token:" + lineNumber + "," + TOKEN_NUMBER_2_STRING.get(tokenNumber) + "," + attribute + ">";
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(int tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public String getTokenClass() {
        return TOKEN_NUMBER_2_STRING.get(tokenNumber);
    }

    public Object getAttribute() {
        return attribute;
    }

    public void setAttribute(Object attribute) {
        this.attribute = attribute;
    }


}
