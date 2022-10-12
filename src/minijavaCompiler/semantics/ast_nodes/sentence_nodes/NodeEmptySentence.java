package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

public class NodeEmptySentence implements NodeSentence{

    public void check() {}

    public boolean isVariableDeclaration() {
        return false;
    }
}
