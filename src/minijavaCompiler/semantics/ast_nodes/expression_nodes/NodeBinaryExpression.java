package minijavaCompiler.semantics.ast_nodes.expression_nodes;

import minijavaCompiler.lexical.Token;

import static minijavaCompiler.Main.symbolTable;
import static minijavaCompiler.lexical.TokenType.*;

import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.types.ReferenceType;
import minijavaCompiler.semantics.types.Type;
import minijavaCompiler.semantics.types.primitives.BoolType;
import minijavaCompiler.semantics.types.primitives.CharType;
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
        Type leftType = leftSide.check();
        Type rightType = rightSide.check();
        if (operatorIsAdd() && oneSideString(leftType,rightType) && bothSidesStringOrCoercion(leftType,rightType)) { // +
            return new ReferenceType(new Token(classID,"String",0));
        } else if (operatorIsInteger() && bothSidesIntegerOrCoercion(leftType,rightType)) // +, -, /, *, %
            return new IntType();
        else if (operatorIsRelational() && bothSidesIntegerOrCoercion(leftType,rightType)) // >, <, >=, <=
            return new BoolType();
        else if (operatorIsBoolean() && bothSidesBoolean(leftType, rightType)) // ||, &&
            return new BoolType();
        else if (operatorIsEquals() && (leftType.isSubtypeOf(rightType) || rightType.isSubtypeOf(leftType))) // ==, !=
            return new BoolType();
        else throw new SemanticException("El operador "+operator.lexeme+" funciona con tipos "+errorMsg, operator.lexeme, operator.lineNumber);
    }

    private boolean bothSidesIntegerOrCoercion(Type leftSide, Type rightSide){return leftSide.isSubtypeOf(new IntType()) && rightSide.isSubtypeOf(new IntType());} // Contempla coerción de char a int
    private boolean bothSidesBoolean(Type leftSide, Type rightSide){return leftSide.equals(new BoolType()) && rightSide.equals(new BoolType());}

    // Coercion de int/char a String
    private boolean oneSideString(Type leftType, Type rightType) {return leftType.getTypeName().equals("String") || rightType.getTypeName().equals("String");}
    private boolean bothSidesStringOrCoercion(Type leftType, Type rightType) {
        if (leftType.getTypeName().equals("String"))
            return rightType.equals(new IntType()) || rightType.equals(new CharType()) || rightType.getTypeName().equals("String");
        else return leftType.equals(new IntType()) || leftType.equals(new CharType()) || leftType.getTypeName().equals("String");
    }

    private boolean operatorIsAdd() {
        errorMsg = "String, int o char";
        return operator.tokenType == addOP;
    }

    private boolean operatorIsInteger() {
        errorMsg = "int o char";
        return operator.tokenType == addOP || operator.tokenType == subOP || operator.tokenType == divOP || operator.tokenType == multOP || operator.tokenType == modOP;
    }

    private boolean operatorIsRelational() {
        errorMsg = "int o char";
        return operator.tokenType == greater || operator.tokenType == greaterOrEquals || operator.tokenType == less || operator.tokenType == lessOrEquals ;
    }

    private boolean operatorIsBoolean() {
        errorMsg = "boolean";
        return operator.tokenType == andOP || operator.tokenType == orOP;
    }

    private boolean operatorIsEquals() {
        errorMsg = "conformantes";
        return operator.tokenType == equals || operator.tokenType == notEquals;
    }

    // Generacion de codigo

    public void generateCode() {
        // De momento no hago logro de coercion
        leftSide.generateCode();
        rightSide.generateCode();
        generateOperatorCode();
    }

    private void generateOperatorCode() {
        switch (operator.tokenType) {

            case addOP:
                symbolTable.ceiASM_instructionList.add("    ADD"); break;
            case subOP:
                symbolTable.ceiASM_instructionList.add("    SUB"); break;
            case multOP:
                symbolTable.ceiASM_instructionList.add("    MUL"); break;
            case divOP:
                symbolTable.ceiASM_instructionList.add("    DIV"); break;
            case modOP:
                symbolTable.ceiASM_instructionList.add("    MOD"); break;

            case greater:
                symbolTable.ceiASM_instructionList.add("    GT"); break;
            case greaterOrEquals:
                symbolTable.ceiASM_instructionList.add("    GE"); break;
            case less:
                symbolTable.ceiASM_instructionList.add("    LT"); break;
            case lessOrEquals:
                symbolTable.ceiASM_instructionList.add("    LE"); break;

            case andOP:
                symbolTable.ceiASM_instructionList.add("    AND"); break;
            case orOP:
                symbolTable.ceiASM_instructionList.add("    OR"); break;

            case equals:
                symbolTable.ceiASM_instructionList.add("    EQ"); break;
            case notEquals:
                symbolTable.ceiASM_instructionList.add("    NE"); break;
        }
    }
}
