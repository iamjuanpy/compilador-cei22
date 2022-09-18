package minijavaCompiler.semantics;

public class SemanticException extends Exception{

    public SemanticException(String name, int line) {
        super("[Error:"+name+"|"+line+"]");
    }
}
