package Ligador;
import java.util.HashMap;
import java.util.Map;
public class SymbolTable {

    private Map<String, Symbol> symbols;

    public SymbolTable() {
        this.symbols = new HashMap<>();
    }

    public void addSymbol(String name, Symbol symbol) {
        symbols.put(name, symbol);
    }

    public boolean containsSymbol(String name) {
        return symbols.containsKey(name);
    }

    public Symbol getSymbol(String name) {
        return symbols.get(name);
    }

    public Map<String, Symbol> getSymbols() {

        return symbols;

    }
}
