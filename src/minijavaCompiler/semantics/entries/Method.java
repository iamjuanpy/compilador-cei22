package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.classes.ClassEntry;
import minijavaCompiler.semantics.entries.types.Type;
import minijavaCompiler.semantics.entries.types.primitives.VoidType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static minijavaCompiler.Main.symbolTable;

public class Method implements Unit {

    private ClassEntry classDeclared;
    private Token methodToken;
    private boolean isStatic;
    private Type returnType;
    private HashMap<String,Parameter> parameterHashMap;
    private List<Parameter> parameterList;

    public Method(boolean isStatic, Type type, Token methodToken) {
        this.methodToken = methodToken;
        this.isStatic = isStatic;
        this.classDeclared = symbolTable.currentClass;
                returnType = type;
        parameterHashMap = new HashMap<>();
        parameterList = new ArrayList<>();
    }

    public void addParameter(Parameter parameter) throws SemanticException {
        if (parameterHashMap.get(parameter.getName()) == null){
            parameterHashMap.put(parameter.getName(),parameter);
            parameterList.add(parameter);
        } else throw new SemanticException("No puede haber mas de un parámetro con el mismo nombre, "+parameter.getName(),parameter.getName(), parameter.getLine());
    }

    public String getName() {
        return methodToken.lexeme;
    }

    public int getLine() {
        return methodToken.lineNumber;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public List<Parameter> getParametersList() {
        return parameterList;
    }

    public Type getReturnType() {return returnType;}

    public boolean isMain() {return isStatic && returnType.equals(new VoidType()) && methodToken.lexeme.equals("main") && parameterHashMap.size() == 0;}

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
        if (parameterList.size() != method.getParametersList().size())
            return false;
        List<Parameter> otherMethodParametersList = method.getParametersList();
        for (int i = 0; i < parameterList.size(); i++)
            if (!parameterList.get(i).equals(otherMethodParametersList.get(i)))
                return false;
        return true;
    }

    public void correctlyDeclared() throws SemanticException {
        checkReturnType();
        for (Parameter parameter : parameterHashMap.values())
            parameter.correctlyDeclared();
    }

    private void checkReturnType() throws SemanticException {
        if (!returnType.isPrimitive() && !symbolTable.classExists(returnType.getTypeName())) // Tipo clase con clase no existente
            throw new SemanticException("No se puede declarar un parametro de tipo "+returnType.getTypeName()+", la clase no existe", returnType.getTypeName(), returnType.getLine());
    }
}
