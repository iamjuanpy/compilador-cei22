package minijavaCompiler.semantics.ast_nodes.literal_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.lexical.TokenType;
import minijavaCompiler.semantics.types.Type;
import minijavaCompiler.semantics.types.primitives.BoolType;

import static minijavaCompiler.Main.symbolTable;

public class NodeBoolean implements NodeLiteral{

    private Token literal; // puede ser true o false

    public NodeBoolean(Token literal){
        this.literal = literal;
    }

    public Type check() {return new BoolType();}

    public void generateCode() {
        if (literal.tokenType == TokenType.r_true)
            symbolTable.ceiASM_instructionList.add("    PUSH 1");
        else symbolTable.ceiASM_instructionList.add("    PUSH 0");
    }
}
