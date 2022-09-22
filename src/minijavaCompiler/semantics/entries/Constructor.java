package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constructor implements Unit {

    private Token idToken;
    private HashMap<String, Parameter> parameterHashMap;
    private List<Parameter> parameterList;

    public Constructor(Token constructorId) {
        idToken = constructorId;
        parameterHashMap = new HashMap<>();
        parameterList = new ArrayList<>();
    }

    public String getName() {
        return idToken.lexeme;
    }

    public int getLine() {
        return idToken.lineNumber;
    }

    public void addParameter(Parameter parameter) throws SemanticException {
        if (parameterHashMap.get(parameter.getName()) == null){
            parameterHashMap.put(parameter.getName(),parameter);
            parameterList.add(parameter);
        } else throw new SemanticException(parameter.getName(), parameter.getLine());
    }

    public void isWellDeclared() throws SemanticException {
        for (Parameter p : parameterHashMap.values())
            p.isWellDeclared();
    }
}
