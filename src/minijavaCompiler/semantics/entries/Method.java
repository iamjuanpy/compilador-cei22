package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.types.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static minijavaCompiler.Main.symbolTable;

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

    public boolean isStatic() {
        return isStatic;
    }

    public List<Parameter> getParametersList() {
        return parameterList;
    }

    public Type getReturnType() {return returnType;}

    public boolean hasSameSignature(Method method) {
        if (isStatic != method.isStatic())
            return false;
        if (differentReturnType(method))
            return false;
        return sameParameters(method);
    }

    private boolean differentReturnType(Method method) {
        return !returnType.equals(method.getReturnType());
    }

    private boolean sameParameters(Method method) {
        if (parameterHashMap.size() != method.getParametersList().size())
            return false;
        List<Parameter> otherMethodParametersList = method.getParametersList();
        for (int i = 0; i < parameterList.size(); i++)
            if (!parameterList.get(i).equals(otherMethodParametersList.get(i)))
                return false;
        return true;
    }

    public void isWellDeclared() throws SemanticException {
        checkReturnType();
        for (Parameter p : parameterHashMap.values())
            p.isWellDeclared();
    }

    private void checkReturnType() throws SemanticException {
        if (!returnType.isPrimitive() && !symbolTable.classExists(returnType.getTypeName())) // Tipo clase con clase no existente
            throw new SemanticException("No se puede declarar un parametro de tipo "+returnType.getTypeName()+", la clase no existe", returnType.getTypeName(), returnType.getLine());
    }
}
