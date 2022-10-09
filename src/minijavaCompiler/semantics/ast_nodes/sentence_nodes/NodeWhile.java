package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;

public class NodeWhile implements NodeSentence {

    private NodeExpression condition;
    private NodeSentence sentence;

    public NodeWhile(NodeExpression expression, NodeSentence sentence) {
        this.condition = expression;
        this.sentence = sentence;
    }

    public void check() {

    }

    public boolean isVariableDeclaration() {
        return false;
    }

}
