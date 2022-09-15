package minijavaCompiler.syntax.exceptions;

import minijavaCompiler.lexical.TokenType;

public class SyntacticException extends Exception{

    public SyntacticException(TokenType expectedToken, TokenType foundToken, String lexeme, int lineNumber) {
        super(buildErrorMessage("Se esperaba "+SyntaxErrorNamesUtil.getTokenNamesMap().get(expectedToken), foundToken, lexeme,lineNumber));
    }

    public SyntacticException(String errorMsg, TokenType foundToken, String lexeme, int lineNumber) {
        super(buildErrorMessage(errorMsg, foundToken, lexeme,lineNumber));
    }

    private static String buildErrorMessage(String expectedMessage, TokenType foundToken, String lexeme, int lineNumber) {
        if (lexeme.equals(SyntaxErrorNamesUtil.getTokenNamesMap().get(foundToken))) {
            return "Error semántico en línea "+lineNumber+": "+expectedMessage+
                    ", se encontró: "+SyntaxErrorNamesUtil.getTokenNamesMap().get(foundToken)+"\n"+"\n"+
                    "[Error:"+lexeme+"|"+lineNumber+"]";
        } else {
            return "Error semántico en línea "+lineNumber+": "+expectedMessage+
                    ", se encontró: "+lexeme+" ("+SyntaxErrorNamesUtil.getTokenNamesMap().get(foundToken)+")\n"+"\n"+
                    "[Error:"+lexeme+"|"+lineNumber+"]";
        }
    }

}
