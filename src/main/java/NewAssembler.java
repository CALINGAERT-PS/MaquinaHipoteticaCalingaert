import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class NewAssembler {

    private HashMap<String, Integer> symbolTable = new HashMap<>();
    private int memPos;
    private String output = "";

    public static void main(String[] args) {
        NewAssembler assembler = new NewAssembler();
        assembler.assemble("main/MASMAPRG.asm", "main/program.obj", "main/program.obj");
    }

    public void assemble(String sourceFile, String outputPath, String outputPath2) {
        
            passOne(sourceFile, outputPath, outputPath2); // Primeira passagem para criar a tabela de símbolos
    }

    private void passOne(String sourceFile, String outputPath, String outputhPath2) {
        try (BufferedReader br = new BufferedReader(new FileReader(sourceFile))) {
            symbolTable.clear();
            memPos = 0;
            output = "";
            String linha;
            Boolean start = false;
            String name = "";
            while ((linha = br.readLine()) != null) {
                if (linha.contains("START")){
                    String[] temp = linha.split("START", 2);
                    memPos = 0;
                    addToTable("START", 0);
                    start = true;
                    name = temp[1];
                    continue;
                }
                if (linha.contains("END")){
                    start = false;
                }
                if (start){
                //Separar label operação operando comentario
                    String[] semComentario = linha.split(";", 2);
                    String[] linhaTratada = semComentario[0].split("\\s+", 2);
                    String label = linhaTratada[0];
                    String[] comando = linhaTratada[1].split("\\s+",2);
                    if(!label.equals("")){
                        addToTable(label, memPos);
                        System.out.println(label);
                        System.out.println(memPos);
                    }
                    switch (comando[0]) {
                        case "ADD": //2
                            memPos += 2;
                            break;
                        case "BR": //0
                            memPos += 2;
                            break;
                        case "BRNEG": //5
                            memPos += 2;
                            break;
                        case "BRPOS": //01
                            memPos += 2;
                            break;
                        case "BRZERO": //04
                            memPos += 2;
                            break;
                        case "CALL": //15
                            memPos += 2;
                            break;
                        case "COPY": //13
                            memPos += 3;
                            break;
                        case "DIVIDE": //10
                            memPos += 2;
                            break;
                        case "LOAD": //3
                            memPos += 2;
                            break;
                        case "MULT": //14
                            memPos += 2;
                            break;
                        case "READ": //12
                            memPos += 2;
                            break;
                        case "RET": //16
                            memPos += 2;
                            break;
                        case "STOP": //11
                            memPos += 1;
                            break;
                        case "STORE": //7
                            memPos += 2;
                            break;
                        case "SUB": //6
                            memPos += 2;
                            break;
                        case "WRITE": //8
                            memPos += 2;
                            break;
                        default:
                            break;
                    }
                }
            }
            }
         catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(sourceFile))) {
            String linha = "";
            while ((linha = br.readLine()) != null) {
                    String[] semComentario = linha.split(";", 2);
                    String[] linhaTratada = semComentario[0].split("\\s+", 2);
                    String label = linhaTratada[0];
                    String[] comando = linhaTratada[1].split("\\s+",2);
                    int modo;
                    int op;
                    switch (comando[0]) {
                        case "ADD": //2
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 2 + modo;
                            output += op + " " + tratarOperando(comando[1]) + "\n";
                            break;
                        case "BR": //0
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 0 + modo;
                            output += op + " " + tratarOperando(comando[1]) + "\n";
                            break;
                        case "BRNEG": //5
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 5 + modo;
                            output += op + " " + tratarOperando(comando[1]) + "\n";
                            break;
                        case "BRPOS": //01
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 1 + modo;
                            output += op + " " + tratarOperando(comando[1]) + "\n";
                            break;
                        case "BRZERO": //04
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 4 + modo;
                            output += op + " " + tratarOperando(comando[1]) + "\n";
                            break;
                        case "CALL": //15
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 15 + modo;
                            output += op + " " + tratarOperando(comando[1]) + "\n";
                            break;
                        case "COPY": //13
                            String[] ops = comando[1].split("\\s+",2);
                            int modo1 = readOp(ops[1], 32, 0);
                            int modo2 = readOp(ops[2], 64, 128);
                            op = 13 + modo1 + modo2;
                            output += op + " " + tratarOperando(ops[0]) + tratarOperando(ops[1]) + "\n";
                            break;
                        case "DIVIDE": //10
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 10 + modo;
                            output += op + " " + tratarOperando(comando[1]) + "\n";
                            break;
                        case "LOAD": //3
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            System.out.println(tratarOperando(comando[1]));
                            op = 3 + modo;
                            output += op + " " + tratarOperando(comando[1]) + "\n";
                            break;
                        case "MULT": //14
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 14 + modo;
                            output += op + " " + tratarOperando(comando[1]) + "\n";
                            break;
                        case "READ": //12
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 12 + modo;
                            output += op + " " + tratarOperando(comando[1]) + "\n";
                            break;
                        case "RET": //16
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 16 + modo;
                            output += op + " " + tratarOperando(comando[1]) + "\n";
                            break;
                        case "STOP": //11
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 11 + modo;
                            output += op + " " + tratarOperando(comando[1]) + "\n";
                            break;
                        case "STORE": //7
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 7 + modo;
                            output += op + " " + tratarOperando(comando[1]) + "\n";
                            break;
                        case "SUB": //6
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 6 + modo;
                            output += op + " " + tratarOperando(comando[1]) + "\n";
                            break;
                        case "WRITE": //8
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 8 + modo;
                            output += op + " " + tratarOperando(comando[1]) + "\n";
                            break;
                        default:
                            break;
                    }
                }
            }
         catch (IOException e) {
            e.printStackTrace();
        }
        String nomeDoArquivo = outputPath; // Nome do arquivo
        try {
            // Cria um novo arquivo
            File arquivo = new File(nomeDoArquivo);
            
            // Verifica se o arquivo não existe, e cria um novo
            if (arquivo.createNewFile()) {
                System.out.println("Arquivo criado: " + arquivo.getName());
            }

            // Escreve no arquivo
            FileWriter escritor = new FileWriter(arquivo);
            escritor.write(output);
            escritor.close(); // Fecha o escritor

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String tratarOperando(String op){
        String temp = isLabel(op);
        if(temp.contains(",")){
            String[] tratado = temp.split(",");
            return tratado[0];
        }
        if(temp.contains("#")){
            String[] tratado = temp.split("#");
            return tratado[0];
        }
        return temp;
    }

    private String isLabel(String op){
        if (symbolTable.containsKey(op)){
            int temp = symbolTable.get(op);
            return Integer.toString(temp);
        }
        return op;
    }

    private int readOp(String operando, int indireto, int imediato){
        String temp = isLabel(operando);
        if (temp.contains(",I")){
            return indireto;
        }
        if (temp.contains("#")){
            return imediato;
        }
        return 0;
    }

    private void addToTable(String operacao, int operando){
        symbolTable.put(operacao, operando);
    }
}
