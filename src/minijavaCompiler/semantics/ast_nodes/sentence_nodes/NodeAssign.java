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

        if (!access.isVariableAccess())
            throw new SemanticException("El operador asignacion solo puede usarse <VARIABLE> "+assignType.lexeme+" <EXPRESSION>", assignType.lexeme, assignType.lineNumber);

        if (assignType.tokenType == addAssign || assignType.tokenType == subAssign) {
            if (!accessType.equals(new IntType()) || !expression.check().equals(new IntType()))
                throw new SemanticException("El operador asignacion += o -= solo se puede usar sobre int", assignType.lexeme, assignType.lineNumber);
        } else {
            if (!expressionType.isSubtypeOf(accessType))
                throw new SemanticException("No se puede asignar "+expressionType.getTypeName()+" a una variable "+accessType.getTypeName(), assignType.lexeme, assignType.lineNumber);
        }
    }

    public boolean isVariableDeclaration() {
        return false;
    }

}
