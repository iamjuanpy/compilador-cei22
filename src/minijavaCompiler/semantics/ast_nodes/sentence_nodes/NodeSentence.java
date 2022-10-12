package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.semantics.SemanticException;

public interface NodeSentence {

    void check() throws SemanticException;

    boolean isVariableDeclaration();

}
