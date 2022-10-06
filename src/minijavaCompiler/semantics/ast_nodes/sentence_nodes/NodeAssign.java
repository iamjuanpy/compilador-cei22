package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.ast_nodes.access_nodes.NodeAccess;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;

public class NodeAssign implements NodeSentence{

    private NodeAccess access;
    private Token assignType;
    private NodeExpression expression;

    public void check() {

    }

}
