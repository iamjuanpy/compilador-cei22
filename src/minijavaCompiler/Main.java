package minijavaCompiler;

import minijavaCompiler.file_manager.SourceFileReader;
import minijavaCompiler.file_manager.SourceFileReaderException;
import minijavaCompiler.lexical.LexicalAnalyzer;
import minijavaCompiler.lexical.LexicalException;
import minijavaCompiler.lexical.Token;
import minijavaCompiler.lexical.TokenType;

public class Main {

    public static void main(String [] args){

        String filePath;
        SourceFileReader sourceFileReader = null;
        LexicalAnalyzer lexicalAnalyzer;

        if (args.length == 1){
            try {
                filePath = args[0];
                sourceFileReader = new SourceFileReader(filePath);
                lexicalAnalyzer = new LexicalAnalyzer(sourceFileReader);

                Token token;
                do {
                    token = lexicalAnalyzer.getNextToken();
                    System.out.println(token.toString());
                } while (token.getTokenType() != TokenType.eof);

                System.out.println("\n"+"[SinErrores]");

            }catch (SourceFileReaderException srException){
                System.out.println("\n"+srException.getMessage());
            }catch (LexicalException lexException){
                //printFancyErrorMsg(sourceFileReader.getLineNumber(), sourceFileReader.getColNumber(), sourceFileReader.getCurrentLine());
                System.out.println("\n"+lexException.getMessage());
            }
        } else System.out.println("Error: run the compiler with a java source file as parameter");

    }

/*    private static void printFancyErrorMsg(int lineNumber, int colNumber, String currentLine){
        String errorTypeLine = "Error Léxico en linea "+lineNumber+" columna "+colNumber;
        String line = "Detalle: "; //"Lexical error in ("+lineNumber+","+colNumber+")=> ";
        String pointerLine = "";

        for (int i = 0; i < line.length() - 1; i++){
            pointerLine += " ";
        }

        for (int i = 0; i < colNumber - 1; i++){
            if (currentLine.charAt(i) == '\t'){
                pointerLine += '\t';
            } else pointerLine += " ";
        }
        pointerLine += '^';

        System.out.println("\n"+errorTypeLine);
        System.out.println(line+currentLine+pointerLine);
    }*/

}
