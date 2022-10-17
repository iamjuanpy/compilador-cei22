package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.types.primitives.BoolType;

public class NodeWhile implements NodeSentence {

    private Token whileToken;
    private NodeExpression condition;
    private NodeSentence sentence;

    public NodeWhile(Token id, NodeExpression expression, NodeSentence sentence) {
        this.whileToken = id;
        this.condition = expression;
        this.sentence = sentence;
    }

    public void check() throws SemanticException {
        if (conditionIsBooleanExpression()) {
            sentence.check();
        } else throw new SemanticException("La condicion de un bloque while debe ser una expresión booleana", whileToken.lexeme, whileToken.lineNumber);
    }

    private boolean conditionIsBooleanExpression() throws SemanticException {return condition.check().equals(new BoolType());}

    public boolean isVariableDeclaration() {
        return false;
    }

}
