package symbol;

import lexer.Token;

public class CompoundSymbol extends Symbol{
    private SymbolTable compoundSymbolTable;

    public CompoundSymbol(Token token, String type, int offset, SymbolTable compoundSymbolTable) {
        super(token, type, offset);
        this.compoundSymbolTable = compoundSymbolTable;
    }

    public SymbolTable getCompoundSymbolTable() {
        return compoundSymbolTable;
    }

    public void setCompoundSymbolTable(SymbolTable compoundSymbolTable) {
        this.compoundSymbolTable = compoundSymbolTable;
    }
}
