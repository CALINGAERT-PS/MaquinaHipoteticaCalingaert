package Linker;
import java.io.IOException;
public class Main {
    public static void main(String[] args) throws IOException {
        // Caminhos para os arquivos de módulos objeto
        String path1 = "C:\\Users\\Christian\\Desktop\\Códigos 2024\\MaquinaHipoteticaCalingaert\\main\\Linker\\program.obj";
        String path2 = "C:\\Users\\Christian\\Desktop\\Códigos 2024\\MaquinaHipoteticaCalingaert\\main\\Linker\\program2.obj";
        Linker linker = new Linker();
        //System.out.println("Linking single module...");
        //linker.link(path1);

        // Teste com dois arquivos .obj (módulos 1 e 2)
        System.out.println("Linking two modules...");
        linker.link(path1, path2);
    }
}
