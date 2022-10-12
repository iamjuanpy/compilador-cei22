package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.entries.Parameter;
import minijavaCompiler.semantics.types.Type;

import java.util.Iterator;
import java.util.List;

import static minijavaCompiler.Main.symbolTable;

public class NodeMethodCall implements NodeAccess {

    private Token methodToken;
    private List<NodeExpression> actualParameters;
    private NodeChaining optChaining;

    public NodeMethodCall(Token id){
        this.methodToken = id;
    }

    public void setParameterList(List<NodeExpression> actualParameters){
        this.actualParameters = actualParameters;
    }

    public boolean isVariableAccess() {
        if (optChaining != null)
            return optChaining.isVariableAccess();
        else return false;
    }

    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }

    public Type check() throws SemanticException {
        if (symbolTable.currentClass.isMethod(methodToken.lexeme))
            checkParameters();
        else throw new SemanticException("No existe metodo "+methodToken.lexeme+" accesible", methodToken.lexeme, methodToken.lineNumber);

        return symbolTable.currentClass.getMethod(methodToken.lexeme).getReturnType();
    }

    private void checkParameters() throws SemanticException {
        List<Parameter> formalParameters = symbolTable.currentClass.getMethod(methodToken.lexeme).getParametersList();

        if (formalParameters.size() != actualParameters.size())
            throw new SemanticException("La llamada a metodo "+methodToken.lexeme+" no se realizo con la cantidad de parametros correctos", methodToken.lexeme, methodToken.lineNumber);

        Iterator<NodeExpression> actualIterator = actualParameters.listIterator();
        Iterator<Parameter> formalIterator = formalParameters.listIterator();

        while (actualIterator.hasNext() && formalIterator.hasNext() ) {
            NodeExpression parameterValue = actualIterator.next();
            Parameter formalParameter = formalIterator.next();
            if (!parameterValue.check().isSubtypeOf(formalParameter.getType()))
                throw new SemanticException("La llamada a metodo "+methodToken.lexeme+" no se realizo con parametros de tipo correcto", methodToken.lexeme, methodToken.lineNumber);
        }

    }
}
