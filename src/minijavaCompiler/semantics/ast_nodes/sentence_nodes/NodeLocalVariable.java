package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.entries.Variable;
import minijavaCompiler.semantics.types.NullType;
import minijavaCompiler.semantics.types.Type;

import static minijavaCompiler.Main.symbolTable;

public class NodeLocalVariable implements NodeSentence, Variable {

    private Type type;
    private Token token;
    private NodeExpression value;

    public NodeLocalVariable(Token token, NodeExpression value) { // Constructor variable local (minijava)
        this.token = token;
        this.value = value;
    }

    public NodeLocalVariable(Type type, Token token){ // Constructor variable local clasica sin asignacion
        this.token = token;
        this.type = type;
    }

    public NodeLocalVariable(Type type, Token token, NodeExpression value) { // Constructor variable local clasica con asignacion
        this.token = token;
        this.type = type;
        this.value = value;
    }

    public String getName() { return token.lexeme;}
    public int getLine() { return token.lineNumber;}
    public Type getType(){ return type;}

    public void check() throws SemanticException {
        symbolTable.currentBlock.addVariable(this); // Agregar la variable al bloque chequea los nombres repetidos
        if (classicDeclaration())
            checkVariableType();
        else setVariableType();
    }

    private boolean classicDeclaration() { return type != null;}

    // Logro variable clasica
    private void checkVariableType() throws SemanticException {
        if (typeNotExists())
            throw new SemanticException("No se puede declarar variable de tipo "+type.getTypeName()+", este no existe", type.getTypeName(), type.getLine());

        if (classicVariableWithAssign()) // Type x = exp, exp.type <= Type
            if (notAssigningASubtype())
                throw new SemanticException("No se puede asignar a una variable "+type.getTypeName()+" una expresión "+value.check().getTypeName(), token.lexeme, token.lineNumber);
    }

    private boolean typeNotExists() {return !type.isPrimitive() && !symbolTable.classExists(type.getTypeName());}

    private boolean classicVariableWithAssign() {return value != null;}
    private boolean notAssigningASubtype() throws SemanticException {return !value.check().isSubtypeOf(type);}
    //
    
    private void setVariableType() throws SemanticException { // var x = exp, var.type = exp.type
        type = value.check();
        checkNotDeclaringNullVariable();
    }

    private void checkNotDeclaringNullVariable() throws SemanticException {
        if (type.equals(new NullType()))
            throw new SemanticException("No se puede declarar una variable de tipo null", token.lexeme, token.lineNumber);
    }

    public boolean isVariableDeclaration() {
        return true;
    }

}
