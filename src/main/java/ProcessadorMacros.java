import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
public class ProcessadorMacros {
    public ProcessadorMacros(){

    }
    public boolean verificaPath(String path){
        File file = new File(path);
        if(file.exists()){
            return true;
        }
        return false;
    }
    public void runProcessador(String path,boolean verifica) {
        HashMap<String, Macro> macros = new HashMap<String, Macro>();
        String caminho = path;
        String output = "";
        boolean isMacro = false;
        boolean nameLinha = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            String nameMacro = "";
            while ((linha = br.readLine()) != null) {
                //deleta linhas que são apenas comentarios
                if (linha.contains("*")){
                    continue;
                }

                //verifica se é o inicio de um macro, se for, marca a proxima linha como nome
                if (linha.contains("MACRO")){
                    isMacro = true;
                    nameLinha = true;
                }
                //salva enquanto for um macro
                else if (isMacro){
                    //definições aninhados
                    //sai do processo de macros
                    if (linha.contains("MEND")){
                        isMacro = false;
                        continue;
                    }
                    //separa o nome do resto
                    if (nameLinha){
                        Macro newMacro = new Macro();

                        //Separa do o label, operação, operando e comentario
                        String[] temp = linha.split(";", 2);
                        String[] semComentario = temp[0].split("\\s+", 3);
                        String[] operandos;
                        if(semComentario.length == 3){
                            //Tira os espaços do operando e pega eles
                            semComentario[2].trim();
                            operandos = semComentario[2].split(",");

                            //Nome do macro
                            nameMacro = semComentario[1];
                        }
                        else{
                            semComentario[1].trim();
                            operandos = semComentario[1].split(",");

                            nameMacro = semComentario[0];
                        }

                        //Armazena no macro
                        newMacro.addOperandos(operandos);

                        macros.put(nameMacro, newMacro);
                        nameLinha = false;
                    }
                    else {
                        //Adiciona a nova linha ao macro
                        Macro valorAtual = macros.get(nameMacro);
                        valorAtual.addLinha(linha);
                    }
                }
                else{
                    //Separa do o label, operação, operando e comentario
                    String[] temp = linha.split(";", 2);
                    String[] semComentario = temp[0].split("\\s+", 3);
                    String[] operandos = temp;
                    String operacao;
                    if(semComentario.length == 3){
                        //Tira os espaços do operando e pega eles
                        semComentario[2].trim();
                        operandos = semComentario[2].split(",");

                        //Nome do macro
                        operacao = semComentario[1];
                    }
                    else if(semComentario.length == 2){
                        semComentario[1].trim();
                        operandos = semComentario[1].split(",");

                        operacao = semComentario[0];
                    }
                    else{
                        operacao = semComentario[0];
                    }

                    if (macros.containsKey(operacao)){
                        Macro newMacro = macros.get(operacao);
                        String macroProcessada = newMacro.chamada(operandos);
                        output += macroProcessada;
                    }
                    else{
                        output += linha + "\n";
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String nomeDoArquivo= "";
        if(verifica == false){
            nomeDoArquivo = "MASMAPRG.asm"; // Nome do arquivo
        }
        if(verifica == true){
            nomeDoArquivo = "MASMAPRG2.asm"; // Nome do arquivo
        }
        try {
            // Cria um novo arquivo
            File arquivo = new File(nomeDoArquivo);
            
            // Verifica se o arquivo não existe, e cria um novo
            if (arquivo.createNewFile()) {
                System.out.println("Arquivo criado: " + arquivo.getName());
            }

            // Escreve no arquivo
            FileWriter escritor = new FileWriter(arquivo);
            output = removerLinhasVazias(output);
            escritor.write(output);
            escritor.close(); // Fecha o escritor

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String removerLinhasVazias(String texto) {
        // Divide o texto em linhas e filtra as não vazias
        return String.join("\n", 
            java.util.Arrays.stream(texto.split("\n"))
                .filter(linha -> !linha.trim().isEmpty())
                .toArray(String[]::new)
        );
    }
}