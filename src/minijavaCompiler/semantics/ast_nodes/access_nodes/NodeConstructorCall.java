package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.entries.Constructor;
import minijavaCompiler.semantics.entries.Parameter;
import minijavaCompiler.semantics.entries.classes.ClassEntry;
import minijavaCompiler.semantics.types.ReferenceType;
import minijavaCompiler.semantics.types.Type;

import java.util.Iterator;
import java.util.List;

import static minijavaCompiler.Main.symbolTable;

public class NodeConstructorCall implements NodeAccess{

    private Token token;
    private List<NodeExpression> actualParameters;
    private NodeChaining optChaining;

    private boolean isLeftSideOfAssign;
    private ClassEntry classBuilding;
    private Constructor constructor;

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

    public boolean isMethodCall() {
        if (optChaining != null)
            return optChaining.isMethodCall();
        else return true;
    }

    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }

    public Type check() throws SemanticException {
        if (classExists())
            if (isConcreteClass()) {
                classBuilding = symbolTable.getClass(token.lexeme);
                constructor = classBuilding.getConstructor();
                checkParameters();
            } else throw new SemanticException("No existe constructor para "+token.lexeme+", es una interface", token.lexeme, token.lineNumber);
        else throw new SemanticException("No existe clase "+token.lexeme, token.lexeme, token.lineNumber);

        Type objectType = new ReferenceType(token); // Tipo de la expresion, tipo clase del objeto

        if (optChaining == null)
            return objectType;
        else return optChaining.check(objectType);
    }

    private boolean classExists() {return symbolTable.classExists(token.lexeme);}
    private boolean isConcreteClass() {return symbolTable.getClass(token.lexeme).isConcreteClass();}

    private void checkParameters() throws SemanticException {
        List<Parameter> formalParameters = constructor.getParametersList();

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

    public void generateCode() {
        symbolTable.ceiASM_instructionList.add("    RMEM 1 ; Reservo puntero malloc");
        symbolTable.ceiASM_instructionList.add("    PUSH "+(classBuilding.getLastAttributeOffset()+1)+" ; Cantidad de atributos + VT Ref");
        symbolTable.ceiASM_instructionList.add("    PUSH simple_malloc");
        symbolTable.ceiASM_instructionList.add("    CALL ; malloc()");
        symbolTable.ceiASM_instructionList.add("    DUP");
        symbolTable.ceiASM_instructionList.add("    PUSH "+classBuilding.getVTableLabel());
        symbolTable.ceiASM_instructionList.add("    STOREREF 0 ; Guardo VT en CIR");
        symbolTable.ceiASM_instructionList.add("    DUP ; Duplico this, para metodo de constructor");
        // Ejecuta metodo de constructor (ya esta la referencia al CIR en el retorno)
        for (NodeExpression p : actualParameters)
            p.generateCode();
        symbolTable.ceiASM_instructionList.add("    PUSH "+constructor.getLabel()+" ; Direccion del constructor");
        symbolTable.ceiASM_instructionList.add("    CALL ; Llama al metodo");
    }

    public void setIsLeftSideOfAssign(){
        isLeftSideOfAssign = true;
        if (optChaining != null)
            optChaining.setIsLeftSideOfAssign();
    }

}
