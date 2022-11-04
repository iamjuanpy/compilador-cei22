package minijavaCompiler.semantics.entries;

import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.sentence_nodes.NodeBlock;
import minijavaCompiler.semantics.types.Type;

import java.util.List;

public interface Unit {
    void addParameter(Parameter parameter) throws SemanticException;
    void addBlock(NodeBlock block);

    boolean isMethod();
    Type getReturnType();

    boolean isParameter(String identifier);
    Parameter getParameter(String identifier);
    List<Parameter> getParametersList();

    void checkSentences() throws SemanticException;

    String getName();
    int getLine();

    String getLabel();
    void generateCode();
    void setParametersOffsets();
}
