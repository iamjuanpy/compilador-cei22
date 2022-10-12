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

            if (expression == null && !symbolTable.currentUnit.getReturnType().equals(new VoidType())) // return; solo valido en metodos void
                throw new SemanticException("Un metodo void no puede retornar un valor",token.lexeme, token.lineNumber);
            else if (expression != null && !expression.check().isSubtypeOf(symbolTable.currentUnit.getReturnType())) // return <exp> exp conforma con returnType
                throw new SemanticException("Un metodo "+symbolTable.currentUnit.getReturnType().getTypeName()+" no puede retornar un tipo "+expression.check().getTypeName(), token.lexeme, token.lineNumber);

        } else if (expression != null)
            throw new SemanticException("Un constructor no puede tener return no vacio",token.lexeme, token.lineNumber);
    }

    public boolean isVariableDeclaration() {
        return false;
    }

}
