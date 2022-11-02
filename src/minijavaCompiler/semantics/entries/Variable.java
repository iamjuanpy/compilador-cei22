package minijavaCompiler.semantics.entries;

import minijavaCompiler.semantics.types.Type;

public interface Variable { // GENERALIZA ATRIBUTO, PARAMETRO, VAR LOCAL Y VAR LOCAL CLASICA
    Type getType();

    void setOffset(int offset);
    int getOffset();
}
