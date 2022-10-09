package minijavaCompiler.semantics.entries;

import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.sentence_nodes.NodeBlock;

public interface Unit {
    void addParameter(Parameter parameter) throws SemanticException;
    void addBlock(NodeBlock block);

    boolean isMethod();

    boolean isParameter(String identifier);
    Parameter getParameter(String identifier);
}
