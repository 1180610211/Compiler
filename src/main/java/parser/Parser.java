package parser;

import lexer.Lexer;
import lexer.Token;
import symbol.*;

import java.util.*;

// shift-reduce analysis
public class Parser {
    //产生式列表 对应于归约动作中的ri
    private List<Item> productionList;
    //状态->(文法符号->动作)
    //action + goto
    private Map<Integer, Map<Integer, String>> LR1Table;

    private static int lineNumber;
    private int instructionNumber;
    private int nextQuad;
    private StringBuilder tree;
    private List<String> errorList;  //语法分析检测出的错误
    private List<String> instructionList = new ArrayList<>(); //三地址指令
    private List<String> quadrupleList = new ArrayList<>(); //四元式

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
        //状态栈
        Stack<Integer> stateStack = new Stack<>();
        //文法符号(树节点)栈
        Stack<TNode> symbolStack = new Stack<>();
        //属性栈（综合属性和继承属性）
        Stack<Attribute> attributeStack = new Stack<>();
        //保存offset
        Stack<Integer> offsetStack = new Stack<>();
        //保存符号表
        Stack<SymbolTable> symbolTableStack = new Stack<>();

        //初始化
        lineNumber = 1;
        errorList = new ArrayList<>();
        tree = new StringBuilder();
        instructionNumber = 1;//总下一条指令的编号
        nextQuad = 1;//一个函数内中间代码的编号

        stateStack.push(1);
        int i = 0;//Token序号
        int tempNumber = 1;//临时变量的编号
        String operation;//LR1Table查找得到的操作
        Item production;//归约产生式
        SymbolTable symbolTable = new SymbolTable(); //当前符号表
        symbolTable.putSymbol("main", new FuncSymbol(null, "function", 0, "int", new ArrayList<String>(), null));
//        symbolTableStack.push(symbolTable);
        int offset = 0;//偏移量

        while (i < tokenList.size()) {

            operation = LR1Table.get(stateStack.peek()).get(tokenList.get(i).getTokenNumber());
            if (operation != null) {
//                System.out.println(operation);
                if (operation.contains("s")) {
                    Token token = tokenList.get(i);
                    stateStack.push(Integer.parseInt(operation.substring(1)));
                    symbolStack.push(new TNode(Token.TOKEN_NUMBER_2_STRING.get(token.getTokenNumber())
                            , token.getLineNumber(), token.getAttribute()));
                    attributeStack.push(new Attribute(token));

                    i++;
                } else if (operation.contains("r")) {
                    int index = Integer.parseInt(operation.substring(1));
                    production = productionList.get(index - 1);
                    System.out.println(production);

                    //语法分析
                    //构建语法分析树parser tree
                    TNode parent = new TNode(LR1Generator.NON_TERMINAL_NUMBER_2_STRING.get(production.getLeft()), 0, null);
                    List<TNode> childrenList = parent.getChildren();
                    for (int j = 0; j < production.getRight().size(); j++) {
                        stateStack.pop();
                        childrenList.add(symbolStack.pop());
                    }
                    if (childrenList.size() != 0) {
                        parent.setLineNumber(childrenList.get(childrenList.size() - 1).getLineNumber());
                    }

                    stateStack.push(Integer.parseInt(LR1Table.get(stateStack.peek()).get(production.getLeft())));
                    symbolStack.push(parent);


                    //语法制导翻译
                    if (index == 6) {

                    } else if (index == 7) {
                        //ExtDef ::= function Type FunDec { O P DefList StmtList }
                        Attribute attribute9 = attributeStack.pop();
                        Attribute attribute8 = attributeStack.pop();
                        Attribute attribute7 = attributeStack.pop();
                        Attribute attribute6 = attributeStack.pop();
                        Attribute attribute5 = attributeStack.pop();
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        int width = offset;
                        SymbolTable temp = symbolTable;
                        symbolTable = symbolTableStack.pop();
                        FuncSymbol funcSymbol = (FuncSymbol) symbolTable.getSymbol((String) ((Token) attribute3.getAttribute("lexeme")).getAttribute());
                        funcSymbol.setOffset(width);
                        funcSymbol.setFuncSymbolTable(temp);
                        offset = offsetStack.pop();
                    } else if (index == 8) {
                        //ExtDef ::= function Type FunDec ;
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        Token token = (Token) attribute3.getAttribute("lexeme");
                        symbolTable.putSymbol((String) token.getAttribute(), new FuncSymbol(token, "function",
                                0, (String) attribute2.getAttribute("type"),
                                (List<String>) attribute3.getAttribute("typeList"), null));
                    } else if (index >= 9 && index <= 13) {
                        //BaseType ::= bool | char | int | float | double
                        Attribute t = attributeStack.peek();
                        if (t.getToken().getTokenNumber() == Token.TOKEN_STRING_2_NUMBER.get("bool")) {
                            t.putAttribute("type", "bool");
                            t.putAttribute("width", 1);
                        } else if (t.getToken().getTokenNumber() == Token.TOKEN_STRING_2_NUMBER.get("char")) {
                            t.putAttribute("type", "char");
                            t.putAttribute("width", 1);
                        } else if (t.getToken().getTokenNumber() == Token.TOKEN_STRING_2_NUMBER.get("int")) {
                            t.putAttribute("type", "int");
                            t.putAttribute("width", 4);
                        } else if (t.getToken().getTokenNumber() == Token.TOKEN_STRING_2_NUMBER.get("float")) {
                            t.putAttribute("type", "float");
                            t.putAttribute("width", 4);
                        } else if (t.getToken().getTokenNumber() == Token.TOKEN_STRING_2_NUMBER.get("double")) {
                            t.putAttribute("type", "double");
                            t.putAttribute("width", 8);
                        }

                    } else if (index == 14) {
                        //Type ::= BaseType
                    } else if (index == 15) {
                        //Type ::= struct ID

                    } else if (index == 16) {
                        //DefList ::= DefList Def
                        attributeStack.pop();
                    } else if (index == 17) {
                        //DefList ::= ''
                        Attribute defList = new Attribute();
                        attributeStack.push(defList);
                    } else if (index == 18) {
                        //Def ::= Type ID dims ;
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        Boolean isArray = (Boolean) attribute3.getAttribute("isArray");
                        if (isArray) {
                            List<Integer> dimList = (List<Integer>) attribute3.getAttribute("list");
                            String type = constructType(dimList, (String) attribute1.getAttribute("type"));
                            int width = (Integer) attribute1.getAttribute("width") * prod(dimList);
                            symbolTable.putSymbol((String) attribute2.getToken().getAttribute(),
                                    new ArraySymbol(attribute2.getToken(), type, offset, dimList, (Integer) attribute1.getAttribute("width")));
                            offset += width;
                        } else {
                            symbolTable.putSymbol((String) attribute2.getToken().getAttribute(),
                                    new Symbol(attribute2.getToken(), (String) attribute1.getAttribute("type"), offset));
                            offset += (Integer) attribute1.getAttribute("width");
                        }
                        Attribute def = new Attribute();
                        attributeStack.push(def);
                    } else if (index == 19) {
                        //dims ::= ε
                        Attribute dims = new Attribute();
                        dims.putAttribute("isArray", false);
                        List<Integer> dimsList = new ArrayList<>();
                        dims.putAttribute("list", new ArrayList<Integer>());
                        attributeStack.push(dims);
                    } else if (index == 20) {
                        //dims ::= [ INTNUM ] dims
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        Attribute dims = new Attribute();
                        dims.putAttribute("isArray", true);

                        List<Integer> dimsList = new ArrayList<>();
                        dimsList.add((Integer) (attribute2.getToken().getAttribute()));
                        dimsList.addAll((List<Integer>) attribute4.getAttribute("list"));
                        dims.putAttribute("list", dimsList);
                        attributeStack.push(dims);
                    } else if (index == 21) {
                        //FunDec ::= ID ( VarList )
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        Attribute funDec = new Attribute();
                        funDec.putAttribute("lexeme", attribute1.getToken());
                        funDec.putAttribute("typeList", (List<String>) attribute3.getAttribute("typeList"));
                        funDec.putAttribute("IDList", (List<Token>) attribute3.getAttribute("IDList"));
                        funDec.putAttribute("widthList", (List<Integer>) attribute3.getAttribute("widthList"));
                        attributeStack.push(funDec);
                    } else if (index == 22) {
                        //FunDec ::= ID ( )
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        Attribute funDec = new Attribute();
                        funDec.putAttribute("lexeme", attribute1.getToken());
                        funDec.putAttribute("typeList", new ArrayList<String>());
                        funDec.putAttribute("IDList", new ArrayList<Token>());
                        funDec.putAttribute("widthList", new ArrayList<Integer>());
                        attributeStack.push(funDec);
                    } else if (index == 23) {
                        //VarList ::= VarList , ParamDec
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        Attribute varList = new Attribute();
                        List<String> typeList = (List<String>) attribute1.getAttribute("typeList");
                        List<Token> IDList = (List<Token>) attribute1.getAttribute("IDList");
                        List<Integer> widthList = (List<Integer>) attribute1.getAttribute("widthList");

                        typeList.add((String) attribute3.getAttribute("type"));
                        varList.putAttribute("typeList", typeList);
                        IDList.add((Token) attribute3.getAttribute("id"));
                        varList.putAttribute("IDList", IDList);
                        widthList.add((Integer) attribute3.getAttribute("width"));
                        varList.putAttribute("widthList", widthList);

                        attributeStack.push(varList);
                    } else if (index == 24) {
                        //VarList ::= ParamDec
                        Attribute paramDec = attributeStack.pop();
                        Attribute varList = new Attribute();
                        List<String> typeList = new ArrayList<>();
                        List<Token> IDList = new ArrayList<>();
                        List<Integer> widthList = new ArrayList<>();

                        typeList.add((String) paramDec.getAttribute("type"));
                        varList.putAttribute("typeList", typeList);
                        IDList.add((Token) paramDec.getAttribute("id"));
                        varList.putAttribute("IDList", IDList);
                        widthList.add((Integer) paramDec.getAttribute("width"));
                        varList.putAttribute("widthList", widthList);

                        attributeStack.push(varList);
                    } else if (index == 25) {
                        //ParamDec ::= Type ID dims
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        List<Integer> dimList = (List<Integer>) attribute3.getAttribute("list");
                        String type = constructType(dimList, (String) attribute1.getAttribute("type"));
                        int width = (Integer) attribute1.getAttribute("width") * prod(dimList);
                        Attribute paramDec = new Attribute();
                        paramDec.putAttribute("id", attribute2.getToken());
                        paramDec.putAttribute("type", type);
                        paramDec.putAttribute("width", width);
                        attributeStack.push(paramDec);
                    } else if (index == 26) {
                        //CompSt ::= { O DefList StmtList }
                        Attribute attribute5 = attributeStack.pop();
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();

                        SymbolTable temp = symbolTable;
                        symbolTable = symbolTableStack.pop();
                        if (offset != 0) {
                            symbolTable.putSymbol("Compound Statements", new CompoundSymbol(null, "Compound Statements", offset, temp));
                        }
                        offset = offsetStack.pop();

                        Attribute compSt = new Attribute();
                        attributeStack.push(compSt);
                    } else if (index == 27) {
                        //StmtList ::= StmtList M Stmt
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        backPatch((Integer) attribute2.getAttribute("quad"), (List<Integer>) attribute1.getAttribute("nextList"));
                        Attribute stmtList = new Attribute();
                        stmtList.putAttribute("nextList", attribute3.getAttribute("nextList"));
                        attributeStack.push(stmtList);
                    } else if (index == 28) {
                        //StmtList ::= ''
                        Attribute stmtList = new Attribute();
                        stmtList.putAttribute("nextList", new ArrayList<Integer>());
                        attributeStack.push(stmtList);
                    } else if (index == 29) {
                        //Stmt ::= Exp ;
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        Attribute stmt = new Attribute();
                        stmt.putAttribute("nextList", new ArrayList<Integer>());
                        attributeStack.push(stmt);
                    } else if (index == 30) {
                        //Stmt ::= ID = Exp ;
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        Symbol symbol = symbolTable.getSymbol((String) attribute1.getToken().getAttribute());
                        gen(symbol.getToken().getAttribute() + "=" + attribute3.getAttribute("addr"));
                        Attribute stmt = new Attribute();
                        stmt.putAttribute("nextList", new ArrayList<Integer>());
                        attributeStack.push(stmt);
                    } else if (index == 31) {
                        //Stmt ::=  L = Exp ;
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        Symbol symbol = (Symbol) attribute1.getAttribute("array");
                        gen(symbol.getToken().getAttribute() + "[" + attribute1.getAttribute("offset") + "]=" + attribute3.getAttribute("addr"));
                        Attribute stmt = new Attribute();
                        stmt.putAttribute("nextList", new ArrayList<Integer>());
                        attributeStack.push(stmt);
                    } else if (index == 32) {
                        Attribute stmt = attributeStack.peek();
                        stmt.putAttribute("nextList", new ArrayList<Integer>());
                    } else if (index == 33) {
                        //Stmt ::=  if ( B ) M Stmt
                        Attribute attribute6 = attributeStack.pop();//Stmt
                        Attribute attribute5 = attributeStack.pop();//M
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();//B
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();

                        Attribute stmt = new Attribute();
                        backPatch((Integer) attribute5.getAttribute("quad"), (List<Integer>) attribute3.getAttribute("trueList"));
                        stmt.putAttribute("nextList",
                                merge((List<Integer>) attribute3.getAttribute("falseList"),
                                        (List<Integer>) attribute6.getAttribute("nextList")));
                        attributeStack.push(stmt);
                    } else if (index == 34) {
                        //Stmt ::=  if ( B ) M Stmt N else M Stmt
                        Attribute attribute10 = attributeStack.pop(); //Stmt
                        Attribute attribute9 = attributeStack.pop();//M
                        Attribute attribute8 = attributeStack.pop();
                        Attribute attribute7 = attributeStack.pop();//N
                        Attribute attribute6 = attributeStack.pop();//Stmt
                        Attribute attribute5 = attributeStack.pop();//M
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();//B
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();

                        Attribute stmt = new Attribute();
                        backPatch((Integer) attribute9.getAttribute("quad"), (List<Integer>) attribute3.getAttribute("falseList"));
                        backPatch((Integer) attribute5.getAttribute("quad"), (List<Integer>) attribute3.getAttribute("trueList"));
                        stmt.putAttribute("nextList",
                                merge(merge((List<Integer>) attribute6.getAttribute("nextList"),
                                        (List<Integer>) attribute7.getAttribute("nextList")),
                                        (List<Integer>) attribute10.getAttribute("nextList")));
                        attributeStack.push(stmt);
                    } else if (index == 35) {
                        //Stmt ::=  return Exp ;
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        gen("return " + attribute2.getAttribute("addr"));
                        Attribute stmt = new Attribute();
                        stmt.putAttribute("nextList", new ArrayList<Integer>());
                        attributeStack.push(stmt);
                    } else if (index == 36) {
                        //Stmt ::=  while ( M B ) M Stmt
                        Attribute attribute7 = attributeStack.pop();//Stmt
                        Attribute attribute6 = attributeStack.pop();//M
                        Attribute attribute5 = attributeStack.pop();
                        Attribute attribute4 = attributeStack.pop();//B
                        Attribute attribute3 = attributeStack.pop();//M
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();

                        Attribute stmt = new Attribute();

                        backPatch((Integer) attribute3.getAttribute("quad"), (List<Integer>) attribute7.getAttribute("nextList"));
                        backPatch((Integer) attribute6.getAttribute("quad"), (List<Integer>) attribute4.getAttribute("trueList"));

                        gen("goto " + attribute3.getAttribute("quad"));
                        attributeStack.push(stmt);
                    } else if (index == 37) {
                        //Stmt ::=  do M Stmt while ( M B ) ;
                        Attribute attribute9 = attributeStack.pop();
                        Attribute attribute8 = attributeStack.pop();
                        Attribute attribute7 = attributeStack.pop();//B
                        Attribute attribute6 = attributeStack.pop();//M
                        Attribute attribute5 = attributeStack.pop();
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();//Stmt
                        Attribute attribute2 = attributeStack.pop();//M
                        Attribute attribute1 = attributeStack.pop();

                        Attribute stmt = new Attribute();

                        backPatch((Integer) attribute2.getAttribute("quad"), (List<Integer>) attribute7.getAttribute("trueList"));
                        backPatch((Integer) attribute6.getAttribute("quad"), (List<Integer>) attribute3.getAttribute("nextList"));
                        stmt.putAttribute("nextList", attribute7.getAttribute("falseList"));

                        attributeStack.push(stmt);
                    } else if (index == 38) {
                        //L ::= ID [ Exp ]
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        ArraySymbol arraySymbol = (ArraySymbol) symbolTable.getSymbol((String) attribute1.getToken().getAttribute());
                        Attribute L = new Attribute();
                        L.putAttribute("array", arraySymbol);
                        L.putAttribute("dim", 1);
                        L.putAttribute("offset", "t" + tempNumber);
                        tempNumber++;
                        int width = arraySymbol.getWidth(1);
                        gen(L.getAttribute("offset") + "=" + attribute3.getAttribute("addr") + "*" + width);
                        attributeStack.push(L);
                    } else if (index == 39) {
                        //L ::= L [ Exp ]
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        ArraySymbol arraySymbol = (ArraySymbol) attribute1.getAttribute("array");
                        int dimNum = (Integer) attribute1.getAttribute("dim") + 1;
                        attribute1.putAttribute("dim", dimNum);
                        String t1 = "t" + tempNumber;
                        tempNumber++;
                        int width = arraySymbol.getWidth(dimNum);
                        gen(attribute1.getAttribute("offset") + "=" + attribute3.getAttribute("addr") + "*" + width);
                        String t2 = "t" + tempNumber;
                        tempNumber++;
                        gen(t2 + "=" + attribute1.getAttribute("offset") + "+" + t1);
                        attribute1.putAttribute("offset", t2);
                        attributeStack.push(attribute1);
                    } else if (index >= 40 && index <= 43) {
                        //Exp ::= Exp + Exp | Exp - Exp | Exp * Exp | Exp / Exp
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        String addr = "t" + tempNumber;
                        tempNumber++;
                        gen(addr + "=" + attribute1.getAttribute("addr") +
                                Token.TOKEN_NUMBER_2_STRING.get(attribute2.getToken().getTokenNumber()) +
                                attribute3.getAttribute("addr"));
                        attribute1.putAttribute("addr", addr);
                        attributeStack.push(attribute1);
                    } else if (index == 44) {
                        //Exp ::=  ( Exp )
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        attributeStack.push(attribute2);
                    } else if (index == 45) {
                        //Exp ::= ID
                        Attribute attribute1 = attributeStack.pop();
                        Symbol symbol = symbolTable.getSymbol((String) attribute1.getToken().getAttribute());
                        Attribute exp = new Attribute();
                        exp.putAttribute("addr", attribute1.getToken().getAttribute());
                        attributeStack.push(exp);
                    } else if (index == 46) {
                        //Exp ::= L
                        Attribute attribute1 = attributeStack.pop();
                        String t = "t" + tempNumber;
                        tempNumber++;
                        Attribute exp = new Attribute();
                        exp.putAttribute("addr", t);
                        Symbol symbol = (Symbol) attribute1.getAttribute("array");
                        gen(exp.getAttribute("addr") + "=" + symbol.getToken().getAttribute() +
                                "[" + attribute1.getAttribute("offset") + "]");
                        attributeStack.push(exp);
                    } else if (index == 47) {
                        //Exp ::=  INTNUM
                        Attribute exp = attributeStack.peek();
                        exp.putAttribute("addr", exp.getToken().getAttribute());
                    } else if (index == 48) {
                        //Exp ::=  REALNUM
                        Attribute exp = attributeStack.peek();
                        exp.putAttribute("addr", exp.getToken().getAttribute());
                    } else if (index == 49) {
                        //Exp ::=  call ID ( )
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        gen("call " + attribute2.getToken().getAttribute() + "," + 0);
                        Attribute exp = new Attribute();
                        attributeStack.push(exp);
                    } else if (index == 50) {
                        //Exp ::=  call ID ( ParamList )
                        Attribute attribute5 = attributeStack.pop();
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        int n = 0;
                        List<String> list = (List<String>) attribute4.getAttribute("list");
                        for (int k = 0; k < list.size(); k++) {
                            gen("param " + list.get(k));
                            n++;
                        }
                        gen("call " + attribute2.getToken().getAttribute() + "," + n);
                        Attribute exp = new Attribute();
                        attributeStack.push(exp);
                    } else if (index == 51) {
                        //ParamList ::= ParamList , Exp
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        List<String> list = (List<String>) attribute1.getAttribute("list");
                        list.add(String.valueOf(attribute3.getAttribute("addr")));
                        Attribute paramList = new Attribute();
                        paramList.putAttribute("list", list);
                        attributeStack.push(paramList);
                    } else if (index == 52) {
                        //ParamList ::= Exp
                        Attribute attribute1 = attributeStack.pop();
                        Attribute paramList = new Attribute();
                        paramList.putAttribute("list",
                                new ArrayList<String>(Arrays.asList(String.valueOf(attribute1.getAttribute("addr")))));
                        attributeStack.push(paramList);
                    } else if (index == 53) {
                        //B ::= B || M B
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        Attribute B = new Attribute();
                        backPatch((Integer) attribute3.getAttribute("quad"), (List<Integer>) attribute1.getAttribute("falseList"));
                        B.putAttribute("trueList", merge((List<Integer>) attribute1.getAttribute("trueList"),
                                (List<Integer>) attribute4.getAttribute("trueList")));
                        B.putAttribute("falseList", attribute4.getAttribute("falseList"));
                        attributeStack.push(B);
                    } else if (index == 54) {
                        //B ::=  B && M B
                        Attribute attribute4 = attributeStack.pop();
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        Attribute B = new Attribute();
                        backPatch((Integer) attribute3.getAttribute("quad"), (List<Integer>) attribute1.getAttribute("trueList"));
                        B.putAttribute("trueList", attribute4.getAttribute("trueList"));
                        B.putAttribute("falseList", merge((List<Integer>) attribute1.getAttribute("falseList"),
                                (List<Integer>) attribute4.getAttribute("falseList")));
                        attributeStack.push(B);
                    } else if (index == 55) {
                        //B ::=  ! B
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        Attribute B = new Attribute();
                        B.putAttribute("trueList", attribute2.getAttribute("falseList"));
                        B.putAttribute("falseList", attribute2.getAttribute("trueList"));
                        attributeStack.push(B);
                    } else if (index == 56) {
                        //B ::=  ( B )
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        Attribute B = new Attribute();
                        B.putAttribute("trueList", attribute2.getAttribute("trueList"));
                        B.putAttribute("falseList", attribute2.getAttribute("falseList"));
                        attributeStack.push(B);
                    } else if (index == 57) {
                        //B ::=  Exp relop Exp
                        Attribute attribute3 = attributeStack.pop();
                        Attribute attribute2 = attributeStack.pop();
                        Attribute attribute1 = attributeStack.pop();
                        Attribute B = new Attribute();
                        B.putAttribute("trueList", makeList(instructionNumber));
                        B.putAttribute("falseList", makeList(instructionNumber + 1));
                        gen("if " + attribute1.getAttribute("addr") + attribute2.getAttribute("op") + attribute3.getAttribute("addr") + " goto ");
                        gen("goto ");
                        attributeStack.push(B);
                    } else if (index == 58) {
                        //B ::=  true
                        Attribute B = attributeStack.peek();
                        B.putAttribute("trueList", makeList(instructionNumber));
                        gen("goto ");
                    } else if (index == 59) {
                        //B ::=  false
                        Attribute B = attributeStack.peek();
                        B.putAttribute("falseList", makeList(instructionNumber));
                        gen("goto ");
                    } else if (index >= 60 && index <= 65) {
                        //relop ::= < | <= | == | != | > | >=
                        Attribute t = attributeStack.peek();
                        t.putAttribute("op", Token.TOKEN_NUMBER_2_STRING.get(t.getToken().getTokenNumber()));
                    } else if (index == 66) {
                        //M ::= ε
                        Attribute M = new Attribute();
                        M.putAttribute("quad", nextQuad);
                        attributeStack.push(M);
                    } else if (index == 67) {
                        //N ::= ε
                        Attribute N = new Attribute();
                        N.putAttribute("nextList", makeList(instructionNumber));
                        gen("goto ");
                        attributeStack.push(N);
                    } else if (index == 68) {
                        //O ::= ε
                        symbolTableStack.push(symbolTable);
                        symbolTable = new SymbolTable(symbolTable);
                        offsetStack.push(offset);
                        offset = 0;
                        attributeStack.push(new Attribute());
                    } else if (index == 69) {
                        //P ::= ε
                        nextQuad = 1;
                        tempNumber = 1;
                        instructionList.add("function " + ((Token) attributeStack.get(attributeStack.size() - 3).getAttribute("lexeme")).getAttribute() + " instructions:");
//                        quadrupleList.add("function " + attributeStack.get(attributeStack.size() - 3).getAttribute("lexeme") + " quadruples:");
                        instructionNumber++;
                        List<String> typeList = (List<String>) attributeStack.get(attributeStack.size() - 3).getAttribute("typeList");
                        List<Integer> widthList = (List<Integer>) attributeStack.get(attributeStack.size() - 3).getAttribute("widthList");
                        List<Token> IDList = (List<Token>) attributeStack.get(attributeStack.size() - 3).getAttribute("IDList");
                        Token token = null;
                        for (int k = 0; k < IDList.size(); k++) {
                            token = IDList.get(k);
                            symbolTable.putSymbol((String) token.getAttribute(), new Symbol(token, typeList.get(k), widthList.get(k)));
                        }
                        attributeStack.push(new Attribute());
                    }

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

    private String constructType(List<Integer> dimList, String type) {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        for (int i = 0; i < dimList.size(); i++) {
            sb.append('[');
            sb.append(dimList.get(i));
            sb.append(']');
        }
        return sb.toString();
    }

    private int prod(List<Integer> dimList) {
        int n = 1;
        for (int i = 0; i < dimList.size(); i++) {
            n *= dimList.get(i);
        }
        return n;
    }

    private List<Integer> makeList(int instructionNumber) {
        return new ArrayList<>(Arrays.asList(instructionNumber));
    }

    private List<Integer> merge(List<Integer> list1, List<Integer> list2) {
        List<Integer> list = new ArrayList<Integer>();
        list.addAll(list1);
        list.addAll(list2);
        return list;
    }

    private void backPatch(int quad, List<Integer> nextList) {
        for (int i = 0; i < nextList.size(); i++) {
            int code = nextList.get(i) - 1;
            instructionList.set(code, instructionList.get(code) + quad);
        }
    }

    private void gen(String instruction) {
        instructionList.add(nextQuad + ": " + instruction);
        instructionNumber++;
        nextQuad++;
    }

    public void printParseTree(TNode root, int layer) {
        List<TNode> childrenList = root.getChildren();
        for (int j = 0; j < layer * 2; j++) {
            tree.append(" ");
        }
        tree.append(root.getSymbol());

        //非终结符，有属性
        if (root.getAttribute() != null) {
            tree.append(": " + root.getAttribute());
        }
        if (root.getLineNumber() == 0) {
            tree.append(" (" + lineNumber + ")\n");
        } else {
            tree.append(" (" + root.getLineNumber() + ")\n");
            lineNumber = root.getLineNumber();
        }
        for (int i = childrenList.size() - 1; i >= 0; i--) {
            printParseTree(childrenList.get(i), layer + 1);
        }
    }

    public List<String> getErrorList() {
        return errorList;
    }

    public String getTree() {
        return tree.toString();
    }

    public static void main(String[] args) {
        Lexer lexer = new Lexer("src/main/resources/test/semantic_test.c"); //syntax_test_error
        ArrayList<Token> tokenList = lexer.getTokenList();
        System.out.println(tokenList.size());

        Parser parser = new Parser();
        TNode root = parser.analysis(tokenList);
        System.out.println();
        System.out.println("productions:");
        for (int i = 0; i < parser.productionList.size(); i++) {
            System.out.println(parser.productionList.get(i));
        }

//        for (int i = 0; i < parser.getErrorList().size(); i++) {
//            System.out.println(parser.getErrorList().get(i));
//        }
//        parser.printParseTree(root, 0);

        System.out.println();
        System.out.println("instructions:");
        for (int i = 0; i < parser.instructionList.size(); i++) {
            System.out.println(parser.instructionList.get(i));
        }
    }
}