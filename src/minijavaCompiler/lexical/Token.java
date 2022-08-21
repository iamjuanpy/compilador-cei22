package minijavaCompiler.lexical;

public class Token {

    private TokenType tokenType;
    private String lexeme;
    private int lineNumber;

    public Token(TokenType tokenType, String lexeme, int lineNumber){
        this.tokenType = tokenType;
        this.lexeme = lexeme;
        this.lineNumber = lineNumber;
    }

    public TokenType getTokenType(){
        return tokenType;
    }

    public String getLexeme(){
        return lexeme;
    }

    public int getLineNumber(){
        return lineNumber;
    }

    public String toString(){
        return "("+tokenType+","+lexeme+","+lineNumber+")";
    }

}
