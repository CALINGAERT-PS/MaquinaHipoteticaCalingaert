package Ligador;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Segment {
    String name; // Nome do segmento
    List<Integer> code; // Código de instruções
    HashMap<String, Symbol> symbols; // Tabela de símbolos
    int baseAddress; // Endereço base do segmento

    public Segment(String name) {
        this.name = name;
        this.code = new ArrayList<>();
        this.symbols = new HashMap<>();
    }

    public void addInstruction(int instruction) {
        this.code.add(instruction);
    }

    public void addSymbol(String name, int address, boolean isGlobal, boolean isRelocatable) {
        this.symbols.put(name, new Symbol(name, address, isGlobal, isRelocatable));
    }
}
