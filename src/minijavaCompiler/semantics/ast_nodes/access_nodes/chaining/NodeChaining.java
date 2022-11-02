package minijavaCompiler.semantics.ast_nodes.access_nodes.chaining;

import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.types.Type;

public interface NodeChaining {

    boolean isVariableAccess();
    boolean isMethodCall();

    void setChaining (NodeChaining chaining);

    Type check(Type previousAccessType) throws SemanticException;

    void generateCode();
    void setIsLeftSideOfAssign();

}
