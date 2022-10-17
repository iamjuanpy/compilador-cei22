package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

public class NodeEmptySentence implements NodeSentence{

    public void check() {} // Semanticamente correcta siempre

    public boolean isVariableDeclaration() {
        return false;
    }
}
