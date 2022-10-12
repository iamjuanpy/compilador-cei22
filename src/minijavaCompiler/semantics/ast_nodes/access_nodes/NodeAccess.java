package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeOperand;
import minijavaCompiler.semantics.ast_nodes.sentence_nodes.NodeSentence;

public interface NodeAccess extends NodeOperand {

    void setChaining (NodeChaining chaining);

    boolean isVariableAccess();
}
