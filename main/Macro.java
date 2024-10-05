import java.util.ArrayList;
import java.util.List;

public class Macro {
    private List<String> linhas;
    private String[] operandos;

    public Macro(){
        linhas = new ArrayList<String>();
    };

    public void addLinha(String linha){
        this.linhas.add(linha);
    }

    public void addOperandos(String[] operandos){
        this.operandos = operandos;
    }

    public String chamada(String[] operandos){
        String output = "";
        for (String string : linhas) {
            for (int i = 0; i < this.operandos.length; i++) {
                string = string.replace(this.operandos[i], operandos[i]);
            }
            output += string + "\n";
        }
        return output;
    }
}
