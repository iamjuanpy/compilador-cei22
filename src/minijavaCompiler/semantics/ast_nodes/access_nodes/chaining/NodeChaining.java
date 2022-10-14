package minijavaCompiler.semantics.ast_nodes.access_nodes.chaining;

import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeOperand;
import minijavaCompiler.semantics.types.Type;

public interface NodeChaining {

    boolean isVariableAccess();
    void isMethodCall() throws SemanticException;

    void setChaining (NodeChaining chaining);

    Type check(Type previousAccessType) throws SemanticException;

}