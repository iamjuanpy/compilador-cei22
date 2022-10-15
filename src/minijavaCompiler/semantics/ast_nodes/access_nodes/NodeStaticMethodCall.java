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

public class NodeStaticMethodCall implements NodeAccess{

    private Token classToken;
    private Token methodToken;
    private List<NodeExpression> actualParameters;
    private NodeChaining optChaining;

    public NodeStaticMethodCall(Token classID, Token methodID){
        this.classToken = classID;
        this.methodToken = methodID;
    }

    public boolean isVariableAccess() {
        if (optChaining != null)
            return optChaining.isVariableAccess();
        else return false;
    }

    public boolean isMethodCall() {
        if (optChaining != null)
            return optChaining.isMethodCall();
        else return true;
    }

    public void setParameterList(List<NodeExpression> actualParameters){
        this.actualParameters = actualParameters;
    }

    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }

    public Type check() throws SemanticException {
        if (symbolTable.classExists(classToken.lexeme)) {
            if (symbolTable.getClass(classToken.lexeme).isMethod(methodToken.lexeme))
                if (symbolTable.getClass(classToken.lexeme).getMethod(methodToken.lexeme).isStatic())
                    checkParameters();
                else throw new SemanticException("El metodo "+methodToken.lexeme+" no es estático", methodToken.lexeme, methodToken.lineNumber);
            else throw new SemanticException("El metodo "+methodToken.lexeme+" no existe para "+classToken.lexeme, methodToken.lexeme, methodToken.lineNumber);

            Type methodType = symbolTable.getClass(classToken.lexeme).getMethod(methodToken.lexeme).getReturnType();

            if (optChaining != null) {
                return optChaining.check(methodType);
            } else return methodType;

        } else throw new SemanticException("No existe clase "+classToken.lexeme, classToken.lexeme, classToken.lineNumber);
    }

    private void checkParameters() throws SemanticException {
        List<Parameter> formalParameters = symbolTable.getClass(classToken.lexeme).getMethod(methodToken.lexeme).getParametersList();

        if (formalParameters.size() != actualParameters.size())
            throw new SemanticException("La llamada a metodo estático "+methodToken.lexeme+" no se realizo con la cantidad de parametros correctos", methodToken.lexeme, methodToken.lineNumber);

        Iterator<NodeExpression> actualIterator = actualParameters.listIterator();
        Iterator<Parameter> formalIterator = formalParameters.listIterator();

        while (actualIterator.hasNext() && formalIterator.hasNext() ) {
            NodeExpression parameterValue = actualIterator.next();
            Parameter formalParameter = formalIterator.next();
            if (!parameterValue.check().isSubtypeOf(formalParameter.getType()))
                throw new SemanticException("La llamada a metodo estático "+methodToken.lexeme+" no se realizo con parametros de tipo correcto", methodToken.lexeme, methodToken.lineNumber);
        }
    }
}
