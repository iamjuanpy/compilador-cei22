package minijavaCompiler.lexical.exceptions;

public class LexicalException extends Exception{

    public LexicalException(String lexeme, String errorLine, String errorType, int lineNumber, int colNumber){
        super(FancyErrorStringBuilder.getFancyErrorMsg(lexeme, errorLine, errorType, lineNumber, colNumber));
    }

}
