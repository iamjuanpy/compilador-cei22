package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;

public class NodeIf implements NodeSentence{

    private NodeExpression condition;
    private NodeSentence thenSentence;
    private NodeSentence elseSentence;

    public void check() {

    }

    public boolean isVariableDeclaration() {
        return false;
    }
}
