// import javax.swing.SwingUtilities;

public class Main {
  public static void main(String[] args) {
    /*
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
        11      // STOP
    };

    // Carregar o programa na memória
    vm.loadProgram(program);
    // Configura o modo de operação
    vm.setMop((byte) 1);

    // Carregar valores em memória para os testes
    vm.setMemoryValue(8, (short) 5);   // Valor para LOAD
    vm.setMemoryValue(9, (short) 10);  // Valor a ser adicionado

    // Executar o programa
    vm.run();

    // Verificar o resultado
    System.out.println("Memória[10]: " + vm.getMemoryValue((short) 10) ); // Esperado: 15
  }
}