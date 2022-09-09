package minijavaCompiler.syntax.exceptions;

import minijavaCompiler.lexical.TokenType;

public class SyntacticException extends Exception{

    public SyntacticException(String lexeme, int lineNumber) {
        super("[Error:"+lexeme+"|"+lineNumber+"]");
    }

    public SyntacticException(TokenType expectedToken, TokenType foundToken, String lexeme, int lineNumber) {
        super("Error semántico en línea "+lineNumber +": "+"Se esperaba "+ SyntaxErrorUtil.getTokenNamesMap().get(expectedToken)+
                " y se encontró "+ SyntaxErrorUtil.getTokenNamesMap().get(foundToken) +"\n"+
                "[Error:"+lexeme+"|"+lineNumber+"]");
    }

    public SyntacticException(String errorMsg, TokenType foundToken, String lexeme, int lineNumber) {
        super("Error semántico en línea "+lineNumber+": "+errorMsg+
                ", se encontró "+ SyntaxErrorUtil.getTokenNamesMap().get(foundToken) +"\n"+
                "[Error:"+lexeme+"|"+lineNumber+"]");
    }

}
