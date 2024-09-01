public class Vm {
    public short[] memoria;
    private short[] pilha;
    private short pc;
    private short sp;
    private short acc;
    private byte mop;
    private short ri;
    private short re;

    public Vm() {
        this.memoria = new short[1014];
        this.pilha = new short[10];
        this.pc = 0;
        this.sp = 0;
        this.acc = 0;
        this.mop = 0;
        this.ri = 0;
        this.re = 0;
    }

    private short fetchOperand(int mode, short operand) {
        switch (mode) {
            case 0: // DIRETO
                return memoria[operand];
            case 1: // INDIRETO
                return memoria[memoria[operand]];
            case 4: // IMEDIATO
                return operand;
            default:
                throw new IllegalArgumentException("Modo de enderaçamento inválido");
        }
    }

    public boolean executeInstruction() {
        ri = memoria[pc];
        int opcode = ri & 0x1F; // Bits 0 a 4 determinam o opcode
        int mode = (ri >> 5) & 0x7; // Bits 5 a 7 determinam o modo de endereÃ§amento

        switch (opcode) {
            case 2: // ADD
                acc += fetchOperand(mode, memoria[pc + 1]);
                pc += 2;
                break;
            case 0: // BR
                pc = fetchOperand(mode, memoria[pc + 1]);
                break;
            case 5: // BRNEG
                if (acc < 0) {
                    pc = fetchOperand(mode, memoria[pc + 1]);
                    break;
                }
                pc += 1;
                break;
            case 1: // BRPOS
                if (acc > 0) {
                    pc = fetchOperand(mode, memoria[pc + 1]);
                    break;
                }
                pc += 1;
                break;
            case 4: // BRZERO
                if (acc == 0) {
                    pc = fetchOperand(mode, memoria[pc + 1]);
                    break;
                }
                pc += 1;
                break;
            case 15: // CALL
                if (sp < 0) {
                    System.out.println("Error: Stack Overflow");
                    return false;
                }
                pilha[sp] = pc;
                pc = fetchOperand(mode, memoria[pc + 1]);
                ++sp;
                break;
            case 13: // COPY
                re = fetchOperand(mode, memoria[pc + 1]);
                memoria[re] = fetchOperand(mode, memoria[pc + 2]);
                pc += 3;
                break;
            case 10: // DIVIDE
                re = fetchOperand(mode, memoria[pc + 1]);
                acc = (short) (acc / re);
                pc += 2;
                break;
            case 3: // LOAD
                acc = fetchOperand(mode, memoria[pc + 1]);
                pc += 2;
                break;
            case 14: // MULT
                re = fetchOperand(mode, memoria[pc + 1]);
                acc = (short) (acc * re);
                pc += 2;
                break;
            case 12: // READ
                break;
            case 16: // RET
                pc = pilha[sp];
                --sp;
                break;
            case 11: // STOP
                return false;
            case 7: // STORE
                re = fetchOperand(mode, memoria[pc + 1]);
                memoria[re] = acc;
                pc += 2;
                break;
            case 6: // SUB
                re = fetchOperand(mode, memoria[pc + 1]);
                acc = (short) (acc - re);
                pc += 2;
                break;
            case 8: // WRITE
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

    public short[] getMemoria() {
        return memoria;
    }

    public short[] getPilha() {
        return pilha;
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
        while (executeInstruction()) {
        }
    }
}