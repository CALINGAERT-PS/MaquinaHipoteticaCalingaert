package errors;

public class redefinedSymbol extends Exception{
    public redefinedSymbol(String reason){
        super(reason);
    }
}   

