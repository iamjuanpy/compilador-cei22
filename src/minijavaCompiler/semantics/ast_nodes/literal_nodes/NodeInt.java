package minijavaCompiler.semantics.ast_nodes.literal_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.types.Type;

public class NodeInt implements NodeLiteral{

    private Token token;

    public NodeInt(Token literal){
        this.token = literal;
    }

    public Type check(){
        return null;
    }
}
