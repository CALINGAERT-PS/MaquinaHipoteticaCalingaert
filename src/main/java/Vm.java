import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Vm {
    private short[] memory;
    private short[] stack;
    private short pc;
    private short sp;
    private short acc;
    private byte mop;
    private short ri;
    private short re;
    private final int MEMORY_SIZE = 256;
    private int STACK_SIZE;
    private Scanner scan;

    public Vm() {
        this.memory = new short[MEMORY_SIZE];
        this.stack = new short[STACK_SIZE];
        this.pc = 0;
        this.sp = 0;
        this.acc = 0;
        this.mop = 0;
        this.ri = 0;
        this.re = 0;
        this.scan = new Scanner(System.in);
    }

    private short fetchOperand(int mode, short operand) {
        switch (mode) {
            case 0: // Direto
                return memory[operand];
            case 1: // Indireto
                return memory[memory[operand]];
            case 4: // Imediato
                return operand;
            default:
                throw new IllegalArgumentException("Modo de enderaçamento inválido");
        }
    }

    // Carrega o programa na memória
    public void loadProgram(short program[]) {
        for (int i = 0; i < program.length; i++) {
            setMemoryValue(i, program[i]);
        }
    }

    public void clear() {
        this.memory = new short[MEMORY_SIZE];
        this.stack = new short[STACK_SIZE];
        this.pc = 0;
        this.sp = 0;
        this.acc = 0;
        this.mop = 0;
        this.ri = 0;
        this.re = 0;
    }
    

    public boolean executeInstruction() {
        ri = memory[pc];
        int opcode = ri & 0x1F;     // Bits 0 a 4 determinam o opcode
        int mode = (ri >> 5) & 0x7; // Bits 5 a 7 determinam o modo de endereçamento

        switch (opcode) {
            case 2: // ADD
                re = memory[pc+1];
                acc += fetchOperand(mode, re);
                pc += 2;
                break;
            case 0: // BR
                re = memory[pc+1];
                pc = re;
                break;
            case 5: // BRNEG
                if (acc < 0) {
                    re = memory[pc+1];
                    pc = re;
                    break;
                }
                else {
                    pc += 2;
                }
                break;
            case 1: // BRPOS
                if (acc > 0) {
                    re = memory[pc+1];
                    pc = re;
                    break;
                }
                else {
                    pc += 2;
                }
                
                break;
            case 4: // BRZERO
                if (acc == 0) {
                    re = memory[pc+1];
                    pc = re;
                    break;
                }
                else {
                    pc += 2;
                }
                break;
            case 15: // CALL
                if (sp < 0) {
                    System.out.println("Error: Stack Overflow");
                    return false;
                }
                stack[sp] = pc;
                re = memory[pc+1];
                pc = fetchOperand(mode, re);
                ++sp;
                break;
            case 13: // COPY
                int mode1 = (ri >> 5) & 0x1;
                int mode2 = (ri >> 5) & 0x6;
                if (mode1 == 0){
                    mode1 = 4;
                }
                else if (mode1 == 1){
                    mode1 = 0;
                }
                if (mode2 == 2){
                    mode2 = 1;
                }
                re = memory[pc+1];
                memory[fetchOperand(mode, re)] = fetchOperand(mode2, memory[pc + 2]);
                pc += 3;
                break;
            case 10: // DIVIDE
                re = memory[pc+1];
                acc = (short) (acc / fetchOperand(mode, re));
                pc += 2;
                break;
            case 3: // LOAD
                re = memory[pc+1];
                acc = fetchOperand(mode, re);
                pc += 2;
                break;
            case 14: // MULT
                re = memory[pc+1];
                acc = (short) (acc * fetchOperand(mode, re));
                pc += 2;
                break;
            case 12: // READ
                short input = scan.nextShort();
                if (mode == 0){
                    mode = 4;
                }
                else if (mode == 1){
                    mode = 0;
                }
                re = memory[pc+1];
                memory[fetchOperand(mode, re)] = input;
                break;
            case 16: // RET
                pc = stack[sp];
                --sp;
                break;
            case 11: // STOP
                System.out.println("STOP!");
                return false;
            case 7: // STORE
                if (mode == 0){
                    mode = 4;
                }
                else if (mode == 1){
                    mode = 0;
                }
                re = memory[pc+1];
                memory[fetchOperand(mode, re)] = acc;
                pc += 2;
                break;
            case 6: // SUB
                re = memory[pc+1];
                acc = (short) (acc - fetchOperand(mode, re));
                pc += 2;
                break;
            case 8: // WRITE
                re = memory[pc+1];  
                pc += 2;
                System.out.println(fetchOperand(mode, re));
                break;
        }
        if (pc > 1014) {
            pc = 0;
        }
        return true;
    }

    public void setRe(short re) {
        this.re = re;
    }

    public void setMop(byte mop) {
        this.mop = mop;
    }

    public short getAcc() {
        return acc;
    }

    public short getPc() {
        return pc;
    }

    public short[] getMemory() {
        return memory;
    }

    public short getMemoryValue(int i) {
        return memory[i];
    }

    public void setMemoryValue(int i, short value) {
        memory[i] = value;
    }

    public short[] getStack() {
        return stack;
    }

    public short getSp() {
        return sp;
    }

    public short getRi() {
        return ri;
    }

    public short getRe() {
        return re;
    }

    public byte getMop() {
        return mop;
    }

    public void run() {
        while (executeInstruction());
    }

    public void loadProgramFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int address = 0;
            int programMemoryLimit = MEMORY_SIZE - STACK_SIZE; 
            boolean isFirstLine = true; 

            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\s+");

                if(isFirstLine){ 
                    try{
                        int tamanhoPilha = Integer.parseInt(parts[0]);
                        if (tamanhoPilha > -1 && tamanhoPilha < MEMORY_SIZE){
                            STACK_SIZE = tamanhoPilha;
                            programMemoryLimit = MEMORY_SIZE - STACK_SIZE;
                            System.out.println("PILHA TAMANHO " + STACK_SIZE);
                            isFirstLine = false;
                            continue;
                        } else {
                            System.out.println("TAMANHO INVALIDO");
                        }
                    } catch (NumberFormatException e){
                        System.out.println("TAMANHO INVALIDO");
                        return;
                    }
                }

                if (parts.length >= 1 && parts.length <= 3) {
                    try {
                        short instrucao = Short.parseShort(parts[0], 10);

                        if (instrucao == 11) {
                            System.out.println("Comando STOP"); 
                        }

                        memory[address] = instrucao;
                        address++;
                        if (parts.length >= 2) {
                            short operando1 = Short.parseShort(parts[1], 10);
                            if (address < programMemoryLimit) {
                                memory[address] = operando1;
                                address++;
                            }
                        }

                        if (parts.length == 3) {
                            short operando2 = Short.parseShort(parts[2], 10);
                            if (address < programMemoryLimit) {
                                memory[address] = operando2;
                                address++;
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Erro: Número inválido na linha: " + line);
                    }

                } else {
                    System.out.println("Erro: Formato inválido de linha no arquivo: " + line);
                }

                if (address >= programMemoryLimit) {
                    System.out.println("Erro: Programa excede o limite de memória disponível.");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        } 
    }
    

}