package minijavaCompiler.lexical;

public class LexicalException extends Exception{
    public LexicalException(String lexeme, int lineNumber, int colNumber){
        super("Error Léxico en linea "+lineNumber+" columna "+colNumber+"\n\n"+"[Error:"+lexeme+"|"+lineNumber+"]");
    }

    public LexicalException(String lexeme, String errorLine, int lineNumber, int colNumber){
        super(printFancyErrorMsg(lexeme, errorLine, lineNumber, colNumber));
    }

    private static String printFancyErrorMsg(String lexeme, String errorLine, int lineNumber, int colNumber){
        String errorTypeLine = "Error Léxico en linea "+lineNumber+" columna "+colNumber;
        String line = "Detalle: "; //"Lexical error in ("+lineNumber+","+colNumber+")=> ";
        String pointerLine = "";

        for (int i = 0; i < line.length() - 1; i++){
            pointerLine += " ";
        }

        for (int i = 0; i < colNumber ; i++){
            if (errorLine.charAt(i) == '\t'){
                pointerLine += '\t';
            } else pointerLine += " ";
        }
        pointerLine += '^';

        return errorTypeLine+"\n"+line+errorLine+pointerLine+"\n"+"[Error:"+lexeme+"|"+lineNumber+"]";
    }
}
