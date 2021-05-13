package symbol;

import lexer.Token;

import java.util.Map;

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

    @Override
    public String print(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append("  ");
        }
        sb.append("Struct Symbol: " + getToken().getAttribute() + " " + getOffset() + "\n");
        Map<String, Symbol> m = structSymbolTable.getTable();
        for (String key : m.keySet()) {
            sb.append(m.get(key).print(n + 1));
            sb.append("\n");
        }
        return sb.toString();
    }
}
