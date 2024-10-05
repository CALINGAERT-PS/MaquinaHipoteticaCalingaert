package Linker;
import java.io.IOException;
public class Main {
    public static void main(String[] args) throws IOException {
        // Caminhos para os arquivos de módulos objeto
        String path1 = "C:\\Users\\Christian\\Desktop\\Códigos 2024\\MaquinaHipoteticaCalingaert\\main\\Linker\\program.obj";
        //String path2 = "./src/files/module2.obj";

        // Cria uma instância do ligador
        Linker linker = new Linker();

        // Teste com um arquivo .obj (apenas o módulo 1)
        System.out.println("Linking single module...");
        linker.link(path1);

        // Teste com dois arquivos .obj (módulos 1 e 2)
        //System.out.println("Linking two modules...");
        //linker.link(path1, path2);
    }
}
