package minijavaCompiler.semantics.ast_nodes.expression_nodes;

import minijavaCompiler.lexical.Token;
import static minijavaCompiler.lexical.TokenType.*;

import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.types.Type;
import minijavaCompiler.semantics.types.primitives.BoolType;
import minijavaCompiler.semantics.types.primitives.IntType;

public class NodeBinaryExpression implements NodeExpression {

    private Token operator;
    private NodeExpression leftSide;
    private NodeExpression rightSide;
    private String errorMsg;

    public NodeBinaryExpression(Token operator, NodeExpression leftSide, NodeExpression rightSide){
        this.operator = operator;
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    public Type check() throws SemanticException {
        Type tipoIzq = leftSide.check();
        Type tipoDer = rightSide.check();
        if (operatorIsInteger() && tipoIzq.equals(new IntType()) && tipoDer.equals(new IntType())) // +, -, /, *, %
            return new IntType();
        else if (operatorIsRelational() && tipoIzq.equals(new IntType()) && tipoDer.equals(new IntType())) // >, <, >=, <=
            return new BoolType();
        else if (operatorIsBoolean() && tipoIzq.equals(new BoolType()) && tipoDer.equals(new BoolType())) // ||, &&
            return new BoolType();
        else if (operatorIsEquals() && (tipoIzq.isSubtypeOf(tipoDer) || tipoDer.isSubtypeOf(tipoIzq))) // ==, !=
            return new BoolType();
        else throw new SemanticException("El operador "+operator.lexeme+" funciona con tipos "+errorMsg, operator.lexeme, operator.lineNumber);
    }

    private boolean operatorIsInteger() {
        errorMsg = "int";
        return operator.tokenType == addOP || operator.tokenType == subOP || operator.tokenType == divOP || operator.tokenType == multOP || operator.tokenType == modOP;
    }

    private boolean operatorIsBoolean() {
        errorMsg = "boolean";
        return operator.tokenType == andOP || operator.tokenType == orOP;
    }

    private boolean operatorIsEquals() {
        errorMsg = "conformantes";
        return operator.tokenType == equals || operator.tokenType == notEquals;
    }

    private boolean operatorIsRelational() {
        errorMsg = "int";
        return operator.tokenType == greater || operator.tokenType == greaterOrEquals || operator.tokenType == less || operator.tokenType == lessOrEquals ;
    }
}
