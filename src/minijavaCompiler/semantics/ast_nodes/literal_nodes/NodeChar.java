package minijavaCompiler.semantics.ast_nodes.literal_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.types.Type;
import minijavaCompiler.semantics.types.primitives.CharType;

import static minijavaCompiler.Main.symbolTable;

public class NodeChar implements NodeLiteral{

    private Token token;

    public NodeChar(Token literal){
        this.token = literal;
    }

    public Type check(){return new CharType(); }

    //if (token.lexeme.length() != 8) TODO ver
    //    c = token.lexeme.charAt(1);
    //else c = Integer.parseInt(token.lexeme.substring(3, 7),16);
    public void generateCode() {
        int c = token.lexeme.charAt(1);
        symbolTable.ceiASM_instructionList.add("    PUSH "+c);
    }
}
