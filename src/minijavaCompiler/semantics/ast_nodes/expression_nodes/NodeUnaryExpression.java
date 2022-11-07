package minijavaCompiler.semantics.ast_nodes.expression_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.types.Type;
import minijavaCompiler.semantics.types.primitives.BoolType;
import minijavaCompiler.semantics.types.primitives.IntType;

import static minijavaCompiler.Main.symbolTable;
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
        Type expressionType = expression.check();
        if (operatorIsNot() && isBoolean(expressionType)) // !
            return new BoolType();
        else if (operatorIsInt() && isIntegerOrCoercion(expressionType)) // +a -a
            return new IntType();
        else throw new SemanticException("El operador "+operator.lexeme+" funciona con tipo "+errorMsg, operator.lexeme, operator.lineNumber);
    }

    private boolean isBoolean(Type expressionType) {return expressionType.equals(new BoolType());}
    private boolean isIntegerOrCoercion(Type expressionType) {return expressionType.isSubtypeOf(new IntType());}

    private boolean operatorIsInt() {
        errorMsg = "int o char";
        return operator.tokenType == addOP || operator.tokenType == subOP;
    }

    private boolean operatorIsNot() {
        errorMsg = "boolean";
        return operator.tokenType == not;
    }

    // Generacion de codigo

    public void generateCode() {
        expression.generateCode();
        generateOperatorCode();
    }

    private void generateOperatorCode() {
        switch (operator.tokenType) {
            case addOP:
                break;
            case subOP:
                symbolTable.ceiASM_instructionList.add("    NEG ; Negativo");
                break;
            case not:
                symbolTable.ceiASM_instructionList.add("    NOT");
                break;
        }
    }
}
