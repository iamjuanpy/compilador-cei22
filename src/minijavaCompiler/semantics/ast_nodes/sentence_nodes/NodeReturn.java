package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;

public class NodeReturn implements NodeSentence{

    private Token token; // guardo el token para reportar error de return inválido?????
    private NodeExpression expression;

    public NodeReturn(NodeExpression expression) {
        this.expression = expression;
    }

    public void check() {

    }

    public boolean isVariableDeclaration() {
        return false;
    }

}
