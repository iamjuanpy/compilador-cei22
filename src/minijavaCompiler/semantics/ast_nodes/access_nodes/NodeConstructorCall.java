package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.entries.Parameter;
import minijavaCompiler.semantics.types.ReferenceType;
import minijavaCompiler.semantics.types.Type;

import java.util.Iterator;
import java.util.List;

import static minijavaCompiler.Main.symbolTable;

public class NodeConstructorCall implements NodeAccess{

    private Token token;
    private List<NodeExpression> actualParameters;
    private NodeChaining optChaining;

    public NodeConstructorCall(Token id){
        this.token = id;
    }

    public void setParameterList(List<NodeExpression> actualParameters){
        this.actualParameters = actualParameters;
    }

    public boolean isVariableAccess() {
        if (optChaining != null)
            return optChaining.isVariableAccess();
        else return false;
    }

    public boolean isMethodAccess() {
        if (optChaining == null)
            return true;
        else return optChaining.isMethodAccess();
    }

    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }

    public Type check() throws SemanticException {
        if (symbolTable.getClass(token.lexeme) != null && symbolTable.getClass(token.lexeme).isConcreteClass())
            checkParameters();
        else throw new SemanticException("No existe constructor para una clase "+token.lexeme+" accesible", token.lexeme, token.lineNumber);

        return new ReferenceType(token);
    }

    private void checkParameters() throws SemanticException {
        List<Parameter> formalParameters = symbolTable.getClass(token.lexeme).getConstructor().getParametersList();

        if (formalParameters.size() != actualParameters.size())
            throw new SemanticException("La llamada a constructor "+token.lexeme+" no se realizo con la cantidad de parametros correctos", token.lexeme, token.lineNumber);

        Iterator<NodeExpression> actualIterator = actualParameters.listIterator();
        Iterator<Parameter> formalIterator = formalParameters.listIterator();

        while (actualIterator.hasNext() && formalIterator.hasNext() ) {
            NodeExpression parameterValue = actualIterator.next();
            Parameter formalParameter = formalIterator.next();
            if (!parameterValue.check().isSubtypeOf(formalParameter.getType()))
                throw new SemanticException("La llamada a constructor "+token.lexeme+" no se realizo con parametros de tipo correcto", token.lexeme, token.lineNumber);
        }

    }
}
