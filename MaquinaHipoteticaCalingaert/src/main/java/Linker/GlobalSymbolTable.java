package Linker;

public class GlobalSymbolTable {
    private String label;
    private String address;

    public GlobalSymbolTable(String label, String address) {
        this.label = label;
        this.address = address;
    }

    public String getLabel() {
        return label;
    }

    public String getAddress() {
        return address;
    }
}
