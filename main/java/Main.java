// import static org.junit.jupiter.api.Assertions.assertEquals;

// import org.junit.jupiter.api.Test;
import javax.swing.SwingUtilities;
public class Main {
  public static void main(String[] args) {
   /* System.out.println("Hello world!");

    SwingUtilities.invokeLater(() -> {
            InterfaceGrafica tela = new InterfaceGrafica();
            tela.setVisible(true);
        });
    }
  */
  Vm vm = new Vm();


  short[] program = {
      3, 8,   // LOAD 0
      2, 9,   // ADD 1
      7, 9,   // STORE 2
      11         // STOP
  };

  // Carregar o programa na memória
  for (int i = 0; i < program.length; i++) {
      vm.memoria[i] = program[i];
  }

  // Carregar valores em memória para os testes
  vm.memoria[8] = 5;   // Valor para LOAD
  vm.memoria[9] = 10;  // Valor a ser adicionado

  // Executar o programa
  vm.run();

  // Verificar o resultado
  System.out.println("Memória[10]: " + vm.memoria[10]); // Esperado: 15

  // @Test
  // void addition() {
  //     assertEquals(2, 1 + 1);
  // }
}
}