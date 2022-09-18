package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.entries.types.Type;

import java.util.List;

public class Method {

    private Token idToken;
    private Type returnType;
    private List<Parameter> parameterList;

}
