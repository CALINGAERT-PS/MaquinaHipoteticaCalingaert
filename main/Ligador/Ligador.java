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
        System.out.println("Segmento adicionado: " + segment.name);
    }

    // Primeira passagem: unir as tabelas de definições dos segmentos
    public void firstPass() {
        System.out.println("Iniciando a primeira passagem...");
        for (Segment segment : segments) {
            // Definir o endereço base do segmento (relocação)
            segment.baseAddress = relocationOffset;
            System.out.println("Base Address do segmento " + segment.name + " definida para: " + relocationOffset);

            // Adicionar símbolos globais à Tabela de Símbolos Globais (TSG)
            for (Symbol symbol : segment.symbols.values()) {
                if (symbol.isGlobal) {
                    // Adiciona o símbolo com relocação aplicada
                    Symbol relocatedSymbol = new Symbol(
                            symbol.name,
                            symbol.address + relocationOffset,
                            symbol.isGlobal,
                            symbol.isRelocatable);
                    
                    // Verifica se o símbolo já existe na tabela global
                    if (globalSymbolTable.containsSymbol(symbol.name)) {
                        throw new RuntimeException("Erro: Símbolo redefinido - " + symbol.name);
                    }
                    globalSymbolTable.addSymbol(symbol.name, relocatedSymbol);
                    System.out.println("Símbolo global adicionado: " + symbol.name + " com endereço " + relocatedSymbol.address);
                }
            }

            // Atualizar o offset de relocação
            relocationOffset += segment.code.size();
        }
        System.out.println("Primeira passagem completa.");
        System.out.println("Tabela de Símbolos Globais após a primeira passagem:");
        for (String symbolName : globalSymbolTable.getSymbols().keySet()) {
            System.out.println("Símbolo: " + symbolName + ", Endereço: " + globalSymbolTable.getSymbol(symbolName).address);
        }
    }

    // Segunda passagem: atualizar endereços e resolver referências externas
    public void secondPass() {
        System.out.println("Iniciando a segunda passagem...");
        for (Segment segment : segments) {
            // Atualizar as instruções do segmento para refletir a relocação
            for (int i = 0; i < segment.code.size(); i++) {
                int instruction = segment.code.get(i);
                System.out.println("Instrução original no segmento " + segment.name + ": " + instruction);

                // Exemplo: Se a instrução usa um símbolo global, substituí-lo pelo endereço
                for (Symbol symbol : segment.symbols.values()) {
                    if (symbol.isGlobal && symbol.isRelocatable) {
                        Symbol globalSymbol = globalSymbolTable.getSymbol(symbol.name);
                        if (globalSymbol != null) {
                            // Atualizar a instrução com o endereço do símbolo global
                            System.out.println("Ajustando a instrução original " + instruction + " com o endereço de símbolo " + globalSymbol.address);
                            instruction = adjustInstruction(instruction, globalSymbol.address);
                            System.out.println("Instrução ajustada com o símbolo " + symbol.name + ": " + instruction);
                        }
                    }
                }

                // Atualiza a instrução relocada
                segment.code.set(i, instruction);
            }
        }
        System.out.println("Segunda passagem completa.");
    }

    // Ajusta a instrução com base no endereço fornecido
    private int adjustInstruction(int instruction, int address) {
        System.out.println("Ajustando instrução " + instruction + " com o endereço " + address);
        return instruction + address; // Implementar lógica de ajuste
    }

    // Lê os segmentos de um arquivo
    public void loadSegmentsFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        Segment currentSegment = null;

        System.out.println("Carregando segmentos do arquivo: " + filename);
        while ((line = reader.readLine()) != null) {
            line = line.trim();

            // Novo segmento
            if (line.startsWith("SEGMENT")) {
                if (currentSegment != null) {
                    addSegment(currentSegment);
                }
                currentSegment = new Segment(line.split(" ")[1]);
                System.out.println("Novo segmento encontrado: " + currentSegment.name);
            }
            // Instrução
            else if (line.startsWith("INSTR")) {
                if (currentSegment != null) {
                    int instr = Integer.parseInt(line.split(" ")[1]);
                    currentSegment.addInstruction(instr);
                    System.out.println("Instrução adicionada ao segmento " + currentSegment.name + ": " + instr);
                }
            }
            // Símbolo
            else if (line.startsWith("SYMBOL")) {
                if (currentSegment != null) {
                    String[] parts = line.split(" ");
                    currentSegment.addSymbol(parts[1], Integer.parseInt(parts[2]), Boolean.parseBoolean(parts[3]),
                            Boolean.parseBoolean(parts[4]));
                    System.out.println("Símbolo adicionado ao segmento " + currentSegment.name + ": " + parts[1] + " (Endereço: " + parts[2] + ")");
                }
            }
        }

        if (currentSegment != null) {
            addSegment(currentSegment);
        }

        reader.close();
        System.out.println("Carregamento de segmentos completo.");
    }

    // Escreve os segmentos ligados em um arquivo
    public void writeLinkedSegmentsToFile(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

        System.out.println("Gravando segmentos ligados no arquivo: " + filename);
        for (Segment segment : segments) {
            writer.write("SEGMENT " + segment.name + " Base Address: " + segment.baseAddress + "\n");
            for (int instruction : segment.code) {
                writer.write("INSTR " + instruction + "\n");
                System.out.println("Gravando instrução: " + instruction + " no arquivo.");
            }
        }

        writer.close();
        System.out.println("Gravação completa.");
    }
}
