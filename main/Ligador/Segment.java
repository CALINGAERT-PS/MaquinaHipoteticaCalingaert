package Ligador;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Segment {
    String name;
    List<Integer> code; // Código objeto do segmento
    Map<String, Symbol> symbols; // Tabela de símbolos locais
    int baseAddress; // Endereço base do segmento

    public Segment(String name) {
        this.name = name;
        this.code = new ArrayList<>();
        this.symbols = new HashMap<>();
        this.baseAddress = 0;
    }

    // Adiciona uma instrução ao segmento
    public void addInstruction(int instruction) {
        code.add(instruction);
    }

    // Adiciona um símbolo ao segmento
    public void addSymbol(String name, int address, boolean isGlobal, boolean isRelocatable) {
        symbols.put(name, new Symbol(name, address, isGlobal, isRelocatable));
    }

    @Override
    public String toString() {
        return "Segment{name='" + name + "', baseAddress=" + baseAddress + ", symbols=" + symbols + '}';
    }
}
