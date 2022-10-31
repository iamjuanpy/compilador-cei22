package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;

public interface NodeSentence {

    void check() throws SemanticException;

    boolean isReturn();
    boolean isVariableDeclaration();

    void generateCode();
}
