package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.types.Type;

public class NodeVariable implements NodeSentence{

    private Type type;
    private Token token;
    private NodeExpression value;

    public NodeVariable(Token token, Type type){

    }

    public NodeVariable(Token token, NodeExpression value) {

    }

    public void check() {

    }

}
