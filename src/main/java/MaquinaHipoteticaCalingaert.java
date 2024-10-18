/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
/**
 *
 * @author samue
 */

public class MaquinaHipoteticaCalingaert {

    public static void main(String[] args) {
        Vm vm = new Vm();
        ProcessadorMacros processadorMacros = new ProcessadorMacros();
        Linker linker = new Linker();
        NewAssembler assembler = new NewAssembler();
        /*
         Carrega o programa de teste
        short[] program = {
            3, 8,   // LOAD 
            2, 9,   // ADD
            7, 9,   // STORE 
            11      // STOP
        };
        vm.loadProgram(program);
        vm.setMop((byte) 1);

         Carregar valores em memória para os testes
        vm.setMemoryValue(8, (short) 5);   // Valor para LOAD
        vm.setMemoryValue(9, (short) 10);  // Valor a ser adicionado
        */
        // Inicia a interface gráfica e conecta com a VM
        Interface gui = new Interface(vm,processadorMacros,assembler,linker);
        gui.show();
    }
}
