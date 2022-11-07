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

    public void generateCode() {
        symbolTable.ceiASM_instructionList.add("    PUSH "+token.lexeme+" ; Valor de char");
    }
}
