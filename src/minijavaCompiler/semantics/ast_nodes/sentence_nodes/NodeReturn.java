package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.types.primitives.VoidType;

import static minijavaCompiler.Main.symbolTable;

public class NodeReturn implements NodeSentence{

    private Token token; // guardo el token para reportar error de return inválido?????
    private NodeExpression expression;

    public NodeReturn(Token token, NodeExpression expression) {
        this.token = token;
        this.expression = expression;
    }

    public void check() throws SemanticException {
        if (symbolTable.currentUnit.isMethod()) {

            if (returnHasValue() && methodIsVoid())
                throw new SemanticException("Un metodo tipo void no puede retornar un valor",token.lexeme, token.lineNumber);

            if (!returnHasValue() && !methodIsVoid())
                throw new SemanticException("Un metodo tipo "+symbolTable.currentUnit.getReturnType().getTypeName()+" no puede retornar nada",token.lexeme, token.lineNumber);

            if (returnHasValue() && !expressionTypeIsSubtypeOfReturnType())
                throw new SemanticException("Un metodo tipo "+symbolTable.currentUnit.getReturnType().getTypeName()+" no puede retornar un tipo "+expression.check().getTypeName(), token.lexeme, token.lineNumber);

        } else if (returnHasValue()) // Constructor
            throw new SemanticException("Un constructor no puede tener return no vacio",token.lexeme, token.lineNumber);
    }

    private boolean expressionTypeIsSubtypeOfReturnType() throws SemanticException {return expression.check().isSubtypeOf(symbolTable.currentUnit.getReturnType());}

    private boolean methodIsVoid() {return symbolTable.currentUnit.getReturnType().equals(new VoidType());}

    private boolean returnHasValue() {return expression != null;}

    public boolean isReturn(){return true;}

}
