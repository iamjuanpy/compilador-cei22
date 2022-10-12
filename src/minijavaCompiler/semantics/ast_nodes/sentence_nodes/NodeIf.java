package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.types.primitives.BoolType;

public class NodeIf implements NodeSentence{

    private NodeExpression condition;
    private NodeSentence thenSentence;
    private NodeSentence elseSentence;

    public NodeIf(NodeExpression expression, NodeSentence thenSentence, NodeSentence elseSentence) {
        this.condition = expression;
        this.thenSentence = thenSentence;
        this.elseSentence = elseSentence;
    }

    public void check() throws SemanticException {
        if (conditionIsBooleanExpression()) {
            thenSentence.check();
            if (elseSentence != null)
                elseSentence.check();
        } else throw new SemanticException("La condicion de un bloque if debe ser una expresión booleana","",0); // COMPLETAR
    }

    private boolean conditionIsBooleanExpression() throws SemanticException {
        return condition.check().equals(new BoolType());
    }

    public boolean isVariableDeclaration() {
        return false;
    }
}
