package symbol;

import lexer.Token;

public class StructSymbol extends Symbol {
    private SymbolTable structSymbolTable;

    public StructSymbol(Token token, String type, int offset, SymbolTable structSymbolTable) {
        super(token, type, offset);
        this.structSymbolTable = structSymbolTable;
    }

    public SymbolTable getStructSymbolTable() {
        return structSymbolTable;
    }

    public void setStructSymbolTable(SymbolTable structSymbolTable) {
        this.structSymbolTable = structSymbolTable;
    }
}
