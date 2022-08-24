package minijavaCompiler.lexical;

import minijavaCompiler.file_manager.SourceFileReader;
import minijavaCompiler.file_manager.SourceFileReaderException;

import java.util.HashMap;
import java.util.Map;

import static minijavaCompiler.lexical.TokenType.*;

public class LexicalAnalyzer {

    private SourceFileReader fileReader;
    private Map<String,TokenType> reservedWords;
    private char currentChar;
    private String lexeme;
    private String firstCommentLineLexeme;
    private String firstCommentLine;
    private int commentLineNumber;
    private int commentColNumber;

    public LexicalAnalyzer (SourceFileReader fileReader) throws SourceFileReaderException {
        this.fileReader = fileReader;
        currentChar = fileReader.readCharacter();
        loadReservedWords();
    }

    public Token getNextToken() throws LexicalException, SourceFileReaderException {
        lexeme = "";
        firstCommentLine = null;
        return s0();
    }

    private void updateLexeme(){lexeme = lexeme + currentChar;}
    private void readNextCharacter() throws SourceFileReaderException {
        currentChar = fileReader.readCharacter();
    }

    // AUTOMATA

    private Token s0() throws LexicalException, SourceFileReaderException {
        if (currentChar == '/'){
            updateLexeme();
            readNextCharacter();
            return s1();
        } else if (currentChar == '('){
            updateLexeme();
            readNextCharacter();
            return s6();
        } else if (currentChar == ')'){
            updateLexeme();
            readNextCharacter();
            return s7();
        } else if (currentChar == '{'){
            updateLexeme();
            readNextCharacter();
            return s8();
        } else if (currentChar == '}'){
            updateLexeme();
            readNextCharacter();
            return s9();
        } else if (currentChar == ';'){
            updateLexeme();
            readNextCharacter();
            return s10();
        } else if (currentChar == '.'){
            updateLexeme();
            readNextCharacter();
            return s11();
        } else if (currentChar == ','){
            updateLexeme();
            readNextCharacter();
            return s12();
        } else if (currentChar == '%'){
            updateLexeme();
            readNextCharacter();
            return s13();
        } else if (currentChar == '*'){
            updateLexeme();
            readNextCharacter();
            return s14();
        } else if (currentChar == '+'){
            updateLexeme();
            readNextCharacter();
            return s15();
        } else if (currentChar == '-'){
            updateLexeme();
            readNextCharacter();
            return s17();
        } else if (currentChar == '='){
            updateLexeme();
            readNextCharacter();
            return s19();
        } else if (currentChar == '>'){
            updateLexeme();
            readNextCharacter();
            return s21();
        } else if (currentChar == '<'){
            updateLexeme();
            readNextCharacter();
            return s23();
        } else if (currentChar == '!'){
            updateLexeme();
            readNextCharacter();
            return s25();
        } else if (currentChar == '|'){
            updateLexeme();
            readNextCharacter();
            return s27();
        } else if (currentChar == '&'){
            updateLexeme();
            readNextCharacter();
            return s29();
        } else if (Character.isDigit(currentChar)){
            updateLexeme();
            readNextCharacter();
            return s31();
        } else if (currentChar == '\''){
            updateLexeme();
            readNextCharacter();
            return s32();
        } else if (currentChar == '"'){
            updateLexeme();
            readNextCharacter();
            return s36();
        } else if (Character.isUpperCase(currentChar)) {
            updateLexeme();
            readNextCharacter();
            return s39();
        } else if (Character.isLowerCase(currentChar)) {
            updateLexeme();
            readNextCharacter();
            return s40();
        }
        else if (fileReader.isEOL(currentChar) || currentChar == '\t' || currentChar == ' ') {
            updateLexeme();
            readNextCharacter();
            return getNextToken();
        } else if (fileReader.isEOF(currentChar)){
            return s5();
        } else {
            updateLexeme();
            throw new LexicalException(lexeme, fileReader.getLineNumber(), fileReader.getColNumber());
        }
    }

    private Token s1() throws LexicalException, SourceFileReaderException {
        if (currentChar == '/'){
            updateLexeme();
            readNextCharacter();
            return s2();
        } else if (currentChar == '*'){
            firstCommentLine = fileReader.getCurrentLine();
            commentLineNumber = fileReader.getLineNumber();
            commentColNumber = fileReader.getColNumber() - 1;
            updateLexeme();
            readNextCharacter();
            return s3();
        } else return new Token(divOP, lexeme, fileReader.getLineNumber());
    }

    private Token s2() throws LexicalException, SourceFileReaderException {
        if (fileReader.isEOL(currentChar) || fileReader.isEOF(currentChar)){
            updateLexeme();
            readNextCharacter();
            return getNextToken();
        } else {
            updateLexeme();
            readNextCharacter();
            return s2();
        }
    }


    private Token s3() throws LexicalException, SourceFileReaderException {
        if (currentChar == '*'){
            updateLexeme();
            readNextCharacter();
            return s4();
        } else if (!fileReader.isEOF(currentChar)){
            if (!fileReader.isEOL(currentChar)) {
                updateLexeme();
            }
            else {
                if(firstCommentLineLexeme == null) {
                    firstCommentLineLexeme = lexeme;
                }
            }
            readNextCharacter();
            return s3();
        } else throw new LexicalException(firstCommentLineLexeme, firstCommentLine, commentLineNumber, commentColNumber);
    }

    private Token s4() throws LexicalException, SourceFileReaderException {
        if (currentChar == '/'){
            updateLexeme();
            readNextCharacter();
            return getNextToken();
        } else if (!fileReader.isEOF(currentChar)){
            if (!fileReader.isEOL(currentChar)) {
                updateLexeme();
            }
            readNextCharacter();
            return s3();
        } else throw new LexicalException(firstCommentLineLexeme, firstCommentLine, commentLineNumber, commentColNumber);
    }

    private Token s5() {
        return new Token(eof,"eof",fileReader.getLineNumber());
    }

    private Token s6(){ return new Token(openBr, lexeme, fileReader.getLineNumber());}
    private Token s7(){ return new Token(closeBr, lexeme, fileReader.getLineNumber());}
    private Token s8(){ return new Token(openCurly, lexeme, fileReader.getLineNumber());}
    private Token s9(){ return new Token(closeCurly, lexeme, fileReader.getLineNumber());}
    private Token s10(){ return new Token(semicolon, lexeme, fileReader.getLineNumber());}
    private Token s11(){ return new Token(dot, lexeme, fileReader.getLineNumber());}
    private Token s12(){ return new Token(comma, lexeme, fileReader.getLineNumber());}

    private Token s13(){ return new Token(modOP, lexeme, fileReader.getLineNumber());}
    private Token s14(){ return new Token(multOP, lexeme, fileReader.getLineNumber());}

    private Token s15() throws SourceFileReaderException {
        if (currentChar == '='){
            updateLexeme();
            readNextCharacter();
            return s16();
        } else return new Token(addOP, lexeme, fileReader.getLineNumber());
    }

    private Token s16(){ return new Token(addAssign, lexeme, fileReader.getLineNumber());}

    private Token s17() throws SourceFileReaderException {
        if (currentChar == '='){
            updateLexeme();
            readNextCharacter();
            return s18();
        } else return new Token(subOP, lexeme, fileReader.getLineNumber());
    }

    private Token s18(){ return new Token(subAssign, lexeme, fileReader.getLineNumber());}

    private Token s19() throws SourceFileReaderException {
        if (currentChar == '='){
            updateLexeme();
            readNextCharacter();
            return s20();
        } else return new Token(assign, lexeme, fileReader.getLineNumber());
    }

    private Token s20(){ return new Token(equals, lexeme, fileReader.getLineNumber());}

    private Token s21() throws SourceFileReaderException {
        if (currentChar == '='){
            updateLexeme();
            readNextCharacter();
            return s22();
        } else return new Token(greater, lexeme, fileReader.getLineNumber());
    }

    private Token s22(){ return new Token(greaterOrEquals, lexeme, fileReader.getLineNumber());}

    private Token s23() throws SourceFileReaderException {
        if (currentChar == '='){
            updateLexeme();
            readNextCharacter();
            return s24();
        } else return new Token(less, lexeme, fileReader.getLineNumber());
    }
    private Token s24(){ return new Token(lessOrEquals, lexeme, fileReader.getLineNumber());}

    private Token s25() throws SourceFileReaderException {
        if (currentChar == '='){
            updateLexeme();
            readNextCharacter();
            return s26();
        } else return new Token(not, lexeme, fileReader.getLineNumber());
    }

    private Token s26(){ return new Token(notEquals, lexeme, fileReader.getLineNumber());}

    private Token s27() throws LexicalException, SourceFileReaderException {
        if (currentChar == '|'){
            updateLexeme();
            readNextCharacter();
            return s28();
        } else {
            updateLexeme();
            throw new LexicalException(lexeme, fileReader.getLineNumber(), fileReader.getColNumber());
        }
    }

    private Token s28(){ return new Token(orOP, lexeme, fileReader.getLineNumber());}

    private Token s29() throws LexicalException, SourceFileReaderException {
        if (currentChar == '&'){
            updateLexeme();
            readNextCharacter();
            return s30();
        } else {
            updateLexeme();
            throw new LexicalException(lexeme, fileReader.getLineNumber(), fileReader.getColNumber());
        }
    }

    private Token s30(){ return new Token(andOP, lexeme, fileReader.getLineNumber());}

    private Token s31() throws LexicalException, SourceFileReaderException {
        if (Character.isDigit(currentChar)){
            updateLexeme();
            readNextCharacter();
            return s31();
        } else if (lexeme.length() <= 9){
            return new Token(intLit, lexeme, fileReader.getLineNumber());
        } else {
            throw new LexicalException(lexeme, fileReader.getLineNumber(), fileReader.getColNumber());
        }
    }

    private Token s32() throws LexicalException, SourceFileReaderException {
        if (currentChar == '\''){
            updateLexeme();
            readNextCharacter();
            return s35();
        } else if (currentChar == '\\'){
            updateLexeme();
            readNextCharacter();
            return s33();
        } else if ( currentChar != '\t' && !fileReader.isEOL(currentChar) && !fileReader.isEOF(currentChar) ){
            updateLexeme();
            readNextCharacter();
            return s34();
        } else throw new LexicalException(lexeme, fileReader.getLineNumber(), fileReader.getColNumber());
    }

    private Token s33() throws LexicalException, SourceFileReaderException {
        if ( currentChar != '\t' && !fileReader.isEOL(currentChar) && !fileReader.isEOF(currentChar) ){
            updateLexeme();
            readNextCharacter();
            return s34();
        } else throw new LexicalException(lexeme, fileReader.getLineNumber(), fileReader.getColNumber());
    }

    private Token s34() throws LexicalException, SourceFileReaderException {
        if (currentChar == '\'') {
            updateLexeme();
            readNextCharacter();
            return s35();
        } else throw new LexicalException(lexeme, fileReader.getLineNumber(), fileReader.getColNumber());
    }

    private Token s35(){ return new Token(charLit, lexeme, fileReader.getLineNumber());}

    private Token s36() throws LexicalException, SourceFileReaderException {
        if (currentChar == '"'){
            updateLexeme();
            readNextCharacter();
            return s38();
        } else if (currentChar == '\\'){
            updateLexeme();
            readNextCharacter();
            return s37();
        } else if (!fileReader.isEOL(currentChar) && !fileReader.isEOF(currentChar)) {
            updateLexeme();
            readNextCharacter();
            return s36();
        } else throw new LexicalException(lexeme, fileReader.getCurrentLine(), fileReader.getLineNumber(), fileReader.getColNumber());
    }

    private Token s37() throws LexicalException, SourceFileReaderException {
        if (!fileReader.isEOL(currentChar) && !fileReader.isEOF(currentChar)) {
            updateLexeme();
            readNextCharacter();
            return s36();
        } else throw new LexicalException(lexeme, fileReader.getLineNumber(), fileReader.getColNumber());
    }

    private Token s38() { return new Token(strLit, lexeme, fileReader.getLineNumber());}

    private Token s39() throws SourceFileReaderException {
        if (Character.isLetterOrDigit(currentChar) || currentChar == '_'){
            updateLexeme();
            readNextCharacter();
            return s39();
        } else return new Token(classID, lexeme, fileReader.getLineNumber());
    }

    private Token s40() throws SourceFileReaderException {
        if (Character.isLetterOrDigit(currentChar) || currentChar == '_'){
            updateLexeme();
            readNextCharacter();
            return s40();
        } else {
            TokenType rwType;
            if ((rwType = reservedWords.get(lexeme)) != null) {
                return new Token(rwType, lexeme, fileReader.getLineNumber());
            }
            else return new Token(mvID, lexeme, fileReader.getLineNumber());
        }
    }

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

