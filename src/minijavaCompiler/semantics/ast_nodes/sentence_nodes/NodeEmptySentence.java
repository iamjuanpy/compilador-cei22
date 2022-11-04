package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;

import static minijavaCompiler.Main.symbolTable;

public class NodeEmptySentence implements NodeSentence{

    private Token token;

    public NodeEmptySentence(Token token){
        this.token = token;
    }

    public void check() {} // Semanticamente correcta siempre

    public boolean isReturn(){return false;}
    public boolean isVariableDeclaration() {return false;}

    public void generateCode() {
        symbolTable.ceiASM_instructionList.add("    NOP ; Instruccion vacia");
    }

}
