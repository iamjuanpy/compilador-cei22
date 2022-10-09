package minijavaCompiler.semantics.ast_nodes.literal_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.lexical.TokenType;
import minijavaCompiler.semantics.types.ReferenceType;
import minijavaCompiler.semantics.types.Type;

public class NodeString implements NodeLiteral {

    private Token token;

    public NodeString(Token literal){
        this.token = literal;
    }

    public Type check(){return new ReferenceType(new Token(TokenType.classID, "String", 0));}

}
