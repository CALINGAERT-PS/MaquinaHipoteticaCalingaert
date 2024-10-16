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
    
        // Defina o caminho para o arquivo de saída
        String outputFile = "./src/files/output.txt";
        File file = new File(outputFile);
    
        // Certifique-se de que o diretório exista
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs(); // Cria os diretórios, se não existirem
        }
    
        // Criar o arquivo de saída
        file.createNewFile();
    
        // Verificar se a lista output está preenchida
        if (output.isEmpty()) {
            System.out.println("Output list is empty.");
        } else {
            System.out.println("Output list size: " + output.size());
        }
    
        // Escrever o resultado no arquivo
        this.writer = new BufferedWriter(new FileWriter(file));
    
        // Escrever a concatenação de map1 e map2
        this.writer.append(map1.concat(map2) + "\n");
    
        // Parte onde ocorre a conversão da string para número
        for (int i = 0; i < output.size(); i++) {
            System.out.println("Processing line: " + output.get(i)); // Debug para ver o conteúdo
            String line = output.get(i).trim(); // Remover espaços extras
            if (line.contains(" ")) {
                String[] parts = line.split(" ");
                int firstPart;
                int secondPart;
    
                if (parts[0].matches("[0-9A-Fa-f]+")) {
                    firstPart = Integer.parseInt(parts[0], 16); // Hexadecimal
                } else {
                    firstPart = Integer.parseInt(parts[0]); // Decimal
                }
    
                if (parts[1].matches("[0-9A-Fa-f]+")) {
                    secondPart = Integer.parseInt(parts[1], 16); // Hexadecimal
                } else {
                    secondPart = Integer.parseInt(parts[1]); // Decimal
                }
    
                output.set(i, String.format("%03d %04d", firstPart, secondPart));
            } else {
                if (line.matches("[0-9A-Fa-f]+")) {
                    int value = Integer.parseInt(line, 16); // Hexadecimal
                    output.set(i, String.format("%03d", value));
                } else {
                    int value = Integer.parseInt(line);
                    output.set(i, String.format("%03d", value));
                }
            }
        }
    
        // Escrever o conteúdo da lista output
        for (String line : output) {
            this.writer.append(line + "\n");
        }
    
        this.writer.flush();
        this.writer.close();
    
        // Confirmar o caminho do arquivo de saída
        System.out.println("Output file created at: " + file.getAbsolutePath());
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
                map2 = line; // Armazena o mapa de relocação
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
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    int code = Integer.parseInt(parts[0], 16); // Código em hexadecimal
                    value = Integer.parseInt(parts[1], 16); // Valor em hexadecimal

                    // Verifica se a string `map2` tem o tamanho necessário para acessar `position`
                    if (position < map2.length() && map2.charAt(position) == '1') {
                        value += size1;
                    }
                    output.add(String.format("%02X %04X", code, value)); // Adiciona em formato hexadecimal
                }
                position += 1;
            }
            line = reader.readLine();
        }
    }

}
