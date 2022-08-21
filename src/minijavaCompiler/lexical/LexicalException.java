package minijavaCompiler.lexical;

public class LexicalException extends Exception{
    public LexicalException(String lexeme, int lineNumber){
        super("[Error:"+lexeme+"|"+lineNumber+"]");
    }
}
