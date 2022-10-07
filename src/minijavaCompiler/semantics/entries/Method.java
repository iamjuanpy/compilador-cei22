package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.sentence_nodes.NodeBlock;
import minijavaCompiler.semantics.entries.classes.ClassEntry;
import minijavaCompiler.semantics.types.Type;
import minijavaCompiler.semantics.types.primitives.VoidType;

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
    private NodeBlock block;

    public Method(boolean isStatic, Type type, Token methodToken) {
        this.classDeclared = symbolTable.currentClass;
        this.methodToken = methodToken;
        this.isStatic = isStatic;
        this.returnType = type;
        parameterHashMap = new HashMap<>();
        parameterList = new ArrayList<>();
    }

    public String getName() {return methodToken.lexeme;}
    public int getLine() {return methodToken.lineNumber;}
    public boolean isStatic() {return isStatic;}
    public List<Parameter> getParametersList() {return parameterList;}
    public Type getReturnType() {return returnType;}
    public boolean isMain() {return isStatic && returnType.equals(new VoidType()) && methodToken.lexeme.equals("main") && parameterHashMap.size() == 0;}

    public void addParameter(Parameter parameter) throws SemanticException {
        if (parameterHashMap.get(parameter.getName()) == null){
            parameterHashMap.put(parameter.getName(),parameter);
            parameterList.add(parameter);
        } else throw new SemanticException("No se puede declarar mas de un parámetro con el mismo nombre, "+parameter.getName(),parameter.getName(), parameter.getLine());
    }

    public void addBlock(NodeBlock block){ this.block = block;}

    public void correctlyDeclared() throws SemanticException {
        isReturnTypeCorrectlyDeclared();
        for (Parameter parameter : parameterHashMap.values())
            parameter.correctlyDeclared();
    }

    private void isReturnTypeCorrectlyDeclared() throws SemanticException {
        if (!returnType.isPrimitive() && !symbolTable.classExists(returnType.getTypeName())) // Tipo clase con clase no existente
            throw new SemanticException("No se puede declarar un metodo con retorno de tipo "+returnType.getTypeName()+", la clase no existe", returnType.getTypeName(), returnType.getLine());
    }

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
            return false;                                                           // difiere cantidad de parametros
        List<Parameter> otherMethodParametersList = method.getParametersList();
        for (int i = 0; i < parameterList.size(); i++)
            if (!parameterList.get(i).equals(otherMethodParametersList.get(i)))     // difiere un parametro
                return false;
        return true;
    }

}
