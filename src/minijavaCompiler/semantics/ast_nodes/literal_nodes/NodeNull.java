package minijavaCompiler.semantics.ast_nodes.literal_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.types.NullType;
import minijavaCompiler.semantics.types.Type;

public class NodeNull implements NodeLiteral{

    private Token token;

    public NodeNull (Token literal){
        this.token = literal;
    }

    public Type check(){return new NullType();}

}
