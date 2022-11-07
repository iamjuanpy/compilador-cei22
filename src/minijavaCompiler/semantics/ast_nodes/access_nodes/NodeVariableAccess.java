package minijavaCompiler.semantics.ast_nodes.access_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.entries.Attribute;
import minijavaCompiler.semantics.entries.Method;
import minijavaCompiler.semantics.entries.Variable;
import minijavaCompiler.semantics.types.Type;

import static minijavaCompiler.Main.symbolTable;

public class NodeVariableAccess implements NodeAccess {

    private Token variableToken;
    private NodeChaining optChaining;

    private boolean isLeftSideOfAssign;
    private boolean accessIsAttribute;
    private Variable variable;


    public NodeVariableAccess(Token id){
        this.variableToken = id;
    }

    public boolean isVariableAccess() {
        if (optChaining == null)
            return true;
        else return optChaining.isVariableAccess();
    }

    public boolean isMethodCall() {
        if (optChaining != null)
            return optChaining.isMethodCall();
        else return false;
    }

    public void setChaining(NodeChaining chaining) {
        this.optChaining = chaining;
    }

    public Type check() throws SemanticException {
        if (accessIsParameter())
            variable = symbolTable.currentUnit.getParameter(variableToken.lexeme);
        else if (accessIsLocalVariable())
            variable = symbolTable.currentBlock.getLocalVariable(variableToken.lexeme);
        else if (accessIsAttribute()) {
            if (currentUnitIsConstructor() || currentUnitIsDynamicMethod()) {
                if (attributeIsPublic() || attributeIsDeclaredInCurrentClass())
                    variable = symbolTable.currentClass.getAtrribute(variableToken.lexeme);
                else throw new SemanticException("La variable de instancia "+variableToken.lexeme+" no es accesible", variableToken.lexeme, variableToken.lineNumber);
            } else throw new SemanticException("No se puede acceder a variable de instancia "+variableToken.lexeme+" desde un metodo estático", variableToken.lexeme, variableToken.lineNumber);
        } else throw new SemanticException("No se encuentra variable/parámetro "+variableToken.lexeme+" en el ambiente de referenciamiento", variableToken.lexeme, variableToken.lineNumber);

        if (optChaining == null) {
            return variable.getType();
        } else return optChaining.check(variable.getType());
    }

    private boolean accessIsAttribute() {return symbolTable.currentClass.isAttribute(variableToken.lexeme);}
    private boolean accessIsLocalVariable() {return symbolTable.currentBlock.isLocalVariable(variableToken.lexeme);}
    private boolean accessIsParameter() {return symbolTable.currentUnit.isParameter(variableToken.lexeme);}

    private boolean attributeIsDeclaredInCurrentClass() {return symbolTable.getClass(symbolTable.currentClass.getName()).getAtrribute(variableToken.lexeme).getClassDeclared().equals(symbolTable.currentClass);}
    private boolean attributeIsPublic() {return symbolTable.getClass(symbolTable.currentClass.getName()).getAtrribute(variableToken.lexeme).isPublic();}

    private boolean currentUnitIsConstructor() {return !symbolTable.currentUnit.isMethod();}
    private boolean currentUnitIsDynamicMethod() {return !((Method) symbolTable.currentUnit).isStatic();}

    public void generateCode() {
        if (variable.isAttribute()){
            symbolTable.ceiASM_instructionList.add("    LOAD 3 ; Cargo this para acceder a atributo");
            if (!isLeftSideOfAssign || optChaining != null){
                symbolTable.ceiASM_instructionList.add("    LOADREF "+variable.getOffset()+" ; Cargo direccion de atributo");
            } else {
                symbolTable.ceiASM_instructionList.add("    SWAP ; Muevo this a SP - 1");
                symbolTable.ceiASM_instructionList.add("    STOREREF "+variable.getOffset()+" ; Guardo valor en la direccion del atributo");
            }
        } else {
            if (!isLeftSideOfAssign || optChaining != null)
                symbolTable.ceiASM_instructionList.add("    LOAD "+variable.getOffset()+" ; Cargo la direccion de parametro/var local");
            else symbolTable.ceiASM_instructionList.add("    STORE "+variable.getOffset()+" ; Guardo valor en la direccion de parametro/var local");
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
