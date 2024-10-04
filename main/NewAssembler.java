import java.io.*;
import java.util.HashMap;

public class NewAssembler {

    // Tabela de simbolo
    private HashMap<String, Integer> symbolTable = new HashMap<>();

    // Location Counter (LC)
    private int LC = 0;

    // arquivos de saida
    private BufferedWriter objWriter;
    private BufferedWriter lstWriter;

    public static void main(String[] args) {
        NewAssembler assembler = new NewAssembler();
        assembler.assemble("MASMAPRG.asm", "program.obj", "program.lst");
    }

    // constructor
    public void assemble(String sourceFile, String objFile, String lstFile) {
        try {
            // criacao dos arq de saida
            objWriter = new BufferedWriter(new FileWriter(objFile));
            lstWriter = new BufferedWriter(new FileWriter(lstFile));

            // passo 1
            passOne(sourceFile);

            // reseta lc para passo 2
            LC = 0;

            // passo 2
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

    // Passo 1 - Gera a tabela de simbolos
    private void passOne(String sourceFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLinePassOne(line.trim());
            }
            System.out.println("Passagem 1 completa. Tabela de símbolos gerada.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // funcao para processar cada linha na passagem 1
    private void processLinePassOne(String line) {
        if (line.isEmpty() || line.startsWith("*")) {
            return; // Ignora comentarios e linhas vazias
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

        // Processa o label, se houver
        if (label != null) {
            addSymbol(label);
        }

        // processa instrucao do passo 1
        processOpcodePassOne(opcode, operand);
    }

    // Add a symbol to the symbol table
    private void addSymbol(String label) {
        if (symbolTable.containsKey(label)) {
            System.out.println("Erro: Simbolo redefinido - " + label);
        } else {
            symbolTable.put(label, LC);
        }
    }

    // processa opcode no passo 1 e ajusta o LC
    private void processOpcodePassOne(String opcode, String operand) {
        switch (opcode.toUpperCase()) {
            case "START":
                processStart(operand);
                break;
            case "END":
                // nao faz nada ??
                break;
            case "SPACE":
                processSpace(operand);
                break;
            case "CONST":
                processConst(operand);
                break;
            case "INTDEF":
            case "INTUSE":
                // nao faz nada ?? 
                break;
            case "STACK":
                processStack(operand);
                break;
            default:
                // ajusta lc
                LC += 2; // cada instruc ocupa 2 palavras
                break;
        }
    }

    // Passo 2 - Gera o codigo objeto e a listagem
    private void passTwo(String sourceFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLinePassTwo(line.trim());
            }
            System.out.println("Passagem 2 completa. Código objeto e listagem gerados.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Processa cada linha na passagem 2
    private void processLinePassTwo(String line) {
        if (line.isEmpty() || line.startsWith("*")) {
            return; // Ignora comentarios e linhas vazias
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

        // Resolve o opcode e gera o codigo objeto
        processOpcodePassTwo(opcode, operand, line);
    }

    // Processa opcode no passo 2 e gera o codigo objeto
    private void processOpcodePassTwo(String opcode, String operand, String line) {
        try {
            switch (opcode.toUpperCase()) {
                case "START":
                    writeLst(LC, "START", line);
                    break;
                case "END":
                    writeLst(LC, "END", line);
                    break;
                case "SPACE":
                    /// testar melhor /// 
                    writeObj("00"); // codigo aleatorio? n sei se ta certo
                    writeLst(LC, "SPACE", line);
                    LC += 1; // ajusta lc
                    break;
                case "CONST":
                    writeObj(operand); // codigo objeto para CONST
                    writeLst(LC, "CONST", line);
                    LC += 1;
                    break;
                case "INTDEF":
                case "INTUSE":
                    // nada ??? 
                    writeLst(LC, opcode, line);
                    break;
                case "STACK":
                    writeObj("00"); 
                    writeLst(LC, "STACK", line);
                    LC += Integer.parseInt(operand);
                    break;
                default:
                    // Gera cdg objeto para instrução normal
                    writeObj("XX"); // cdg fictício para instruct ( mesma coisa de cima, testar melhor)
                    writeLst(LC, opcode, line);
                    LC += 2; // msm coisa
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // escreve no arquivo .OBJ
    private void writeObj(String code) throws IOException {
        objWriter.write(code + "\n");
    }

    // escreve no arquivo .LST
    private void writeLst(int lc, String opcode, String line) throws IOException {
        lstWriter.write(String.format("%04d %s %s\n", lc, opcode, line));
    }

    // funcao para processar a diretiva START (Passo 1)
    private void processStart(String operand) {
        try {
            LC = Integer.parseInt(operand);
            System.out.println("START: LC inicializado em " + LC);
        } catch (NumberFormatException e) {
            System.out.println("Erro: Operando inválido para START");
        }
    }

    // funcao para processar a diretiva Space (Passo 1)
    private void processSpace(String operand) {
        int size = 1; // Default size
        if (operand != null) {
            try {
                size = Integer.parseInt(operand);
            } catch (NumberFormatException e) {
                System.out.println("Erro: Operando inválido para SPACE");
            }
        }
        LC += size;
    }

    // funcao para processar a diretiva Const (Passo 1)
    private void processConst(String operand) {
        LC += 1; 
    }

    // funcao para processar a diretiva STACK (Passo 1)
    private void processStack(String operand) {
        if (operand != null) {
            try {
                int stackSize = Integer.parseInt(operand);
                LC += stackSize;
            } catch (NumberFormatException e) {
                System.out.println("Erro: Operando inválido para STACK");
            }
        }
    }

    // ve se eh opcode
    private boolean isOpcode(String part) {
        return part.matches("[A-Z]+");
    }
}
