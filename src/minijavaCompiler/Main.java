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

                System.out.println("\n[SinErrores]");

            }catch (SourceFileReaderException srException){
                System.out.println(srException.getMessage());
            }catch (LexicalException lexException){
                //System.out.println("\nLexical error in ("+sourceFileReader.getLineNumber()+","+sourceFileReader.getColNumber()+")=> "+sourceFileReader.getCurrentLine());
                System.out.println(lexException.getMessage());
            }
        } else System.out.println("Error: run the compiler with a java source file as parameter");

    }

}
