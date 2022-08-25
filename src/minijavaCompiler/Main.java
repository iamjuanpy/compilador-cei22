package minijavaCompiler;

import minijavaCompiler.file_manager.SourceFileReader;
import minijavaCompiler.file_manager.SourceFileReaderException;
import minijavaCompiler.lexical.LexicalAnalyser;
import minijavaCompiler.lexical.exceptions.LexicalException;
import minijavaCompiler.lexical.Token;
import minijavaCompiler.lexical.TokenType;

public class Main {

    public static void main(String [] args){

        String filePath;
        SourceFileReader sourceFileReader = null;
        LexicalAnalyser lexicalAnalyser;

        if (args.length == 1){
            try {
                filePath = args[0];
                sourceFileReader = new SourceFileReader(filePath);
                lexicalAnalyser = new LexicalAnalyser(sourceFileReader);

                Token token;
                do {
                    token = lexicalAnalyser.getNextToken();
                    System.out.println(token.toString());
                } while (token.getTokenType() != TokenType.eof);

                System.out.println("\n"+"[SinErrores]");

            }catch (SourceFileReaderException | LexicalException exception) {
                System.out.println("\n" + exception.getMessage());
            }
        } else System.out.println("Error: run the compiler with a java source file as parameter");

    }

}
