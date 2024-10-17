import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Linker extends Exception{
    String output;

    public static void main(String[] args) {
        Linker link = new Linker();
        link.link("program.obj", "a");
    }

    public void link(String program1, String usefulTable){
        output = "";
        output += "2" + "\n";
        try (BufferedReader br = new BufferedReader(new FileReader(program1))) {
            String linha = "";
            while ((linha = br.readLine()) != null) {
                    output += linha + "\n";
                }
            }
         catch (IOException e) {
            e.printStackTrace();
        }
        String nomeDoArquivo = "src/files/output.hpx"; // Nome do arquivo
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

    public void link2(String program1, String program2){
    }
}
