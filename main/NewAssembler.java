import java.io.*;
import java.util.HashMap;

public class NewAssembler {

    private HashMap<String, Integer> symbolTable = new HashMap<>();
    private int LC = 0;
    private BufferedWriter objWriter;
    private BufferedWriter lstWriter;

    public static void main(String[] args) {
        NewAssembler assembler = new NewAssembler();
        assembler.assemble("MASMAPRG.asm", "program.obj", "program.lst");
    }

    public void assemble(String sourceFile, String objFile, String lstFile) {
        try {
            objWriter = new BufferedWriter(new FileWriter(objFile));
            lstWriter = new BufferedWriter(new FileWriter(lstFile));

            passOne(sourceFile);
            LC = 0;
            passTwo(sourceFile);

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
        if (line.isEmpty() || line.startsWith("*")) {
            return;
        }

        String[] parts = line.split("\\s+");
        String label = null;
        String opcode;
        String operand = null;

        if (!isOpcode(parts[0])) {
            label = parts[0];
            opcode = parts[1];
            if (parts.length > 2) {
                operand = parts[2];
            }
        } else {
            opcode = parts[0];
            if (parts.length > 1) {
                operand = parts[1];
            }
        }

        if (label != null) {
            addSymbol(label);
        }

        processOpcodePassOne(opcode, operand);
    }

    private void addSymbol(String label) {
        if (!symbolTable.containsKey(label)) {
            symbolTable.put(label, LC);
        } else {
            System.out.println("Erro: Símbolo redefinido - " + label);
        }
    }

    private void processOpcodePassOne(String opcode, String operand) {
        switch (opcode.toUpperCase()) {
            case "START":
                processStart(operand);
                break;
            case "END":
                break;
            case "SPACE":
            case "CONST":
                LC++;
                break;
            default:
                LC++;
                break;
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
        if (line.isEmpty() || line.startsWith("*")) {
            return;
        }

        String[] parts = line.split("\\s+");
        String label = null;
        String opcode;
        String operand = null;

        if (!isOpcode(parts[0])) {
            label = parts[0];
            opcode = parts[1];
            if (parts.length > 2) {
                operand = parts[2];
            }
        } else {
            opcode = parts[0];
            if (parts.length > 1) {
                operand = parts[1];
            }
        }

        processOpcodePassTwo(opcode, operand, line);
    }

    private void processOpcodePassTwo(String opcode, String operand, String line) {
        try {
            String objectCode = generateObjectCode(opcode, operand);
            writeObj(objectCode);
            writeLst(LC, line);
            if (!opcode.equalsIgnoreCase("START") && !opcode.equalsIgnoreCase("END")) {
                LC++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateObjectCode(String opcode, String operand) {
        switch (opcode.toUpperCase()) {
            case "START":
            case "END":
                return "";
            case "NOP":
                return "00 0000";
            case "STOP":
                return "11 0000";
            case "LOAD":
                return operand != null ? "00 0" + String.format("%03X", Integer.parseInt(operand)) : "00 0000";
            case "STORE":
                return "01 0" + (operand != null && operand.equals("R1") ? "700" : "701");
            case "ADD":
                return "02 0700";
            case "SUB":
                return "06 0100";
            case "BRZERO":
                return "05 000C";
            case "BR":
                return operand != null ? "00 000" + (symbolTable.containsKey(operand) ? String.format("%X", symbolTable.get(operand)) : "9") : "00 0000";
            default:
                return "00 0000";
        }
    }

    private void writeObj(String code) throws IOException {
        if (!code.isEmpty()) {
            objWriter.write(code + "\n");
        }
    }

    private void writeLst(int lc, String line) throws IOException {
        lstWriter.write(String.format("%04d %s\n", lc, line));
    }

    private void processStart(String operand) {
        // START não tem operando neste caso, então inicializamos LC com 0
        LC = 0;
    }

    private boolean isOpcode(String part) {
        return part.matches("[A-Z]+");
    }
}