package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.entries.Method;
import minijavaCompiler.semantics.entries.Parameter;
import minijavaCompiler.semantics.types.Type;
import minijavaCompiler.semantics.types.primitives.VoidType;

import java.util.Iterator;
import java.util.List;

import static minijavaCompiler.Main.symbolTable;

public class NodeMethodCall implements NodeAccess {

    private Token methodToken;
    private List<NodeExpression> actualParameters;
    private NodeChaining optChaining;

    private Method methodCalled;
    private boolean isLeftSideOfAssign;

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

    public boolean isMethodCall() {
        if (optChaining == null)
            return true;
        else return optChaining.isMethodCall();
    }

    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }

    public Type check() throws SemanticException {
        if (methodCalledExistsInClass()) {
            if (methodCalledIsDynamic())
                checkNotCalledInStaticMethod();
            methodCalled = symbolTable.currentClass.getMethod(methodToken.lexeme);
            checkParameters();
        }
        else throw new SemanticException("El metodo "+methodToken.lexeme+" no existe en "+symbolTable.currentClass.getName(), methodToken.lexeme, methodToken.lineNumber);

        Type methodType = methodCalled.getReturnType();

        if (optChaining == null) {
            return methodType;
        } else return optChaining.check(methodType);
    }

    private boolean methodCalledExistsInClass() {
        return symbolTable.currentClass.isMethod(methodToken.lexeme);
    }

    private boolean methodCalledIsDynamic() {
        return !symbolTable.currentClass.getMethod(methodToken.lexeme).isStatic();
    }

    private void checkNotCalledInStaticMethod() throws SemanticException {
        if (symbolTable.currentUnit.isMethod())
            if (((Method) symbolTable.currentUnit).isStatic())
                throw new SemanticException("No se puede tener accesos a metodo dinamico en metodo estático", methodToken.lexeme, methodToken.lineNumber);
    }

    private void checkParameters() throws SemanticException {
        List<Parameter> formalParameters = methodCalled.getParametersList();

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
        if (methodCalled.isStatic()) {
            if (!methodCalled.getReturnType().equals(new VoidType())){
                symbolTable.ceiASM_instructionList.add("    RMEM 1 ; Reservo lugar para el retorno");
            }
            for (NodeExpression p : actualParameters){ // Genero codigo de los parametros
                p.generateCode();
            }
            symbolTable.ceiASM_instructionList.add("    PUSH "+methodCalled.getLabel()+" ; Cargo la direccion estatica");
            symbolTable.ceiASM_instructionList.add("    CALL ; Llamo metodo");
        } else {
            symbolTable.ceiASM_instructionList.add("    LOAD 3");
            if (!methodCalled.getReturnType().equals(new VoidType())){
                symbolTable.ceiASM_instructionList.add("    RMEM 1 ; Reservo lugar para el retorno");
                symbolTable.ceiASM_instructionList.add("    SWAP");
            }
            for (NodeExpression p : actualParameters){ // Genero codigo de los parametros, corriendo el this
                p.generateCode();
                symbolTable.ceiASM_instructionList.add("    SWAP");
            }
            symbolTable.ceiASM_instructionList.add("    DUP ; Duplico this");
            symbolTable.ceiASM_instructionList.add("    LOADREF 0 ; Cargo VT");
            symbolTable.ceiASM_instructionList.add("    LOADREF "+methodCalled.getOffset()+" ; Cargo metodo");
            symbolTable.ceiASM_instructionList.add("    CALL ; Llamo metodo");
        }

        if (optChaining != null)
            optChaining.generateCode();
    }

    public void setIsLeftSideOfAssign(){
        isLeftSideOfAssign = true;
        if (optChaining != null)
            optChaining.setIsLeftSideOfAssign();
    }

}
