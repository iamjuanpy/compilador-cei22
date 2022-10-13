package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.types.Type;

public class NodeParenthesizedExp implements NodeAccess{

    private NodeExpression expression;
    private NodeChaining optChaining;

    public NodeParenthesizedExp(NodeExpression expression) {
        this.expression = expression;
    }

    public boolean isVariableAccess() {
        if (optChaining != null)
            return optChaining.isVariableAccess();
        else return false;
    }
    public void isMethodCall() throws SemanticException {
        if (optChaining != null)
            optChaining.isMethodCall();
        else throw new SemanticException("Se esperaba una llamada a método", "", 0); // TODO Que hacer
    }

    public Type check() throws SemanticException {
        Type expressionType = expression.check();
        if (optChaining != null) {
            if (expressionType.isPrimitive())
                throw new SemanticException("No se puede encadenar a tipo primitivo", "", 0); // MISMO
            return optChaining.check(expressionType);
        } else return expressionType;
    }

    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }
}
