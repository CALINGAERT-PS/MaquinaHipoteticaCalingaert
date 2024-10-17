import java.util.HashMap;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Assembler {
    private HashMap<String, Definition> definitionTable; // Tabela de Definições
    private ArrayList<Usage> useTable; // Tabela de Usos
    private HashMap<String, Short> symbolTable; // Tabela de Símbolos
    private ArrayList<String> sourceLines; // Linhas do código fonte
    private ArrayList<String> objectCode; // Código objeto gerado
    private ArrayList<String> errors; // Lista de erros encontrados
    private short locationCounter; // Contador de localizações
    private boolean hasError; // Flag para erro

    public Assembler() {
        definitionTable = new HashMap<>();
        useTable = new ArrayList<>();
        symbolTable = new HashMap<>();
        sourceLines = new ArrayList<>();
        objectCode = new ArrayList<>();
        errors = new ArrayList<>();
        locationCounter = 0;
        hasError = false;
    }

    // Classe para armazenar definições
    class Definition {
        short address;
        String addressingMode;

        Definition(short address, String addressingMode) {
            this.address = address;
            this.addressingMode = addressingMode;
        }
    }

    // Classe para armazenar usos
    class Usage {
        String symbol;
        short address;
        String addressingMode;
        String sign;

        Usage(String symbol, short address, String addressingMode, String sign) {
            this.symbol = symbol;
            this.address = address;
            this.addressingMode = addressingMode;
            this.sign = sign;
        }
    }

    // Método para carregar o código fonte de um arquivo
    public void loadSource(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sourceLines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + filename);
            e.printStackTrace();
            hasError = true;
        }
    }

    public void firstPass() {
        for (String line : sourceLines) {
            String[] tokens = line.trim().split("\\s+");

            if (tokens.length == 0) continue; // Ignora linhas em branco

            int startIndex = 0;

            // Se a linha tem um rótulo
            if (tokens[0].endsWith(":")) {
                String label = tokens[0].substring(0, tokens[0].length() - 1);
                if (symbolTable.containsKey(label)) {
                    reportError("Símbolo redefinido: " + label);
                } else {
                    symbolTable.put(label, locationCounter); // Armazena o endereço do rótulo na tabela de símbolos
                }
                startIndex = 1; // A instrução começa após o rótulo
            }

            // Processa diretivas e instruções
            if (startIndex < tokens.length) {
                if (tokens[startIndex].equalsIgnoreCase(".WORD")) {
                    if (tokens.length > startIndex + 1 && tokens[startIndex + 1].matches("\\d+")) {
                        locationCounter += 1; // Tamanho de uma palavra
                    } else {
                        reportError("Valor inválido para .WORD: " + (tokens.length > startIndex + 1 ? tokens[startIndex + 1] : ""));
                    }
                } else {
                    locationCounter += getInstructionSize(tokens, startIndex);
                }
            }
        }
    }

    public void secondPass() {
        locationCounter = 0; // Reseta o contador de localização para a segunda passagem

        for (String line : sourceLines) {
            String[] tokens = line.trim().split("\\s+");

            if (tokens.length == 0) continue; // Ignora linhas em branco

            int startIndex = 0;

            // Ignorar rótulos na segunda passagem
            if (tokens[0].endsWith(":")) {
                startIndex = 1; // A instrução começa após o rótulo
            }

            if (startIndex < tokens.length) {
                String instruction;
                if (tokens[startIndex].equalsIgnoreCase(".WORD")) {
                    instruction = generateWordObjectCode(tokens, startIndex);
                } else {
                    instruction = generateObjectCode(tokens, startIndex);
                }
                objectCode.add(String.format("%04X %s", locationCounter, instruction));

                locationCounter += getInstructionSize(tokens, startIndex);
            }
        }

        // Atualiza a tabela de usos com os endereços corretos
        for (Usage usage : useTable) {
            if (symbolTable.containsKey(usage.symbol)) {
                usage.address = symbolTable.get(usage.symbol);
            } else {
                reportError("Símbolo não definido: " + usage.symbol);
            }
        }
    }

    private String generateObjectCode(String[] tokens, int startIndex) {
        String instruction = tokens[startIndex].toUpperCase();
        
        if (instruction.equals("CONST")) {
            if (tokens.length > startIndex + 1 && tokens[startIndex + 1].matches("\\d+")) {
                return String.format("%04X", Short.parseShort(tokens[startIndex + 1]));
            } else {
                reportError("Valor inválido para CONST: " + (tokens.length > startIndex + 1 ? tokens[startIndex + 1] : ""));
                return "0000";
            }
        }
    
        // Continue com o código para gerar o object code para outras instruções
        int opcode = getOpcode(instruction);
        short operand = 0;
    
        if (tokens.length > startIndex + 1) {
            String operandToken = tokens[startIndex + 1];
            if (symbolTable.containsKey(operandToken)) {
                operand = symbolTable.get(operandToken);
            } else if (operandToken.matches("\\d+")) {
                operand = Short.parseShort(operandToken);
            } else {
                // Adiciona na tabela de usos e não na tabela de símbolos
                useTable.add(new Usage(operandToken, locationCounter, "Direto", "+"));
                operand = 0; // Placeholder para operandos indefinidos
            }
        }
    
        return String.format("%02X%04X", opcode, operand);
    }

    private String generateWordObjectCode(String[] tokens, int startIndex) {
        if (tokens.length > startIndex + 1) {
            String operand = tokens[startIndex + 1].trim();
    
            if (operand.matches("\\d+")) {
                return String.format("%04X", Short.parseShort(operand));
            } else if (symbolTable.containsKey(operand)) {
                short value = symbolTable.get(operand);
                return String.format("%04X", value);
            } else {
                // Adiciona na tabela de usos e não na tabela de símbolos
                useTable.add(new Usage(operand, locationCounter, "Direto", "+"));
                return "0000";
            }
        } else {
            reportError("Faltando operando para .WORD");
            return "0000";
        }
    }

    private int getOpcode(String mnemonic) {
        switch (mnemonic.toUpperCase()) {
            case "ADD": return 2;
            case "BR": return 0;
            case "BRNEG": return 5;
            case "BRPOS": return 1;
            case "BRZERO": return 4;
            case "CALL": return 15;
            case "COPY": return 13;
            case "DIVIDE": return 10;
            case "LOAD": return 3;
            case "MULT": return 14;
            case "READ": return 12;
            case "RET": return 16;
            case "STOP": return 11;
            case "STORE": return 7;
            case "SUB": return 6;
            case "WRITE": return 8;
            
            // Adicionar suporte para pseudo-instruções
            case "START":
            case "END":
            case "INTDEF":
            case "INTUSE":
            case "CONST":
                return 0; // ou outro valor apropriado, caso se aplique
    
            default:
                reportError("Instrução inválida: " + mnemonic);
                return -1; // Mantém o comportamento de erro para instruções inválidas
        }
    }

    private int getInstructionSize(String[] tokens, int startIndex) {
        if (tokens.length == 0 || startIndex >= tokens.length) {
            return 0; // Nenhuma instrução
        }
    
        String instruction = tokens[startIndex].toUpperCase();
    
        // Diretivas como .WORD geralmente ocupam uma palavra
        if (instruction.equals(".WORD") || instruction.equals("CONST")) {
            return 1; // Diretiva que ocupa uma palavra
        }

        // Para cada instrução, o tamanho pode variar dependendo do opcode e do número de operandos.
        // Suponha que todas as instruções tenham um tamanho fixo para simplificação:
        return 1; // Exemplo simplificado
    }

    private void reportError(String message) {
        errors.add("Erro: " + message);
        hasError = true;
    }

    public void printErrors() {
        if (errors.isEmpty()) {
            System.out.println("Nenhum erro encontrado.");
        } else {
            System.out.println("Erros encontrados:");
            for (String error : errors) {
                System.out.println(error);
            }
        }
    }

    public void printObjectCode() {
        System.out.println("Código Objeto:");
        for (String line : objectCode) {
            System.out.println(line);
        }
    }

    public void printTables() {
        System.out.println("Tabela de Símbolos:");
        for (String symbol : symbolTable.keySet()) {
            System.out.printf("Símbolo: %s, Endereço: %04X%n", symbol, symbolTable.get(symbol));
        }

        System.out.println("\nTabela de Definições:");
        for (String symbol : definitionTable.keySet()) {
            Definition def = definitionTable.get(symbol);
            System.out.printf("Símbolo: %s, Endereço: %04X, Modo de Endereçamento: %s%n", symbol, def.address, def.addressingMode);
        }

        System.out.println("\nTabela de Usos:");
        for (Usage use : useTable) {
            System.out.printf("Símbolo: %s, Endereço: %04X, Modo de Endereçamento: %s, Sinal: %s%n", use.symbol, use.address, use.addressingMode, use.sign);
        }
    }

    public static void main(String[] args) {
        Assembler assembler = new Assembler();
        assembler.loadSource("teste.ASM");
        assembler.firstPass();
        assembler.secondPass();
        assembler.printErrors();
        assembler.printObjectCode();
        assembler.printTables();
    }
}
