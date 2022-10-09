package minijavaCompiler.semantics.ast_nodes.expression_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.types.Type;
import minijavaCompiler.semantics.types.primitives.BoolType;
import minijavaCompiler.semantics.types.primitives.IntType;

import static minijavaCompiler.lexical.TokenType.*;

public class NodeUnaryExpression implements NodeExpression {

    private Token operator;
    private NodeExpression expression;

    private String errorMsg;

    public NodeUnaryExpression(Token operator, NodeExpression expression){
        this.operator = operator;
        this.expression = expression;
    }

    public Type check() throws SemanticException {
        Type tipoExpresion = expression.check();
        if (operatorIsNot() && tipoExpresion.equals(new BoolType()))
            return new BoolType();
        else if (operatorIsInt() && tipoExpresion.equals(new IntType()))
            return new IntType();
        else throw new SemanticException("El operador "+operator.lexeme+" funciona con tipo "+errorMsg, operator.lexeme, operator.lineNumber);
    }

    private boolean operatorIsInt() {
        errorMsg = "int";
        return operator.tokenType == addOP || operator.tokenType == subOP;
    }

    private boolean operatorIsNot() {
        errorMsg = "boolean";
        return operator.tokenType == not;
    }
}
