package minijavaCompiler.semantics.ast_nodes.literal_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.types.Type;
import minijavaCompiler.semantics.types.primitives.CharType;

public class NodeChar implements NodeLiteral{

    private Token token;

    public NodeChar(Token literal){
        this.token = literal;
    }

    public Type check(){return new CharType(); }
}
