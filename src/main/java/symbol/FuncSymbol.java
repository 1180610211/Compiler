package symbol;

import lexer.Token;

import java.util.List;

public class FuncSymbol extends Symbol{
    private String returnType;
    private List<String> parameterTypeList;
    private SymbolTable funcSymbolTable;

    public FuncSymbol(Token token, String type, int offset, String returnType, List<String> parameterTypeList, SymbolTable funcSymbolTable) {
        super(token, type, offset);
        this.returnType = returnType;
        this.parameterTypeList = parameterTypeList;
        this.funcSymbolTable = funcSymbolTable;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<String> getParameterTypeList() {
        return parameterTypeList;
    }

    public void setParameterTypeList(List<String> parameterTypeList) {
        this.parameterTypeList = parameterTypeList;
    }

    public SymbolTable getFuncSymbolTable() {
        return funcSymbolTable;
    }

    public void setFuncSymbolTable(SymbolTable funcSymbolTable) {
        this.funcSymbolTable = funcSymbolTable;
    }
}
