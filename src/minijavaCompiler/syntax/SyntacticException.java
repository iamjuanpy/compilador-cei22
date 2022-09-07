package minijavaCompiler.syntax;

public class SyntacticException extends Exception{

    public SyntacticException(String lexeme, int lineNumber) {
        super("[Error:"+lexeme+"|"+lineNumber+"]");
    }
}
