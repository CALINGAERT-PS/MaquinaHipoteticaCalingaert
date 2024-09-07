import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;

public class Assembler {
    private HashMap<String, Short> symbolTable; // Tabela de Símbolos
    private ArrayList<String> sourceLines; // Linhas do código fonte
    private ArrayList<String> objectCode; // Código objeto gerado
    private ArrayList<String> errors; // Lista de erros encontrados
    private short locationCounter; // Contador de localizações
    private boolean hasError; // Flag para erro

    public Assembler() {
        symbolTable = new HashMap<>();
        sourceLines = new ArrayList<>();
        objectCode = new ArrayList<>();
        errors = new ArrayList<>();
        locationCounter = 0;
        hasError = false;
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
                    symbolTable.put(label, locationCounter);
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
            if (symbolTable.containsKey(tokens[startIndex + 1])) {
                operand = symbolTable.get(tokens[startIndex + 1]);
            } else if (tokens[startIndex + 1].matches("\\d+")) {
                operand = Short.parseShort(tokens[startIndex + 1]);
            } else {
                reportError("Símbolo não definido: " + tokens[startIndex + 1]);
                symbolTable.put(tokens[startIndex + 1], (short) -1); // -1 ou outro valor que indica pendente
            }
        }
    
        return String.format("%02X%04X", opcode, operand);
    }

    private String generateWordObjectCode(String[] tokens, int startIndex) {
        if (tokens.length > startIndex + 1) {
            String operand = tokens[startIndex + 1].trim();
    
            // Verifica se o operando é uma expressão do tipo A(SIMBOLO)
            if (operand.startsWith("A(") && operand.endsWith(")")) {
                String symbol = operand.substring(2, operand.length() - 1).trim();
    
                // Verifica se o símbolo está definido na tabela de símbolos
                if (symbolTable.containsKey(symbol)) {
                    short value = symbolTable.get(symbol);
                    return String.format("%04X", value);
                } else {
                    reportError("Símbolo não definido: " + symbol);
                    return "0000"; // Valor padrão em caso de erro
                }
            }
    
            // Se não for uma expressão, verifica se é um número literal ou um símbolo simples
            if (operand.matches("\\d+")) {
                return String.format("%04X", Short.parseShort(operand));
            } else if (symbolTable.containsKey(operand)) {
                short value = symbolTable.get(operand);
                return String.format("%04X", value);
            } else {
                reportError("Valor inválido para CONST: " + operand);
                return "0000";
            }
        } else {
            reportError("Faltando operando para CONST");
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

        // Para cada instrução, retornar o tamanho adequado
        switch (instruction) {
            case "STOP":
            case "RET":
                return 1; // Instrucoes que ocupam 2 palavras
            case "BR":
            case "BRNEG":
            case "BRPOS":
            case "BRZERO":
            case "ADD":
            case "SUB":
            case "LOAD":
            case "STORE":
            case "DIVIDE":
            case "MULT":
            case "READ":
            case "WRITE":
            case "CALL":
                return 2; // Instruções que ocupam 2 palavras (opcode + operando)
            case "COPY":
                return 3; // Instrucoes que ocupam 3 palavras
            case "START":
            case "END":
            case "INTDEF":
            case "INTUSE":
                return 0; 

            default:
                reportError("Instrução desconhecida: " + instruction);
                return 0; // Se não for uma instrução conhecida, retornar 0
        }
    }

    private void reportError(String message) {
        System.out.println("Erro: " + message);
        errors.add(message);
        hasError = true;
    }

    public boolean hasError() {
        return hasError;
    }

    public void generateOutput(String filename) {
        if (hasError) {
            System.out.println("Montagem falhou devido a erros.");
            return;
        }

        try (PrintWriter objWriter = new PrintWriter(filename + ".OBJ");
             PrintWriter lstWriter = new PrintWriter(filename + ".LST")) {

            for (String line : objectCode) {
                objWriter.println(line.split(" ")[1]);
                lstWriter.println(line);
            }
            lstWriter.println("Nenhum erro detectado.");
        } catch (IOException e) {
            System.out.println("Erro ao escrever os arquivos de saída.");
        }
    }

    public static void main(String[] args) {
        Assembler assembler = new Assembler();

        // Carrega o arquivo fonte fornecido como argumento
        assembler.loadSource("teste.ASM");
        assembler.firstPass();
        assembler.secondPass();
        assembler.generateOutput("output");

        // Exibe os erros se houver algum
        if (assembler.hasError()) {
            for (String error : assembler.errors) {
                System.out.println(error);
            }
        }

        System.out.println(Collections.singletonList(assembler.symbolTable)); 
        System.out.println(Collections.singletonList(assembler.sourceLines)); 
        System.out.println(Collections.singletonList(assembler.objectCode)); 
    }
}
