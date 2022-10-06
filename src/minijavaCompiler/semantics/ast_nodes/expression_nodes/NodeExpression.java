package minijavaCompiler.semantics.ast_nodes.expression_nodes;

import minijavaCompiler.semantics.types.Type;

public interface NodeExpression {

    Type check();

}
