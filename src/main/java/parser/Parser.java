package parser;

import lexer.Lexer;
import lexer.Token;

import java.util.*;

// shift-reduce analysis
public class Parser {
    //产生式列表 对应于归约动作中的ri
    private List<Item> productionList;
    //状态->(文法符号->动作)
    //action + goto
    private Map<Integer, Map<Integer, String>> LR1Table;

    //状态栈
    private Stack<Integer> stateStack = new Stack<>();
    //文法符号(树节点)栈
    private Stack<TNode> symbolStack = new Stack<>();
    private static int lineNumber = 1;

    private StringBuilder tree = new StringBuilder();
    private final ArrayList<String> errorList = new ArrayList<>();  //语法分析检测出的错误

    public Parser() {
        LR1Generator lr1Generator = new LR1Generator();
        lr1Generator.loadGrammar("src/main/resources/grammar.txt");
        lr1Generator.calculateFirstSet();
        lr1Generator.calculateFollowSet();
        Set<ItemSet> itemSets = lr1Generator.items();
        System.out.println("size:" + itemSets.size());
        lr1Generator.generateLR1Table(itemSets);

        productionList = lr1Generator.getProductionList();
        LR1Table = lr1Generator.getLR1Table();
    }

    // 移入归约分析 LR
    public TNode analysis(ArrayList<Token> tokenList) {
        stateStack.push(1);
        int i = 0;
        String operation;
        Item production;
        while (i < tokenList.size()) {

            operation = LR1Table.get(stateStack.peek()).get(tokenList.get(i).getTokenNumber());
            if (operation != null) {
//                System.out.println(operation);
                if (operation.contains("s")) {
                    Token token = tokenList.get(i);
                    stateStack.push(Integer.parseInt(operation.substring(1)));
                    symbolStack.push(new TNode(Token.TOKEN_NUMBER_2_STRING.get(token.getTokenNumber())
                            , token.getLineNumber(), token.getAttribute()));
                    i++;
                } else if (operation.contains("r")) {
                    production = productionList.get(Integer.parseInt(operation.substring(1)) - 1);
//                    System.out.println(production);

                    TNode parent = new TNode(LR1Generator.NON_TERMINAL_NUMBER_2_STRING.get(production.getLeft()), 0, null);
                    List<TNode> childrenList = parent.getChildren();
                    for (int j = 0; j < production.getRight().size(); j++) {
                        stateStack.pop();
                        childrenList.add(symbolStack.pop());
                    }

                    stateStack.push(Integer.parseInt(LR1Table.get(stateStack.peek()).get(production.getLeft())));
                    symbolStack.push(parent);

                } else if (operation.equals("acc")) {
                    System.out.println("success!");
                    break;
                }
            } else {
                // TODO 错误处理 恐慌模式
//                System.out.println("error");
                if (tokenList.get(i).getTokenNumber() != LR1Generator.DOLLAR) {
                    errorList.add("Syntax error around Line " + tokenList.get(i).getLineNumber()
                            + " : Please check the syntax before \'" + Token.TOKEN_NUMBER_2_STRING.get(tokenList.get(i).getTokenNumber())
                            + (tokenList.get(i).getAttribute() != null ? " " + tokenList.get(i).getAttribute().toString() : "") + "\' !");
                } else {
                    errorList.add("Syntax error around Line " + tokenList.get(i).getLineNumber()
                            + " : Please check the syntax before the end !");
                }

                outer:
                while (true) { //从栈顶向下扫描
                    int state = stateStack.peek();
                    // goto表可能有多个条目存在转移，选择第一个
                    for (int nonTerminal : LR1Generator.NON_TERMINAL_INTEGER) {
                        if (LR1Table.get(state).get(nonTerminal) != null) {
                            HashSet<Integer> follow = LR1Generator.followMap.get(nonTerminal);
//                            if (follow.contains(Token.TOKEN_STRING_2_NUMBER.get(";")) || follow.contains(Token.TOKEN_STRING_2_NUMBER.get("}"))) {
                            while (true) { // 从输入词法单元向后扫描
                                if (LR1Table.get(Integer.parseInt(LR1Table.get(state).get(nonTerminal))).get(tokenList.get(i).getTokenNumber()) != null) {
                                    stateStack.push(Integer.parseInt(LR1Table.get(state).get(nonTerminal)));
                                    System.out.println("push " + LR1Table.get(state).get(nonTerminal));
                                    break outer;
                                } else {
                                    i++;
                                    if (i == tokenList.size())
                                        break outer;
                                }
                            }
//                            }
                        }
                    }
                    stateStack.pop();
                }

                if (i >= tokenList.size() - 1) break;
            }

//            System.out.println(stateStack);
        }

//        if (LR1Table.get(stateStack.peek()).get(tokenList.get(i).getTokenNumber()).equals("acc")) {
//            System.out.println("success");
//        }

        return symbolStack.pop();
    }

    public void printParseTree(TNode root, int layer) {
        List<TNode> childrenList = root.getChildren();
        for (int j = 0; j < layer * 2; j++) {
            tree.append(" ");
        }
        tree.append(root.getSymbol());
        if (root.getAttribute() != null) {
            tree.append(": " + root.getAttribute());
        }
        if (root.getLineNumber() == 0) {
            tree.append(" (" + lineNumber + ")\n");
        } else {
            tree.append(" (" + root.getLineNumber() + ")\n");
            lineNumber = root.getLineNumber() + 1;
        }
        for (int i = childrenList.size() - 1; i >= 0; i--) {
            printParseTree(childrenList.get(i), layer + 1);
        }
    }

    public ArrayList<String> getErrorList() {
        return errorList;
    }

    public String getTree() {
        return tree.toString();
    }

    public static void main(String[] args) {
        Lexer lexer = new Lexer("src/main/resources/test/syntax_test.c");//syntax_test_error
        ArrayList<Token> tokenList = lexer.getTokenList();
        System.out.println(tokenList.size());

        Parser parser = new Parser();
        TNode root = parser.analysis(tokenList);
        for (int i = 0; i < parser.getErrorList().size(); i++) {
            System.out.println(parser.getErrorList().get(i));
        }
        parser.printParseTree(root, 0);
    }
}