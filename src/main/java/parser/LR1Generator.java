package parser;

import lexer.Token;

import java.io.*;
import java.util.*;

public class LR1Generator {
    public static Map<String, Integer> NON_TERMINAL_STRING_2_NUMBER = new HashMap<>();
    public static Map<Integer, String> NON_TERMINAL_NUMBER_2_STRING = new HashMap<>();
    public static Set<String> NON_TERMINAL_STRING = new HashSet<>();
    public static Set<Integer> NON_TERMINAL_INTEGER = new HashSet<>();
    public static Set<Integer> GRAMMAR_CHARACTER;
    public static int DOLLAR = -1;

    private static Map<Integer, ArrayList<Item>> productionMap = new HashMap<>();
    private static List<Item> productionList = new ArrayList<>();
    public static Map<Integer, HashSet<Integer>> firstMap = new HashMap<>();
    public static Map<Integer, HashSet<Integer>> followMap = new HashMap<>();
    private static Map<Integer, Boolean> nullableMap = new HashMap<>();

    private static Map<Integer, Map<Integer, String>> LR1Table = new HashMap<>();

    private static int itemSetIndex = 1;

    // 加载文法
    void loadGrammar(String grammarPath) {
        int nonTerminalID = 60;
        int index = 1;
        try {
            BufferedReader br = new BufferedReader(new FileReader(grammarPath));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() == 0) continue;
                // 产生式左右部 分割
                String[] parts = line.split("::=");
                String leftString = parts[0].trim();

                int left;
                if (!NON_TERMINAL_STRING.contains(leftString)) {
                    NON_TERMINAL_STRING.add(leftString);
                    NON_TERMINAL_NUMBER_2_STRING.put(nonTerminalID, leftString);
                    NON_TERMINAL_STRING_2_NUMBER.put(leftString, nonTerminalID);
                    left = nonTerminalID;
                    nonTerminalID++;
                } else {
                    left = NON_TERMINAL_STRING_2_NUMBER.get(leftString);
                }

                // 产生式右边 | 分割
                String[] productions = parts[1].split(" \\| ");
                for (int i = 0; i < productions.length; i++) {
                    String[] fields = productions[i].trim().split(" ");
                    ArrayList<Integer> right = new ArrayList<>();
                    for (int j = 0; j < fields.length; j++) {
                        String symbol = fields[j];
                        if (Token.TERMINAL_STRING.contains(symbol)) {
                            right.add(Token.TOKEN_STRING_2_NUMBER.get(symbol));
                        } else if (NON_TERMINAL_STRING.contains(symbol)) {
                            right.add(NON_TERMINAL_STRING_2_NUMBER.get(symbol));
                        } else if (symbol.equals("ε")) {
                            break;
                        } else {
                            NON_TERMINAL_STRING.add(symbol);
                            NON_TERMINAL_NUMBER_2_STRING.put(nonTerminalID, symbol);
                            NON_TERMINAL_STRING_2_NUMBER.put(symbol, nonTerminalID);
                            right.add(nonTerminalID);
                            nonTerminalID++;
                        }
                    }
                    Item item = new Item(index, 0, left, right, DOLLAR);
                    productionList.add(item);
                    index++;
                    addProduction(item);
                }
                NON_TERMINAL_INTEGER = NON_TERMINAL_NUMBER_2_STRING.keySet();
            }
        } catch (FileNotFoundException e) {
            System.err.println("文件加载失败");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("文件读写失败");
            e.printStackTrace();
        }
    }

    // 将产生式production加入到productionMap
    void addProduction(Item item) {
        ArrayList<Item> itemList = productionMap.get(item.getLeft());
        if (itemList == null) {
            itemList = new ArrayList<>();
            productionMap.put(item.getLeft(), itemList);
            firstMap.put(item.getLeft(), new HashSet<>());
            followMap.put(item.getLeft(), new HashSet<>());
        }
        if (itemList.contains(item) == false) {
            itemList.add(item);
        }
    }

    // 计算First集
    void calculateFirstSet() {
        boolean isChange = true;
        while (isChange) {
            isChange = false;
            for (int left : productionMap.keySet()) {
                ArrayList<Item> items = productionMap.get(left);
                HashSet<Integer> currentFirstSet = firstMap.get(left);
                for (Item item : items) {
                    ArrayList<Integer> right = item.getRight();
                    // 产生式为空
                    if (right.size() == 0) {
                        if (!isNullable(left)) {
                            isChange = true;
                            nullableMap.put(left, true);
                        }
                    } else {
                        int pos = 0;
                        do {
                            int currentSymbol = right.get(pos);
                            if (NON_TERMINAL_INTEGER.contains(currentSymbol)
                                    && !currentFirstSet.containsAll(firstMap.get(currentSymbol))) {
                                currentFirstSet.addAll(firstMap.get(right.get(pos)));
                                isChange = true;
                            }
                            if (Token.TERMINAL_INTEGER.contains(currentSymbol)
                                    && !currentFirstSet.contains(currentSymbol)) {
                                currentFirstSet.add(currentSymbol);
                                isChange = true;
                                break;
                            }
                            if (isNullable(currentSymbol)) pos++;
                            else break;
                        } while (pos < item.getRight().size());
                        if (pos == item.getRight().size()) {
                            if (!isNullable(left)) {
                                isChange = true;
                                nullableMap.put(left, true);
                            }
                        }
                    }
                }
                firstMap.put(left, currentFirstSet);
            }
        }
    }

    // 计算Follow集
    void calculateFollowSet() {
        followMap.put(NON_TERMINAL_STRING_2_NUMBER.get("S"), new HashSet<>(Arrays.asList(DOLLAR)));
        boolean isChange = true;
        while (isChange) {
            isChange = false;
            for (int left : productionMap.keySet()) {
                ArrayList<Item> items = productionMap.get(left);
                for (Item item : items) {
                    ArrayList<Integer> right = item.getRight();
                    for (int i = 0; i < right.size(); i++) {
                        if (NON_TERMINAL_INTEGER.contains(right.get(i))) {
                            ArrayList<Integer> tmp = new ArrayList<>();
                            for (int j = i + 1; j < right.size(); j++) {
                                tmp.add(right.get(j));
                            }
                            ArrayList<Integer> betaFirst = calculateLookAhead(tmp);

                            if (!followMap.get(right.get(i)).containsAll(betaFirst)) {
                                isChange = true;
                                followMap.get(right.get(i)).addAll(betaFirst);
                            }
                            if (isNullable(tmp) && !followMap.get(right.get(i)).containsAll(followMap.get(left))) {
                                isChange = true;
                                followMap.get(right.get(i)).addAll(followMap.get(left));
                            }
                        }
                    }
                }
            }
        }
    }

    // 计算beta+a的lookahead集
    ArrayList<Integer> calculateLookAhead(ArrayList<Integer> list) {
        ArrayList<Integer> lookAhead = new ArrayList<>();
        if (list.size() == 0) {
            return lookAhead;
        }
        int i = 0;
        do {
            if (Token.TERMINAL_INTEGER.contains(list.get(i)) || DOLLAR == list.get(i)) {
                lookAhead.add(list.get(i));
            } else {
                lookAhead.addAll(firstMap.get(list.get(i)));
            }
            if (isNullable(list.get(i))) {
                i++;
            } else {
                break;
            }
        } while (i < list.size());
        return lookAhead;
    }

    // 给定文法符号是否可空
    boolean isNullable(int symbol) {
        Boolean result = nullableMap.get(symbol);
        if (result == null) return false;
        if (result == true) {
            return true;
        } else {
            return false;
        }
    }

    // 给定文法符号串是否可空
    boolean isNullable(ArrayList<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            if (!NON_TERMINAL_INTEGER.contains(list.get(i)) || !isNullable(list.get(i))) {
                return false;
            }
        }
        return true;
    }

    // 计算LR(1)项目的闭包
    ItemSet closure(Set<Item> items) {
        ItemSet itemSet = new ItemSet();
        itemSet.setIndex(itemSetIndex);
        boolean isChange = true;
        while (isChange) {
            isChange = false;
            for (Item item : new HashSet<>(items)) {
                int dotSymbol = item.getDotSymbol();
                if (NON_TERMINAL_INTEGER.contains(dotSymbol)) {
                    for (Item production : productionMap.get(dotSymbol)) {
                        for (int b : calculateLookAhead(item.getBetaAndA())) {
                            Item newItem = new Item(production.getIndex(), 0,
                                    production.getLeft(), production.getRight(), b);
                            if (!items.contains(newItem)) {
                                isChange = true;
                                items.add(newItem);
                            }
                        }
                    }
                }
            }
//            System.out.println(itemSetIndex);
//            System.out.println("closure");
        }
        itemSet.setClosure(items);
        return itemSet;
    }

    // goto函数
    ItemSet gotoFunction(ItemSet itemSet, Integer x) {
        Set<Item> J = new HashSet<>();
        for (Item item : itemSet.getClosure()) {
            if (item.getDotSymbol() == x) {
                J.add(item.dotAdvance());
            }
        }
        return closure(J);
    }

    // 计算LR(1)项集族C
    Set<ItemSet> items() {
        Item startItem = new Item(1, 0, NON_TERMINAL_STRING_2_NUMBER.get("S"),
                new ArrayList<>(Arrays.asList(NON_TERMINAL_STRING_2_NUMBER.get("Program"))), DOLLAR);
        Set<ItemSet> C = new HashSet<>();
        C.add(closure(new HashSet<>(Arrays.asList(startItem))));
        itemSetIndex++;

        boolean isChange = true;
        GRAMMAR_CHARACTER = new HashSet<>(Token.TERMINAL_INTEGER);
        GRAMMAR_CHARACTER.add(DOLLAR);
        GRAMMAR_CHARACTER.addAll(NON_TERMINAL_INTEGER);

        while (isChange) {
            isChange = false;
            Set<ItemSet> addSet = new HashSet<>();
            for (ItemSet itemSet : C) {
                for (Integer x : GRAMMAR_CHARACTER) {
                    ItemSet resultItemSet = gotoFunction(itemSet, x);
                    if (resultItemSet.getClosure().size() != 0) {
                        Set<ItemSet> tempSet = new HashSet<>(C);
                        tempSet.addAll(addSet);

                        // 填每个项目的goto表
                        boolean contains = false;
                        for (ItemSet itemSet2 : tempSet) {
                            if (itemSet2.equals(resultItemSet)) {
                                contains = true;
                                itemSet.getGotoTable().put(x, itemSet2.getIndex());
                                break;
                            }
                        }

                        if (!contains) {
                            isChange = true;
                            addSet.add(resultItemSet);
                            itemSet.getGotoTable().put(x, itemSetIndex);
                            itemSetIndex++;
                        }
                    }
                }
            }
            C.addAll(addSet);
        }
        return C;
    }

    // 比较栈顶符号和lookahead的优先级
    boolean compare(int exp, int lookahead) {
        int ADD = Token.TOKEN_STRING_2_NUMBER.get("+");
        int SUB = Token.TOKEN_STRING_2_NUMBER.get("-");
        int TIMES = Token.TOKEN_STRING_2_NUMBER.get("*");
        int DIV = Token.TOKEN_STRING_2_NUMBER.get("/");

        int AND = Token.TOKEN_STRING_2_NUMBER.get("&&");
        int OR = Token.TOKEN_STRING_2_NUMBER.get("||");
        int NOT = Token.TOKEN_STRING_2_NUMBER.get("!");

        int ELSE = Token.TOKEN_STRING_2_NUMBER.get("else");

        if (exp == TIMES || exp == DIV || exp == NOT || exp == AND) return true;
        else if ((exp == ADD || exp == SUB) && (lookahead == ADD || lookahead == SUB)) return true;
        else if (exp == OR && lookahead == OR) return true;
        else return false;
    }

    // 生成LR1分析表
    void generateLR1Table(Set<ItemSet> itemSets) {
        int conflicts = 0;
        // 结束项目
        Item endItem = new Item(1, 1, NON_TERMINAL_STRING_2_NUMBER.get("S"),
                new ArrayList<>(Arrays.asList(NON_TERMINAL_STRING_2_NUMBER.get("Program"))), DOLLAR);

        boolean shiftable, reducible;
        // 处理二义性文法
        int ADD = Token.TOKEN_STRING_2_NUMBER.get("+");
        int SUB = Token.TOKEN_STRING_2_NUMBER.get("-");
        int TIMES = Token.TOKEN_STRING_2_NUMBER.get("*");
        int DIV = Token.TOKEN_STRING_2_NUMBER.get("/");

        int AND = Token.TOKEN_STRING_2_NUMBER.get("&&");
        int OR = Token.TOKEN_STRING_2_NUMBER.get("||");

        int N = NON_TERMINAL_STRING_2_NUMBER.get("N");
        int ELSE = Token.TOKEN_STRING_2_NUMBER.get("else");

        for (ItemSet itemSet : itemSets) {

            int index = itemSet.getIndex();
            Map<Integer, Integer> gotoTable = itemSet.getGotoTable();
            Set<Item> closure = itemSet.getClosure();
            Map<Integer, String> transition = new HashMap<>();

            shiftable = reducible = true;
            for (Item item : closure) {

                int dotSymbol = item.getDotSymbol();
                int lookahead = item.getLookAhead();
                ArrayList<Integer> right = item.getRight();

                if (item.equals(endItem)) {
                    String s = transition.get(DOLLAR);
                    transition.put(DOLLAR, "acc");
                } else if (item.isReducible()) {
                    if (((right.size() == 3 || right.size() == 4) && compare(right.get(1), lookahead))
                            || (right.size() == 2 && compare(right.get(0), lookahead))
                            || (item.getLeft() == N)) {
                        transition.put(item.getLookAhead(), "r" + item.getIndex());
                        shiftable = false;
                        reducible = false;
                    } else {
                        String s = transition.get(item.getLookAhead());
                        if (s == null) {
                            transition.put(item.getLookAhead(), "r" + item.getIndex());
                        } else if (reducible) {
                            conflicts++;
                            transition.put(item.getLookAhead(), transition.get(item.getLookAhead()) + "\tr" + item.getIndex());
                        }
                    }
                } else if (Token.TERMINAL_INTEGER.contains(dotSymbol)) {
                    if (((right.size() == 3 || right.size() == 4) && (dotSymbol == TIMES || dotSymbol == DIV || dotSymbol == AND))
                            || (right.size() == 10 && dotSymbol == N)) {
                        transition.put(dotSymbol, "s" + gotoTable.get(dotSymbol));
                        reducible = false;
                    } else {
                        String s = transition.get(dotSymbol);
                        if (s == null) {
                            transition.put(dotSymbol, "s" + gotoTable.get(dotSymbol));
                        } else if (!s.contains("s" + gotoTable.get(dotSymbol)) && shiftable) {
                            conflicts++;
                            transition.put(dotSymbol, transition.get(dotSymbol) + "\ts" + gotoTable.get(dotSymbol));
                        }
                    }
                }
            }

            for (int nonTerminal : NON_TERMINAL_INTEGER) {
                if (gotoTable.get(nonTerminal) != null) {
                    transition.put(nonTerminal, String.valueOf(gotoTable.get(nonTerminal)));
                }
            }
//            System.out.println(index + ":" + transition);
            LR1Table.put(index, transition);
        }
        System.out.println(conflicts);
    }

    // 打印LR1分析表
    void printLR1Table(int size, String path) {
        BufferedWriter bw = null;
        int width = 10;
        try {
            bw = new BufferedWriter(new FileWriter(path));

            for (int i = 0; i < width; i++) {
                bw.write(" ");
            }
            bw.write("|");

            for (int character : GRAMMAR_CHARACTER) {
                String s;
                if (character == -1) s = "$";
                else if (character < 60) s = Token.TOKEN_NUMBER_2_STRING.get(character);
                else s = NON_TERMINAL_NUMBER_2_STRING.get(character);

                bw.write(s);
                for (int i = 0; i < width - s.length(); i++) {
                    bw.write(" ");
                }
                bw.write("|");
            }
            bw.newLine();

            for (int i = 1; i <= size; i++) {
                bw.write(String.valueOf(i));
                for (int j = 0; j < width - String.valueOf(i).length(); j++) {
                    bw.write(" ");
                }
                bw.write("|");

                for (int character : GRAMMAR_CHARACTER) {
                    String s = LR1Table.get(i).get(character);
                    if (s != null) {
                        bw.write(s);
                        for (int j = 0; j < width - s.length(); j++) {
                            bw.write(" ");
                        }
                    } else {
                        for (int j = 0; j < width; j++) {
                            bw.write(" ");
                        }
                    }
                    bw.write("|");
                }
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Item> getProductionList() {
        return productionList;
    }

    public Map<Integer, Map<Integer, String>> getLR1Table() {
        return LR1Table;
    }

    public static void main(String[] args) {
        LR1Generator lr1Generator = new LR1Generator();
        lr1Generator.loadGrammar("src/main/resources/grammar.txt");
//        System.out.println(productionMap);
//        for (Item production : productionList) {
//            System.out.println(production);
//        }

        lr1Generator.calculateFirstSet();
        System.out.println(firstMap);
        System.out.println("========================");
        lr1Generator.calculateFollowSet();
        System.out.println(followMap);

        Set<ItemSet> itemSets = lr1Generator.items();

        int size = itemSets.size();
        for (ItemSet itemSet : itemSets) {
            System.out.println(itemSet);
        }
        System.out.println("size:" + itemSets.size());
        lr1Generator.generateLR1Table(itemSets);
        lr1Generator.printLR1Table(size, "src/main/resources/LR1Table.txt");

//        for (int x : firstMap.keySet()) {
////            System.out.println(NON_TERMINAL_NUMBER_2_STRING.get(x) + ":" + isNullable(x));
//            for (int y : firstMap.get(x)) {
//                if (y < 60) {
//                    System.out.print(Token.TOKEN_NUMBER_2_STRING.get(y) + " ");
//                } else {
//                    System.out.print(NON_TERMINAL_NUMBER_2_STRING.get(y) + " ");
//                }
//                System.out.println();
//            }
//            System.out.println();
//        }

//        System.out.println(lr1Generator.calculateLookAhead(new ArrayList<>(Arrays.asList(61, 2))));

//        for (Integer key : NON_TERMINAL_NUMBER_2_STRING.keySet()) {
//            System.out.print(key + ":" + NON_TERMINAL_NUMBER_2_STRING.get(key) + " ");
//        }
//        System.out.println();
//
//        for (Integer key : productionMap.keySet()) {
//            System.out.println(key);
//            System.out.println(productionMap.get(key));
//        }
    }

}