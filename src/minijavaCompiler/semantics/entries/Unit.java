package minijavaCompiler.semantics.entries;

import minijavaCompiler.semantics.SemanticException;

public interface Unit {
    void addParameter(Parameter parameter) throws SemanticException;
}
