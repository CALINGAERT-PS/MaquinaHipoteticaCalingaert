import java.io.*;
import java.util.HashMap;

public class NewAssembler {

    private HashMap<String, Integer> symbolTable = new HashMap<>();
    private int LC = 0; // Contador de Localização
    private BufferedWriter objWriter;
    private BufferedWriter lstWriter;

    public static void main(String[] args) {
        NewAssembler assembler = new NewAssembler();
        assembler.assemble("C:\\\\Users\\\\Christian\\\\Desktop\\\\Códigos 2024\\\\MaquinaHipoteticaCalingaert\\\\main\\\\MASMAPRG.asm", "program.obj", "program.lst");
    }

    public void assemble(String sourceFile, String objFile, String lstFile) {
        try {
            objWriter = new BufferedWriter(new FileWriter(objFile));
            lstWriter = new BufferedWriter(new FileWriter(lstFile));

            passOne(sourceFile); // Primeira passagem para criar a tabela de símbolos
            LC = 0; // Reinicia o LC para a segunda passagem
            passTwo(sourceFile); // Segunda passagem para gerar o código de objeto e listagem

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (objWriter != null) objWriter.close();
                if (lstWriter != null) lstWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void passOne(String sourceFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLinePassOne(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processLinePassOne(String line) {
        if (line.isEmpty() || line.startsWith(";")) {
            return; // Ignora linhas vazias ou comentários
        }

        String[] parts = line.split("\\s+", 3);
        String label = null;
        String opcode;
        String operand = null;

        // Verifica se a linha começa com um label
        if (parts.length > 0) {
            if (isOpcode(parts[0])) {
                opcode = parts[0];
                if (parts.length > 1) {
                    operand = parts[1];
                }
            } else {
                label = parts[0];
                opcode = parts[1];
                if (parts.length > 2) {
                    operand = parts[2];
                }
            }

            if (label != null && !label.isEmpty()) {
                addSymbol(label);
            }
            processOpcodePassOne(opcode);
        }
    }

    private void addSymbol(String label) {
        if (!symbolTable.containsKey(label)) {
            symbolTable.put(label, LC);
        } else {
            System.out.println("Erro: Símbolo redefinido - " + label);
        }
    }

    private void processOpcodePassOne(String opcode) {
        if (opcode.equalsIgnoreCase("START")) {
            LC = 0; // Inicia o endereço no início
        } else if (!opcode.equalsIgnoreCase("END")) {
            LC += 2; // Cada instrução ocupa 2 endereços
        }
    }

    private void passTwo(String sourceFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLinePassTwo(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processLinePassTwo(String line) {
        if (line.isEmpty() || line.startsWith(";")) {
            return; // Ignora linhas vazias ou comentários
        }

        String[] parts = line.split("\\s+", 3);
        String label = null;
        String opcode = "";
        String operand = null;

        // Verifica se a linha começa com um label
        if (parts.length > 0) {
            if (isOpcode(parts[0])) {
                opcode = parts[0];
                if (parts.length > 1) {
                    operand = parts[1];
                }
            } else {
                label = parts[0];
                opcode = parts[1];
                if (parts.length > 2) {
                    operand = parts[2];
                }
            }
            processOpcodePassTwo(opcode, operand, line);
        }
    }

    private void processOpcodePassTwo(String opcode, String operand, String line) {
        try {
            String objectCode = generateObjectCode(opcode, operand);
            writeObj(objectCode);
            writeLst(LC, objectCode, line);
            if (!opcode.equalsIgnoreCase("START") && !opcode.equalsIgnoreCase("END")) {
                LC += 2; // Incrementa o LC após processar a instrução
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateObjectCode(String opcode, String operand) {
        switch (opcode.toUpperCase()) {
            case "NOP":
                return "00 0000"; // Representação para NOP
            case "STOP":
                return "11 0000"; // Representação para STOP
            case "LOAD":
                return "00 " + String.format("%03X", Integer.parseInt(operand)); // Operando deve ser decimal
            case "STORE":
                if (operand.equalsIgnoreCase("R1")) {
                    return "01 0700"; // Representação para STORE R1
                } else if (operand.equalsIgnoreCase("R2")) {
                    return "01 0701"; // Representação para STORE R2
                }
                break;
            case "ADD":
                return "02 0700"; // Representação para ADD R1
            case "SUB":
                return "06 0100"; // Representação para SUB
            case "BRZERO":
                return "05 " + String.format("%04X", symbolTable.getOrDefault(operand, 0)); // Endereço relativo ao label
            case "BR":
                return "09 " + String.format("%04X", symbolTable.getOrDefault(operand, 0)); // Endereço relativo ao label
            case "HALT":
                return "00 0000"; // Representação para HALT (opcional, ajuste conforme necessário)
            default:
                return "00 0000"; // Código padrão
        }
        return "00 0000"; // Código padrão se opcode não for reconhecido
    }

    private void writeObj(String code) throws IOException {
        if (!code.isEmpty()) {
            objWriter.write(code + "\n");
        }
    }

    private void writeLst(int lc, String objectCode, String line) throws IOException {
        String address = String.format("%04X", lc);
        String codePart = objectCode.isEmpty() ? "        " : objectCode;
        lstWriter.write(String.format("%s    %-8s%s\n", address, codePart, line));
    }

    private boolean isOpcode(String part) {
        return part.matches("[A-Z]+");
    }
}
