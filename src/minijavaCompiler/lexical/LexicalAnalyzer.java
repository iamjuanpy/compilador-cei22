package minijavaCompiler.lexical;

import minijavaCompiler.file_manager.SourceFileReader;

public class LexicalAnalyzer {

    private SourceFileReader fileReader;
    private char currentChar;
    private String lexeme;

    public LexicalAnalyzer (SourceFileReader fileReader){
        this.fileReader = fileReader;
        currentChar = fileReader.readCharacter();
    }

    public Token getNextToken() throws LexicalException {
        lexeme = "";
        return s0();
    }

    private void updateLexeme(){ lexeme = lexeme + currentChar;}
    private void readNextCharacter(){ currentChar = fileReader.readCharacter();}

    // AUTOMATA!!!

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

}
