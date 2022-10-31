package minijavaCompiler.semantics.ast_nodes.expression_nodes;

import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.types.Type;

public interface NodeExpression {

    Type check() throws SemanticException;

    void generateCode();

}
