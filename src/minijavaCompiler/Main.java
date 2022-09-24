package minijavaCompiler;

import minijavaCompiler.file_manager.SourceFileReader;
import minijavaCompiler.file_manager.SourceFileReaderException;
import minijavaCompiler.lexical.LexicalAnalyser;
import minijavaCompiler.lexical.exceptions.LexicalException;
import minijavaCompiler.semantics.DefaultClasses;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.SymbolTable;
import minijavaCompiler.syntax.exceptions.SyntacticException;
import minijavaCompiler.syntax.SyntaxParser;

public class Main {

    public static SymbolTable symbolTable;

    public static void main(String [] args){

        String filePath;
        SourceFileReader sourceFileReader = null;
        LexicalAnalyser lexicalAnalyser;
        SyntaxParser syntaxParser;

        if (args.length == 1){
            try {
                filePath = args[0];
                sourceFileReader = new SourceFileReader(filePath);
                lexicalAnalyser = new LexicalAnalyser(sourceFileReader);    // Etapa 1

                syntaxParser = new SyntaxParser(lexicalAnalyser);
                symbolTable = new SymbolTable();
                DefaultClasses.instanceSymbolTableDefaults();

                syntaxParser.startParse();                                  // Etapa 2/3
                symbolTable.checkDeclarations();                            // Etapa 3, linea a comentar para probar constructores/variables clasicas

                System.out.println("\n"+"Compilación exitosa");
                System.out.println("\n"+"[SinErrores]");
            } catch (SourceFileReaderException | LexicalException | SyntacticException | SemanticException exception) {
                System.out.println("\n"+exception.getMessage());
            }
        } else System.out.println("Error: ingresa un archivo como parametro");

    }
}
