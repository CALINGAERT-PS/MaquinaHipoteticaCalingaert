package Ligador;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    Map<String, Symbol> table;

    public SymbolTable() {
        this.table = new HashMap<>();
    }

    // Adiciona um simbolo na tabela
    public void addSymbol(String name, Symbol symbol) {
        table.put(name, symbol);
    }

    // Retorna um simbolo da tabela
    public Symbol getSymbol(String name) {
        return table.get(name);
    }

    @Override
    public String toString() {
        return "SymbolTable{table=" + table + '}';
    }
}
