package Ligador;

public class Symbol {
    String name;
    int address;
    boolean isGlobal;
    boolean isRelocatable;

    public Symbol(String name, int address, boolean isGlobal, boolean isRelocatable) {
        this.name = name;
        this.address = address;
        this.isGlobal = isGlobal;
        this.isRelocatable = isRelocatable;
    }

    @Override
    public String toString() {
        return "Symbol{name='" + name + "', address=" + address + ", isGlobal=" + isGlobal + ", isRelocatable=" + isRelocatable + '}';
    }
}
