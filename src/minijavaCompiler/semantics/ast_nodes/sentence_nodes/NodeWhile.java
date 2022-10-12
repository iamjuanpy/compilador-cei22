package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.types.primitives.BoolType;

public class NodeWhile implements NodeSentence {

    private NodeExpression condition;
    private NodeSentence sentence;

    public NodeWhile(NodeExpression expression, NodeSentence sentence) {
        this.condition = expression;
        this.sentence = sentence;
    }

    public void check() throws SemanticException {
        if (conditionIsBooleanExpression()) {
            sentence.check();
        } else throw new SemanticException("La condicion de un bloque while debe ser una expresión booleana","",0); // COMPLETAR
    }

    private boolean conditionIsBooleanExpression() throws SemanticException {
        return condition.check().equals(new BoolType());
    }

    public boolean isVariableDeclaration() {
        return false;
    }

}
