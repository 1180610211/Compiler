package symbol;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private SymbolTable prev;
    private Map<String, Symbol> table = new HashMap<>();

    public SymbolTable() {
    }

    public SymbolTable(SymbolTable prev) {
        this.prev = prev;
    }

    public void putSymbol(String id, Symbol symbol) {
        table.put(id, symbol);
    }

    public Symbol getSymbol(String id) {
        SymbolTable symbolTable = this;
        while (symbolTable != null) {
            Symbol temp = symbolTable.table.get(id);
            if (temp != null) {
                return temp;
            }
            symbolTable = symbolTable.prev;
        }
        return null;
    }

    public Map<String, Symbol> getTable() {
        return table;
    }
}
