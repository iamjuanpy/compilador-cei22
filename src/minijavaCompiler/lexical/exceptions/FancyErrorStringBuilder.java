package minijavaCompiler.lexical.exceptions;

public class FancyErrorStringBuilder {

    public static String getFancyErrorMsg(String lexeme, String errorLine, String errorType, int lineNumber, int colNumber){
        String errorTypeLine = "Error Léxico en linea "+lineNumber+" columna "+colNumber+": "+errorType;
        String detailLine = "Detalle: ";
        String pointerLine = "";

        for (int i = 0; i < detailLine.length(); i++){
            pointerLine += " ";
        }

        for (int i = 0; i < colNumber - 1 ; i++){
            if (errorLine.charAt(i) == '\t'){
                pointerLine += '\t';
            } else pointerLine += " ";
        }
        pointerLine += '^';

        return errorTypeLine+"\n"+detailLine+errorLine+pointerLine+"\n"+"[Error:"+lexeme+"|"+lineNumber+"]";
    }

}
