import java.util.HashMap;
import java.util.Map;
/* Gerenciamento de Tabelas:
Tabela de Definição de Macros: Armazenar as definições de macros.
Tabela de Nomes de Macro (Opcional): Utilizada para distinguir macros de outro texto.
Lista de Parâmetros Reais: Para manter os parâmetros usados nas chamadas das macros */
// package main;
public class ProcessadorMacros {

    private Map<String, String> macros;
    private Map<String, String> macroNames;
    private Map<String, String> realParameters;

    public ProcessadorMacros() {
        macros = new HashMap<>();
        macroNames = new HashMap<>();
        realParameters = new HashMap<>();
    }

    public void processLinePassOne(String line) {
        
    }


    public static void main(String[] args) {
        ProcessadorMacros processador = new ProcessadorMacros();
    }
}