import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;

public class Assembler {
    private HashMap<String, Short> symbolTable; // Tabela de Símbolos
    private HashMap<String, Short> definitionTable; // Tabela de Definições
    private ArrayList<String> sourceLines; // Linhas do código fonte
    private ArrayList<String> objectCode; // Código objeto gerado
    private ArrayList<String> errors; // Lista de erros encontrados
    private short locationCounter; // Contador de localizações
    private boolean hasError; // Flag para erro

    public Assembler() {
        symbolTable = new HashMap<>();
        definitionTable = new HashMap<>();
        sourceLines = new ArrayList<>();
        objectCode = new ArrayList<>();
        errors = new ArrayList<>();
        locationCounter = 0;
        hasError = false;
    }


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
        locationCounter = 0;

        for (String line : sourceLines) {
            String[] tokens = line.trim().split("\\s+");

            if (tokens.length == 0) continue; 

            int startIndex = 0;

            if (tokens[0].endsWith(":")) {
                String label = tokens[0].substring(0, tokens[0].length() - 1);
                if (symbolTable.containsKey(label)) {
                    reportError("Símbolo redefinido: " + label);
                } else {
                    symbolTable.put(label, locationCounter);
                }
                startIndex = 1;
            }

            if (startIndex < tokens.length) {
                String directive = tokens[startIndex].toUpperCase();
                if (directive.equals(".WORD") || directive.equals("CONST")) {
                    if (tokens.length > startIndex + 1 && tokens[startIndex + 1].matches("\\d+")) {
                        definitionTable.put(tokens[startIndex + 1], locationCounter);
                        locationCounter += 1; 
                    } else {
                        reportError("Valor inválido para " + directive + ": " + (tokens.length > startIndex + 1 ? tokens[startIndex + 1] : ""));
                    }
                } else {
                    locationCounter += getInstructionSize(tokens, startIndex);
                }
            }
        }
    }

    public void secondPass() {
        locationCounter = 0; 

        for (String line : sourceLines) {
            String[] tokens = line.trim().split("\\s+");

            if (tokens.length == 0) continue; 

            int startIndex = 0;

            if (tokens[0].endsWith(":")) {
                startIndex = 1; 
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
    
        int opcode = getOpcode(instruction);
        short operand = 0;
    
        if (tokens.length > startIndex + 1) {
            if (symbolTable.containsKey(tokens[startIndex + 1])) {
                operand = symbolTable.get(tokens[startIndex + 1]);
            } else if (tokens[startIndex + 1].matches("\\d+")) {
                operand = Short.parseShort(tokens[startIndex + 1]);
            } else {
                reportError("Símbolo não definido: " + tokens[startIndex + 1]);
                symbolTable.put(tokens[startIndex + 1], (short) -1); 
            }
        }
    
        return String.format("%02X%04X", opcode, operand);
    }

    private String generateWordObjectCode(String[] tokens, int startIndex) {
        if (tokens.length > startIndex + 1) {
            String operand = tokens[startIndex + 1].trim();
    
            // Verifica se o operando é um número literal
            if (operand.matches("\\d+")) {
                return String.format("%04X", Short.parseShort(operand));
            } 
            // Verifica se o operando é um símbolo simples e está definido na tabela de definições
            else if (definitionTable.containsKey(operand)) { 
                short value = definitionTable.get(operand);
                return String.format("%04X", value);
            } 
            // Caso o símbolo não esteja definido
            else {
                reportError("Símbolo não definido ou valor inválido: " + operand);
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
            
            case "START":
            case "END":
            case "INTDEF":
            case "INTUSE":
            case "CONST":
                return 0; 
    
            default:
                reportError("Instrução inválida: " + mnemonic);
                return -1; 
        }
    }

    private int getInstructionSize(String[] tokens, int startIndex) {
        if (tokens.length == 0 || startIndex >= tokens.length) {
            return 0; 
        }
    
        String instruction = tokens[startIndex].toUpperCase();
    
        if (instruction.equals(".WORD") || instruction.equals("CONST")) {
            return 1; 
        }

        // Para cada instrução, retornar o tamanho
        switch (instruction) {
            case "STOP":
            case "RET":
                return 1; 
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
                return 2; 
            case "COPY":
                return 3; 
            case "START":
            case "END":
            case "INTDEF":
            case "INTUSE":
                return 0; 

            default:
                reportError("Instrução desconhecida: " + instruction);
                return 0; 
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
        assembler.loadSource("teste.ASM");
        assembler.firstPass();
        assembler.secondPass();
        assembler.generateOutput("output");

        if (assembler.hasError()) {
            for (String error : assembler.errors) {
                System.out.println(error);
            }
        }

        System.out.println(Collections.singletonList(assembler.symbolTable));
        System.out.println(Collections.singletonList(assembler.definitionTable));
    }
}
