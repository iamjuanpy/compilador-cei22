package minijavaCompiler.lexical;

public class LexicalException extends Exception{
    public LexicalException(String lexeme, int lineNumber, int colNumber){
        super("Error Léxico en linea "+lineNumber+" columna "+colNumber+"\n\n"+"[Error:"+lexeme+"|"+lineNumber+"]");
    }
}
