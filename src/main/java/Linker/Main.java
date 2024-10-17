package Linker;
import java.io.IOException;
public class Main {
    public static void main(String[] args) throws IOException {
        // Caminhos para os arquivos de módulos objeto
        String path1 = "program.obj";

        Linker linker = new Linker();

        linker.link(path1);

    }
}
