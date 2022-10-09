package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.entries.Variable;
import minijavaCompiler.semantics.types.Type;

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

    public void check() {

    }

    public boolean isVariableDeclaration() {
        return true;
    }

}
