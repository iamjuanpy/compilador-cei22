package minijavaCompiler.semantics.ast_nodes.literal_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.types.Type;
import minijavaCompiler.semantics.types.primitives.IntType;

import static minijavaCompiler.Main.symbolTable;

public class NodeInt implements NodeLiteral{

    private Token token;

    public NodeInt(Token literal){
        this.token = literal;
    }

    public Type check(){return new IntType();}

    public void generateCode() {
        symbolTable.ceiASM_instructionList.add("    PUSH "+token.lexeme+" ; Entero");
    }
}
