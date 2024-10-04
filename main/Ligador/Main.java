package Ligador;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            // criacao do ligador
            Ligador linker = new Ligador();

            // Carregar segmentos do arquivo de entrada
            linker.loadSegmentsFromFile("program.obj");

            // Primeira passagem
            linker.firstPass();

            // Segunda passagem
            linker.secondPass();

            // Gravar o arquivo de saida
            linker.writeLinkedSegmentsToFile("linked_output.obj");

            System.out.println("Ligação completa! Arquivo gerado: linked_output.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}