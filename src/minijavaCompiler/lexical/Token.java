package minijavaCompiler.lexical;

public class Token {

    public TokenType tokenType;
    public String lexeme;
    public int lineNumber;

    public Token(TokenType tokenType, String lexeme, int lineNumber){
        this.tokenType = tokenType;
        this.lexeme = lexeme;
        this.lineNumber = lineNumber;
    }

    public String toString(){
        return "("+tokenType+","+lexeme+","+lineNumber+")";
    }

}
