package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constructor implements Unit {

    private Token constructorToken;
    private HashMap<String, Parameter> parameterHashMap;
    private List<Parameter> parameterList;

    public Constructor(Token constructorId) {
        constructorToken = constructorId;
        parameterHashMap = new HashMap<>();
        parameterList = new ArrayList<>();
    }

    public String getName() {return constructorToken.lexeme;}
    public int getLine() {return constructorToken.lineNumber;}
    public List<Parameter> getParametersList() {return parameterList;}

    public boolean hasSameSignature(Constructor constructor){
        if (parameterList.size() != constructor.getParametersList().size())
            return false;                                                           // difiere cantidad de parametros
        List<Parameter> otherConstructorParameters = constructor.getParametersList();
        for (int i = 0; i < parameterList.size(); i++)
            if (!parameterList.get(i).equals(otherConstructorParameters.get(i)))     // difiere un parametro
                return false;
        return true;

    }

    public void addParameter(Parameter parameter) throws SemanticException {
        if (parameterHashMap.get(parameter.getName()) == null){
            parameterHashMap.put(parameter.getName(),parameter);
            parameterList.add(parameter);
        } else throw new SemanticException("No se puede declarar mas de un parámetro con el mismo nombre, "+parameter.getName(), parameter.getName(), parameter.getLine());
    }

    public void correctlyDeclared() throws SemanticException {
        for (Parameter p : parameterHashMap.values())
            p.correctlyDeclared();
    }
}
