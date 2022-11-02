package minijavaCompiler.semantics.ast_nodes.access_nodes.chaining;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.entries.Parameter;
import minijavaCompiler.semantics.types.Type;

import java.util.Iterator;
import java.util.List;

import static minijavaCompiler.Main.symbolTable;

public class NodeMethodChaining implements NodeChaining{

    private Token methodToken;
    public List<NodeExpression> actualParameters;
    private NodeChaining optChaining;
    private boolean isLeftSideOfAssign;

    public NodeMethodChaining(Token id){
        this.methodToken = id;
    }

    public boolean isVariableAccess() {
        if (optChaining != null)
            return optChaining.isVariableAccess();
        else return false;
    }

    public boolean isMethodCall() {
        if (optChaining == null)
            return true;
        else return optChaining.isMethodCall();
    }

    public void setParameterList(List<NodeExpression> parameterList){
        this.actualParameters = parameterList;
    }

    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }

    public Type check(Type previousAccessType) throws SemanticException {
        String className = previousAccessType.getTypeName();

        checkNotChainingPrimitiveType(previousAccessType);

        if (methodExistsInClass(className))
            checkParameters(className);
        else throw new SemanticException("El metodo "+methodToken.lexeme+" no existe para "+className, methodToken.lexeme, methodToken.lineNumber);

        Type methodType = symbolTable.getClass(className).getMethod(methodToken.lexeme).getReturnType();

        if (optChaining == null)
            return methodType;
        else return optChaining.check(methodType);
    }

    private boolean methodExistsInClass(String className) {return symbolTable.getClass(className).isMethod(methodToken.lexeme);}

    private void checkNotChainingPrimitiveType(Type previousAccessType) throws SemanticException {
        if (previousAccessType.isPrimitive())
            throw new SemanticException("No se puede encadenar a tipo primitivo o vacío", methodToken.lexeme, methodToken.lineNumber);
    }

    private void checkParameters(String className) throws SemanticException {
        List<Parameter> formalParameters = symbolTable.getClass(className).getMethod(methodToken.lexeme).getParametersList();

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

    public void generateCode() {

    }

    public void setIsLeftSideOfAssign(){
        isLeftSideOfAssign = true;
        if (optChaining != null)
            optChaining.setIsLeftSideOfAssign();
    }

}
