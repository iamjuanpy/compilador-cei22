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

                Token token = null;
                boolean noError = true;
                do {
                    try {
                        token = lexicalAnalyser.getNextToken();
                        System.out.println(token.toString());
                    } catch (LexicalException exception){
                        System.out.println("\n"+exception.getMessage()+"\n");
                        noError = false;
                        lexicalAnalyser.recoverFromError();
                    }
                } while (token == null || token.getTokenType() != TokenType.eof);

                if (noError) System.out.println("\n"+"[SinErrores]");

            } catch (SourceFileReaderException exception) {
                System.out.println("\n"+exception.getMessage());
            }
        } else System.out.println("Error: no java source file as parameter");

    }
}
