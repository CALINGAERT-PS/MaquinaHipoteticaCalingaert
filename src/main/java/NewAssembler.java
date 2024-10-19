import java.io.*;
import java.util.HashMap;

public class NewAssembler {

    private HashMap<String, Integer> symbolTable = new HashMap<>();
    private int LC;
    private String output = "";
    private String output2 = "";
    private String stack;

    public static void main(String[] args) {
        NewAssembler assembler = new NewAssembler();
        assembler.assemble("MASMAPRG.asm", "program.obj", "program.lst", "usefulTable.txt");
    }

    public void assemble(String sourceFile, String outputPath, String outputPath2, String outTable) {
        try (BufferedReader br = new BufferedReader(new FileReader(sourceFile))) {
            symbolTable.clear();
            LC = 0;
            output = "";
            output2 = "";
            String linha;
            Boolean start = false;
            String name = "";
            while ((linha = br.readLine()) != null) {
                if (linha.contains("START")){
                    String[] temp = linha.split("START", 2);
                    LC = 0;
                    addToTable("START", 0);
                    start = true;
                    name = temp[1];
                    continue;
                }
                if (linha.contains("END")){
                    start = false;
                }
                if (start){
                    String[] semComentario = linha.split(";", 2);
                    String[] linhaTratada = semComentario[0].split("\\s+", 2);
                    String label = linhaTratada[0];
                    String[] comando = linhaTratada[1].split("\\s+",2);
                    if(!label.equals("")){
                        if (comando[0].equals("CONST")){
                            String temp = comando[1].trim();
                            addToTable(label, Integer.parseInt(temp));
                            LC += 1;
                        }
                        else if (comando[0].equals("SPACE")){
                            addToTable(label, LC);
                            LC += 1;
                        }
                        else{
                            addToTable(label, LC);
                        }
                    }
                    switch (comando[0]) {
                        case "ADD": //2
                        LC += 2;
                            break;
                        case "BR": //0
                        LC += 2;
                            break;
                        case "BRNEG": //5
                        LC += 2;
                            break;
                        case "BRPOS": //01
                        LC += 2;
                            break;
                        case "BRZERO": //04
                        LC += 2;
                            break;
                        case "CALL": //15
                        LC += 2;
                            break;
                        case "COPY": //13
                        LC += 3;
                            break;
                        case "DIVIDE": //10
                        LC += 2;
                            break;
                        case "LOAD": //3
                        LC += 2;
                            break;
                        case "MULT": //14
                        LC += 2;
                            break;
                        case "READ": //12
                        LC += 2;
                            break;
                        case "RET": //16
                        LC += 2;
                            break;
                        case "STOP": //11
                        LC += 1;
                            break;
                        case "STORE": //7
                        LC += 2;
                            break;
                        case "SUB": //6
                        LC += 2;
                            break;
                        case "WRITE": //8
                        LC += 2;
                            break;
                        case "STACK":
                            stack = comando[1].trim();
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
            int end = 0;
            int numLinha = 0;
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
                            output += op + " " + tratarOperando(comando[1]);
                            output2 += end + " " + op + " " + tratarOperando(comando[1]) + " " + numLinha + " "+ linha + "\n";
                            end += 2;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "BR": //0
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 0 + modo;
                            output += op + " " + tratarOperando(comando[1]);
                            output2 += end + " " + op + " " + tratarOperando(comando[1]) + " " + numLinha + " "+ linha + "\n";
                            end += 2;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "BRNEG": //5
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 5 + modo;
                            output += op + " " + tratarOperando(comando[1]);
                            output2 += end + " " + op + " " + tratarOperando(comando[1]) + " " + numLinha + " "+ linha + "\n";
                            end += 2;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "BRPOS": //01
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 1 + modo;
                            output += op + " " + tratarOperando(comando[1]);
                            output2 += end + " " + op + " " + tratarOperando(comando[1]) + " " + numLinha + " "+ linha + "\n";
                            end += 2;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "BRZERO": //04
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 4 + modo;
                            output += op + " " + tratarOperando(comando[1]);
                            output2 += end + " " + op + " " + tratarOperando(comando[1]) + " " + numLinha + " "+ linha + "\n";
                            end += 2;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "CALL": //15
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 15 + modo;
                            output += op + " " + tratarOperando(comando[1]);
                            output2 += end + " " + op + " " + tratarOperando(comando[1]) + " " + numLinha + " "+ linha + "\n";
                            end += 2;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "COPY": //13
                            String[] ops = comando[1].split("\\s+",2);
                            int modo1 = readOp(ops[0], 32, 0);
                            int modo2 = readOp(ops[1], 64, 128);
                            op = 13 + modo1 + modo2;
                            output += op + " " + tratarOperando(ops[0]) +  " " +tratarOperando(ops[1]);
                            output2 += end + " " + op + " " + tratarOperando(ops[0]) + " " + tratarOperando(ops[1]) + " " + numLinha + " " + linha + "\n";
                            end += 3;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "DIVIDE": //10
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 10 + modo;
                            output += op + " " + tratarOperando(comando[1]);
                            output2 += end + " " + op + " " + tratarOperando(comando[1]) + " " + numLinha + " "+ linha + "\n";
                            end += 2;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "LOAD": //3
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 3 + modo;
                            output += op + " " + tratarOperando(comando[1]);
                            output2 += end + " " + op + " " + tratarOperando(comando[1]) + " " + numLinha + " "+ linha + "\n";
                            end += 2;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "MULT": //14
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 14 + modo;
                            output += op + " " + tratarOperando(comando[1]);
                            output2 += end + " " + op + " " + tratarOperando(comando[1]) + " " + numLinha + " "+ linha + "\n";
                            end += 2;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "READ": //12
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 12 + modo;
                            output += op + " " + tratarOperando(comando[1]);
                            output2 += end + " " + op + " " + tratarOperando(comando[1]) + " " + numLinha + " "+ linha + "\n";
                            end += 2;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "RET": //16
                            op = 16;
                            output += op ;
                            output2 += end + " " + op + " " + numLinha + " "+ linha + "\n";
                            end += 1;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "STOP": //11
                            op = 11;
                            output += op;
                            output2 += end + " " + op + " " + numLinha + " "+ linha + "\n";
                            end += 1;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "STORE": //7
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 7 + modo;
                            output += op + " " + tratarOperando(comando[1]);
                            output2 += end + " " + op + " " + tratarOperando(comando[1]) + " " + numLinha + " "+ linha + "\n";
                            end += 2;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "SUB": //6
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 6 + modo;
                            output += op + " " + tratarOperando(comando[1]);
                            output2 += end + " " + op + " " + tratarOperando(comando[1]) + " " + numLinha + " "+ linha + "\n";
                            end += 2;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "WRITE": //8
                            comando[1] = comando[1].trim();
                            modo = readOp(comando[1], 32, 128);
                            op = 8 + modo;
                            output += op + " " + tratarOperando(comando[1]);
                            output2 += end + " " + op + " " + tratarOperando(comando[1]) + " " + numLinha + " "+ linha + "\n";
                            end += 2;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "CONST":
                            comando[1] = comando[1].trim();
                            output += tratarOperando(comando[1]);
                            output2 += end + " " + tratarOperando(comando[1]) + " " + numLinha + " " + linha;
                            end += 1;
                            numLinha += 1;
                            output += "\n";
                            break;
                        case "SPACE":
                        output2 += end + "       " +  numLinha + " " +linha + "\n";
                        end += 1;
                        numLinha += 1;
                        output += "\n";
                        break;
                        default:
                            break;
                    }
                }
            }
         catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Cria um novo arquivo
            File arquivo = new File(outputPath);
            File table = new File(outTable);
            File lst = new File(outputPath2);
            System.out.println(stack);
            String tableOutput = stack + "\n";
            tableOutput += LC + "\n";
    
            // Verifica se o arquivo não existe, e cria um novo
            if (arquivo.createNewFile()) {
                System.out.println("Arquivo criado: " + arquivo.getName());
            }
            if (arquivo.createNewFile()) {
                System.out.println("Tabela criada: " + table.getName());
            }
            if (arquivo.createNewFile()) {
                System.out.println(".lst criado: " + lst.getName());
            }

            // Escreve no arquivo
            FileWriter escritor = new FileWriter(arquivo);
            escritor.write(output);
            escritor.close(); // Fecha o escritor
            FileWriter escritor2 = new FileWriter(table);
            escritor2.write(tableOutput);
            escritor2.close(); // Fecha o escritor
            FileWriter escritor3 = new FileWriter(lst);
            escritor3.write(output2);
            escritor3.close(); // Fecha o escritor

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
