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
        // Inicia a interface gráfica e conecta com a VM
        Interface gui = new Interface(vm,processadorMacros,assembler,linker);
        gui.show();
    }
}
