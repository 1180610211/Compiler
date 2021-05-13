package symbol;

import lexer.Token;

import java.util.Map;

public class CompoundSymbol extends Symbol {
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

    @Override
    public String print(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append("  ");
        }
        sb.append("Compound Symbol: " + getOffset() + "\n");
        Map<String, Symbol> m = compoundSymbolTable.getTable();
        for (String key : m.keySet()) {
            sb.append(m.get(key).print(n + 1));
            sb.append("\n");
        }
        return sb.toString();
    }
}
