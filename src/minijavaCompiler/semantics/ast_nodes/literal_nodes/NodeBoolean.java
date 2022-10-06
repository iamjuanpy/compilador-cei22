package minijavaCompiler.semantics.ast_nodes.literal_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.types.Type;

public class NodeBoolean implements NodeLiteral{

    private Token literal; // puede ser true o false

    public NodeBoolean(Token literal){

    }

    public Type check() {
        return null;
    }
}
