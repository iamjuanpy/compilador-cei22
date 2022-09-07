package minijavaCompiler;

import minijavaCompiler.file_manager.SourceFileReader;
import minijavaCompiler.file_manager.SourceFileReaderException;
import minijavaCompiler.lexical.LexicalAnalyser;
import minijavaCompiler.lexical.exceptions.LexicalException;
import minijavaCompiler.lexical.Token;
import minijavaCompiler.lexical.TokenType;
import minijavaCompiler.syntax.SyntacticException;
import minijavaCompiler.syntax.SyntaxParser;

public class Main {
    public static void main(String [] args){

        String filePath;
        SourceFileReader sourceFileReader = null;
        LexicalAnalyser lexicalAnalyser;
        SyntaxParser syntaxParser;

        if (args.length == 1){
            try {
                filePath = args[0];
                sourceFileReader = new SourceFileReader(filePath);
                lexicalAnalyser = new LexicalAnalyser(sourceFileReader);

                boolean noError = true;
                try {
                    syntaxParser = new SyntaxParser(lexicalAnalyser);
                } catch (LexicalException | SyntacticException exception){
                    System.out.println("\n"+exception.getMessage()+"\n");
                    noError = false;
                }

                if (noError) System.out.println("\n"+"[SinErrores]");

            } catch (SourceFileReaderException exception) {
                System.out.println("\n"+exception.getMessage());
            }
        } else System.out.println("Error: no java source file as parameter");

    }
}
