package minijavaCompiler.lexical;

import minijavaCompiler.file_manager.SourceFileReader;

import java.util.HashMap;
import java.util.Map;

import static minijavaCompiler.lexical.TokenType.*;

public class LexicalAnalyzer {

    private SourceFileReader fileReader;
    private Map<String,TokenType> reservedWords;
    private char currentChar;
    private String lexeme;

    public LexicalAnalyzer (SourceFileReader fileReader){
        this.fileReader = fileReader;
        currentChar = fileReader.readCharacter();
        loadReservedWords();
    }

    public Token getNextToken() throws LexicalException {
        lexeme = "";
        return s0();
    }

    private void updateLexeme(){ lexeme = lexeme + currentChar;}
    private void readNextCharacter(){ currentChar = fileReader.readCharacter();}

    // AUTOMATA

    private Token s0() throws LexicalException {
        if (fileReader.isEOL(currentChar) || currentChar == '\t' || currentChar == ' ') {
            updateLexeme();
            readNextCharacter();
            return getNextToken();
        }
        else if (currentChar == '/'){
            updateLexeme();
            readNextCharacter();
            return s1();
        } else if (fileReader.isEOF(currentChar)){
            return s5();
        } else if (Character.isLetter(currentChar)){
            updateLexeme();
            readNextCharacter();
            return s6();
        } else {
            updateLexeme();
            throw new LexicalException(lexeme, fileReader.getLineNumber());
        }
    }

    private Token s1() throws LexicalException {
        if (currentChar == '/'){
            updateLexeme();
            readNextCharacter();
            return s2();
        } else if (currentChar == '*'){
            updateLexeme();
            readNextCharacter();
            return s3();
        } else if (fileReader.isEOF(currentChar)) throw new LexicalException(lexeme, fileReader.getLineNumber());
        return null; // TEMPORAL
    }

    private Token s2() throws LexicalException {
        if (fileReader.isEOL(currentChar) || fileReader.isEOF(currentChar)){
            updateLexeme();
            readNextCharacter();
            return getNextToken(); //return new Token(TokenType.useless, lexeme, fileReader.getLineNumber());
        } else {
            updateLexeme();
            readNextCharacter();
            return s2();
        }
    }


    private Token s3() throws LexicalException {
        if (currentChar == '*'){
            updateLexeme();
            readNextCharacter();
            return s4();
        } else {
            updateLexeme();
            readNextCharacter();
            return s3();
        }
    }

    private Token s4() throws LexicalException {
        if (currentChar == '/'){
            updateLexeme();
            readNextCharacter();
            return getNextToken(); //return new Token(TokenType.useless, lexeme, fileReader.getLineNumber());
        } else {
            updateLexeme();
            readNextCharacter();
            return s3();
        }
    }

    private Token s5() {
        return new Token(TokenType.eof,lexeme,fileReader.getLineNumber());
    }

    private Token s6(){
        if (Character.isLetter(currentChar)){
            updateLexeme();
            readNextCharacter();
            return s6();
        } else {
            return new Token(TokenType.mvID, lexeme, fileReader.getLineNumber());
        }
    }

    // 

    private void loadReservedWords() {
        reservedWords = new HashMap<>();
        reservedWords.put("class",r_class);
        reservedWords.put("public",r_public);
        reservedWords.put("void",r_void);
        reservedWords.put("if",r_if);
        reservedWords.put("this",r_this);
        reservedWords.put("new",r_new);
        reservedWords.put("else",r_else);
        reservedWords.put("boolean",r_boolean);
        reservedWords.put("private",r_private);
        reservedWords.put("interface",r_interface);
        reservedWords.put("extends",r_extends);
        reservedWords.put("static",r_static);
        reservedWords.put("char",r_char);
        reservedWords.put("while",r_while);
        reservedWords.put("null",r_null);
        reservedWords.put("implements",r_implements);
        reservedWords.put("int",r_int);
        reservedWords.put("return",r_return);
        reservedWords.put("var",r_var);
        reservedWords.put("true",r_true);
        reservedWords.put("false",r_false);
    }

}
