package Linker;
import java.io.*;
import java.util.*;

public class Linker {

    private List<UseTable> useTable = new ArrayList<>();
    private List<GlobalSymbolTable> globalSymbols = new ArrayList<>();
    private List<String> output = new ArrayList<>();
    private List<String> words = new ArrayList<>();
    int size1 = 0, size2 = 0, position = 0, value;
    private String map1 = "", map2 = "";
    private BufferedWriter writer;
    private BufferedReader reader;

    public Linker() {
    }

    private String findGlobalSymbol(String label) {
        for (GlobalSymbolTable symbol : globalSymbols) {
            if (symbol.getLabel().equals(label)) {
                return symbol.getAddress();
            }
        }
        return null;
    }

    public void link(String path1) throws FileNotFoundException, IOException {
        this.readFile1(path1);

        if (!useTable.isEmpty()) {
            return;
        }

        String outputFile = "output.txt";

        File file = new File(outputFile);
        file.createNewFile();
        this.writer = new BufferedWriter(new FileWriter(file));
        this.writer.append(map1 + "\n");
        for (String line : this.output) {
            this.writer.append(line + "\n");
        }
        this.writer.flush();
        this.writer.close();
    }

    public void link(String path1, String path2) throws FileNotFoundException, IOException {
        // FIRST PASS
        this.readFile1(path1);
        this.readFile2(path2);

        // SECOND PASS - LINKING
        for (UseTable entry : useTable) {
            String label = entry.getLabel();
            int pos = Integer.parseInt(entry.getPosition());
            String address = this.findGlobalSymbol(label);
            if (address != null) {
                output.set(pos, address);
            } else {
                System.out.println("Error: Undefined external symbol");
            }
        }

        String outputFile = "./src/files/output.txt";
        File file = new File(outputFile);
        file.createNewFile();
        this.writer = new BufferedWriter(new FileWriter(file));
        this.writer.append(map1.concat(map2) + "\n");
        for (String line : this.output) {
            this.writer.append(String.format("%03d", Integer.parseInt(line)) + "\n");
        }
        this.writer.flush();
        this.writer.close();
    }

    public void readFile1(String path) throws FileNotFoundException, IOException {
        reader = new BufferedReader(new FileReader(path));

        String line = reader.readLine();
        while (line != null) {
            if (line.equals("SIZE")) {
                line = reader.readLine();
                size1 = Integer.parseInt(line);
            } else if (line.equals("MAP")) {
                line = reader.readLine();
                map1 = line;
            } else if (line.equals("USE_TABLE")) {
                line = reader.readLine();
                while (!line.equals("***")) {
                    words = Arrays.asList(line.split(" "));
                    useTable.add(new UseTable(words.get(0), words.get(1)));
                    line = reader.readLine();
                }
            } else if (line.equals("DEF_TABLE")) {
                line = reader.readLine();
                while (!line.equals("***")) {
                    words = Arrays.asList(line.split(" "));
                    globalSymbols.add(new GlobalSymbolTable(words.get(0), words.get(1)));
                    line = reader.readLine();
                }
            } else {
                output.add(line);
            }
            line = reader.readLine();
        }
    }

    private void readFile2(String path) throws FileNotFoundException, IOException {
        reader = new BufferedReader(new FileReader(path));

        String line = reader.readLine();
        while (line != null) {
            if (line.equals("SIZE")) {
                line = reader.readLine();
                size2 = Integer.parseInt(line);
            } else if (line.equals("MAP")) {
                line = reader.readLine();
                map2 = line;
            } else if (line.equals("USE_TABLE")) {
                line = reader.readLine();
                while (!line.equals("***")) {
                    words = Arrays.asList(line.split(" "));
                    value = Integer.parseInt(words.get(1)) + size1;
                    useTable.add(new UseTable(words.get(0), Integer.toString(value)));
                    line = reader.readLine();
                }
            } else if (line.equals("DEF_TABLE")) {
                line = reader.readLine();
                while (!line.equals("***")) {
                    words = Arrays.asList(line.split(" "));
                    value = Integer.parseInt(words.get(1)) + size1;
                    globalSymbols.add(new GlobalSymbolTable(words.get(0), Integer.toString(value)));
                    line = reader.readLine();
                }
            } else {
                if (line.equals("XXX")) {
                    output.add(line);
                } else {
                    value = Integer.parseInt(line); 
                    if (map2.charAt(position) == '1') {
                        value += size1;
                    }
                    output.add(Integer.toString(value));
                }
                position += 1;
            }
            line = reader.readLine();
        }
    }
}
