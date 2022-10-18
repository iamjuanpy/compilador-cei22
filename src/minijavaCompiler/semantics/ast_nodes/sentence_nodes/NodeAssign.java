package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.NodeAccess;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.types.Type;
import minijavaCompiler.semantics.types.primitives.IntType;

import static minijavaCompiler.lexical.TokenType.*;

public class NodeAssign implements NodeSentence{

    private NodeAccess access;
    private Token assignType;
    private NodeExpression expression;

    public NodeAssign(NodeAccess access, Token assignType, NodeExpression expression){
        this.access = access;
        this.assignType = assignType;
        this.expression = expression;
    }

    public void check() throws SemanticException {
        Type accessType = access.check();
        Type expressionType = expression.check();

        checkLeftSideIsVariable();

        if (arithmeticAssign()) {
            checkAssigningIntegers(accessType, expressionType);
        } else {
            checkAssigningSubtypes(accessType, expressionType);
        }
    }

    public boolean isReturn(){return false;}
    public Token getReturnToken() {return null;}
    public Token getToken(){return access.getToken();}

    private void checkLeftSideIsVariable() throws SemanticException {
        if (!access.isVariableAccess())
            throw new SemanticException("El operador asignacion solo puede usarse <VARIABLE> "+assignType.lexeme+" <EXPRESION>", assignType.lexeme, assignType.lineNumber);
    }

    private boolean arithmeticAssign() {return assignType.tokenType == addAssign || assignType.tokenType == subAssign;}

    private void checkAssigningIntegers(Type accessType, Type expressionType) throws SemanticException {
        if (!accessType.equals(new IntType()) || !expressionType.equals(new IntType()))
            throw new SemanticException("El operador asignacion += o -= solo se puede usar sobre int", assignType.lexeme, assignType.lineNumber);
    }

    private void checkAssigningSubtypes(Type accessType, Type expressionType) throws SemanticException {
        if (!expressionType.isSubtypeOf(accessType))
            throw new SemanticException("No se puede asignar "+ expressionType.getTypeName()+" a una variable "+ accessType.getTypeName(), assignType.lexeme, assignType.lineNumber);
    }

}
