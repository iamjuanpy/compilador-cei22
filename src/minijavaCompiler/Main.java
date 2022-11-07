package minijavaCompiler;

import minijavaCompiler.file_manager.OutputFileWriter;
import minijavaCompiler.file_manager.SourceFileReader;
import minijavaCompiler.file_manager.FileManagerException;
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

        String filePath, outPath;
        SourceFileReader sourceFileReader = null;
        OutputFileWriter outputFileWriter = null;
        LexicalAnalyser lexicalAnalyser;
        SyntaxParser syntaxParser;

        if (args.length == 2){
            try {
                filePath = args[0];
                outPath = args[1];
                sourceFileReader = new SourceFileReader(filePath);
                outputFileWriter = new OutputFileWriter(outPath);
                lexicalAnalyser = new LexicalAnalyser(sourceFileReader);    // Etapa 1

                syntaxParser = new SyntaxParser(lexicalAnalyser);

                symbolTable = new SymbolTable();
                DefaultClasses.instanceSymbolTableDefaults();

                syntaxParser.startParse();                                  // Etapa 2/3/4
                symbolTable.checkDeclarations();                            // Etapa 3
                symbolTable.checkSentences();                               // Etapa 4

                symbolTable.setOffsets();                                   // Etapa 5
                symbolTable.generateCode();                                 // Etapa 5

                outputFileWriter.writeCodeToFile();

                System.out.println("\n"+"Compilación exitosa");
                System.out.println("\n"+"[SinErrores]");
            } catch (FileManagerException | LexicalException | SyntacticException | SemanticException exception) {
                System.out.println("\n"+exception.getMessage());
            }
        } else System.out.println("Error: ingresa un archivo como parametro y un nombre de archivo de salida");

    }
}
