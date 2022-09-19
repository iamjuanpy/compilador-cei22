package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.types.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Method implements Unit {

    private Token idToken;
    private boolean isStatic;
    private Type returnType;
    private HashMap<String,Parameter> parameterHashMap;
    private List<Parameter> parameterList;

    public Method(boolean isStatic, Type type, Token methodToken) {
        idToken = methodToken;
        this.isStatic = isStatic;
        returnType = type;
        parameterHashMap = new HashMap<>();
        parameterList = new ArrayList<>();
    }

    public void addParameter(Parameter parameter) throws SemanticException {
        if (parameterHashMap.get(parameter.getName()) == null){
            parameterHashMap.put(parameter.getName(),parameter);
            parameterList.add(parameter);
        } else throw new SemanticException(parameter.getName(), parameter.getLine());
    }

    public String getName() {
        return idToken.lexeme;
    }

    public int getLine() {
        return idToken.lineNumber;
    }
}
