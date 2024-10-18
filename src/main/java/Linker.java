import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Linker extends Exception{
    String output;

    public static void main(String[] args) {
        Linker link = new Linker();
        link.link2("program.obj", "program2.obj", "usefulTable.txt", "usefulTable2.txt");
    }

    public void link(String program1, String usefulTable){
        output = "";
        output += "2" + "\n";
        try (BufferedReader br = new BufferedReader(new FileReader(program1))) {
            String linha = "";
            if ((linha = br.readLine()) != null){
                output += linha;
            }   
            while ((linha = br.readLine()) != null) {
                    output += "\n" +    linha;
                }
            }
         catch (IOException e) {
            e.printStackTrace();
        }
        String nomeDoArquivo = "output.hpx"; // Nome do arquivo
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

    public void link2(String program1, String program2, String table1, String table2){
        int stack = 0;
        int lc1 = 0;
        int lc2 = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(table1))) {
            String linha = "";
            if ((linha = br.readLine()) == "null" || linha != null){
                stack = Integer.parseInt(linha.trim());
            }
            if ((linha = br.readLine()) != null) {
                lc1 = Integer.parseInt(linha.trim());
            }
        }
         catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(table1))) {
            String linha = "";
            if ((linha = br.readLine()) == "null" || linha != null){
                stack += Integer.parseInt(linha.trim());
            }
            if ((linha = br.readLine()) != null) {
                lc2 = Integer.parseInt(linha.trim());
            }
        }
         catch (IOException e) {
            e.printStackTrace();
        }
        String output = "";
        output = stack + "\n";
        try (BufferedReader br = new BufferedReader(new FileReader(program1))) {
            String linha = "";
            if ((linha = br.readLine()) != null){
                output += linha;
            }   
            while ((linha = br.readLine()) != null) {
                    output += "\n" +    linha;
                }
            }
         catch (IOException e) {
            e.printStackTrace();
        }
        output += "\n";
        try (BufferedReader br = new BufferedReader(new FileReader(program2))) {
            String linha = "";
            if ((linha = br.readLine()) != null){
                String[] temp = new String[]{
                    "",
                    "",
                    ""
                };
                String[] partes = linha.split(" ", 3);
                for (int i = 0; i < partes.length; i++) {
                    temp[i] = partes[i];
                }
                if (!temp[1].equals("")){
                    output += temp[0] + " " + (Integer.parseInt(temp[1]) + lc1);
                }
                else if (!temp[2].equals("")){
                    output += temp[0] + " " + (Integer.parseInt(temp[1]) + lc1) + (Integer.parseInt(temp[2]) + lc1);
                }
                else{
                    output += linha;
                }
            }   
            while ((linha = br.readLine()) != null) {
                String[] temp = new String[]{
                    "",
                    "",
                    ""
                };
                String[] partes = linha.split(" ", 3);
                for (int i = 0; i < partes.length; i++) {
                    temp[i] = partes[i];
                }
                if (!temp[1].equals("")){
                    if (!temp[2].equals("")){
                        output += "\n" + temp[0] + " " + (Integer.parseInt(temp[1]) + lc1) + " " + (Integer.parseInt(temp[2]) + lc1);
                    }
                    else{
                        output += "\n" + temp[0] + " " + (Integer.parseInt(temp[1]) + lc1);
                    }
                }
                else{
                    output += "\n" + linha;
                }
            }
            }
         catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // Cria um novo arquivo
            File arquivo = new File("output.hpx");
            
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
}
