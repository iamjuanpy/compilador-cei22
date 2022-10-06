package minijavaCompiler.semantics.ast_nodes.literal_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.types.Type;

public class NodeString implements NodeLiteral {

    private Token token;

    public NodeString(Token literal){

    }

    public Type check(){
        return null;
    }

}
