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

    public boolean isMethodCall() {
        if (optChaining != null)
            return optChaining.isMethodCall();
        else return false;
    }

    public Type check() throws SemanticException {
        Type expressionType = expression.check(); // Tipo de expresion parentizada = tipo de la expresion

        if (optChaining == null) {
            return expressionType;
        } else return optChaining.check(expressionType);
    }

    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }

    public void generateCode() {

    }

}
