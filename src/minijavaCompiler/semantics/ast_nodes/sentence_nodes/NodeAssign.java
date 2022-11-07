package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.NodeAccess;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.types.Type;
import minijavaCompiler.semantics.types.primitives.IntType;

import static minijavaCompiler.Main.symbolTable;
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
            checkAssigningIntegersOrCoercion(accessType, expressionType);
        } else {
            checkAssigningSubtypes(accessType, expressionType);
        }
    }

    private void checkLeftSideIsVariable() throws SemanticException {
        if (!access.isVariableAccess())
            throw new SemanticException("El operador asignacion solo puede usarse <VARIABLE> "+assignType.lexeme+" <EXPRESION>", assignType.lexeme, assignType.lineNumber);
    }

    private boolean arithmeticAssign() {return assignType.tokenType == addAssign || assignType.tokenType == subAssign;}

    private void checkAssigningIntegersOrCoercion(Type accessType, Type expressionType) throws SemanticException {
        if (!accessType.isSubtypeOf(new IntType()) || !expressionType.isSubtypeOf(new IntType())) // int (o char por el logro de coercion)
            throw new SemanticException("Los operadores asignacion += o -= solo se pueden usar sobre variables int o char", assignType.lexeme, assignType.lineNumber);
    }

    private void checkAssigningSubtypes(Type accessType, Type expressionType) throws SemanticException {
        if (!expressionType.isSubtypeOf(accessType))
            throw new SemanticException("No se puede asignar "+ expressionType.getTypeName()+" a una variable "+ accessType.getTypeName(), assignType.lexeme, assignType.lineNumber);
    }

    public boolean isReturn(){return false;}

    public boolean isVariableDeclaration() {return false;}

    public void generateCode() {
        if (assignType.tokenType == assign) {
            access.setIsLeftSideOfAssign();
            expression.generateCode();
            access.generateCode();
        } else { // Convierto a += exp en a = a + exp
            access.generateCode();
            expression.generateCode();
            if (assignType.tokenType == addAssign)
                symbolTable.ceiASM_instructionList.add("    ADD ; Suma");
            else symbolTable.ceiASM_instructionList.add("    SUB ; Resta");
            access.setIsLeftSideOfAssign();
            access.generateCode();
        }
    }

}
