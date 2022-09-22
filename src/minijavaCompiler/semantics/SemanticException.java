package minijavaCompiler.semantics;

public class SemanticException extends Exception{

    public SemanticException(String lexeme, int line) {
        super("[Error:"+lexeme+"|"+line+"]");
    }

    public SemanticException(String errorMesage, String lexeme, int line) {
        super("Error semántico en la línea "+line+": "+errorMesage+
                "\n\n"+"[Error:"+lexeme+"|"+line+"]");
    }
}
