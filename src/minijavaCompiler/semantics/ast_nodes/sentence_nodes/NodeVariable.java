package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.types.Type;

public class NodeVariable implements NodeSentence{

    private Type type;
    private Token token;
    private NodeExpression value;

    public NodeVariable(Type type, Token token){
        this.token = token;
        this.type = type;
    }

    public NodeVariable(Token token, NodeExpression value) {
        this.token = token;
        this.value = value;
    }

    public NodeVariable(Type type, Token token, NodeExpression value) {
        this.token = token;
        this.type = type;
        this.value = value;
    }

    public void check() {

    }

    public boolean isVariableDeclaration() {
        return true;
    }

}
