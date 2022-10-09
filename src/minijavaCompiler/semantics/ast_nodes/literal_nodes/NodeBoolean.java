package minijavaCompiler.semantics.ast_nodes.literal_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.types.Type;
import minijavaCompiler.semantics.types.primitives.BoolType;

public class NodeBoolean implements NodeLiteral{

    private Token literal; // puede ser true o false

    public NodeBoolean(Token literal){
        this.literal = literal;
    }

    public Type check() {return new BoolType();}
}
