package Ligador;

import java.util.List;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class Ligador {
    List<Segment> segments; // Lista dos segmentos
    SymbolTable globalSymbolTable; // Tabela de Símbolos Globais
    int relocationOffset = 0; // Offset para relocação de segmentos

    public Ligador() {
        this.segments = new ArrayList<>();
        this.globalSymbolTable = new SymbolTable();
    }

    // Adiciona um segmento à lista de segmentos
    public void addSegment(Segment segment) {
        segments.add(segment);
    }

    // Primeira passagem: unir as tabelas de definições dos segmentos
    public void firstPass() {
        for (Segment segment : segments) {
            // Definir o endereço base do segmento (relocação)
            segment.baseAddress = relocationOffset;

            // Adicionar símbolos globais à Tabela de Símbolos Globais (TSG)
            for (Symbol symbol : segment.symbols.values()) {
                if (symbol.isGlobal) {
                    Symbol relocatedSymbol = new Symbol(
                            symbol.name,
                            symbol.address + relocationOffset,
                            symbol.isGlobal,
                            symbol.isRelocatable);
                    globalSymbolTable.addSymbol(symbol.name, relocatedSymbol);
                }
            }

            // Atualizar o offset de relocação
            relocationOffset += segment.code.size();
        }
    }

    // Segunda passagem: atualizar endereços e resolver referências externas
    public void secondPass() {
        for (Segment segment : segments) {
            // Atualizar as instruções do segmento para refletir a relocação
            for (int i = 0; i < segment.code.size(); i++) {
                int instruction = segment.code.get(i);

                // Exemplo: Se a instrução usa um símbolo global, substituí-lo pelo endereço
                // correto
                for (Symbol symbol : segment.symbols.values()) {
                    if (symbol.isGlobal && symbol.isRelocatable) {
                        instruction += globalSymbolTable.getSymbol(symbol.name).address;
                    }
                }

                // Atualiza a instrução relocada
                segment.code.set(i, instruction);
            }
        }
    }

    // Lê os segmentos de um arquivo
    public void loadSegmentsFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        Segment currentSegment = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // Novo segmento
            if (line.startsWith("SEGMENT")) {
                if (currentSegment != null) {
                    addSegment(currentSegment);
                }
                currentSegment = new Segment(line.split(" ")[1]);
            }
            // Instrução
            else if (line.startsWith("INSTR")) {
                if (currentSegment != null) {
                    currentSegment.addInstruction(Integer.parseInt(line.split(" ")[1]));
                }
            }
            // Símbolo
            else if (line.startsWith("SYMBOL")) {
                if (currentSegment != null) {
                    String[] parts = line.split(" ");
                    currentSegment.addSymbol(parts[1], Integer.parseInt(parts[2]), Boolean.parseBoolean(parts[3]),
                            Boolean.parseBoolean(parts[4]));
                }
            }
        }

        if (currentSegment != null) {
            addSegment(currentSegment);
        }

        reader.close();
    }

    public void writeLinkedSegmentsToFile(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

        for (Segment segment : segments) {
            writer.write("SEGMENT " + segment.name + " Base Address: " + segment.baseAddress + "\n");
            for (int instruction : segment.code) {
                writer.write("INSTR " + instruction + "\n");
            }
        }

        writer.close();
    }
}
