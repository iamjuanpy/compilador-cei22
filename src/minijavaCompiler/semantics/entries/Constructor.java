package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.sentence_nodes.NodeBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constructor implements Unit {

    private Token constructorToken;
    private HashMap<String, Parameter> parameterHashMap;
    private List<Parameter> parameterList;
    private NodeBlock block;

    public Constructor(Token constructorId) {
        constructorToken = constructorId;
        parameterHashMap = new HashMap<>();
        parameterList = new ArrayList<>();
    }

    public String getName() {return constructorToken.lexeme;}
    public int getLine() {return constructorToken.lineNumber;}
    public List<Parameter> getParametersList() {return parameterList;}

    public boolean isMethod() {return false;}

    public void addParameter(Parameter parameter) throws SemanticException {
        if (parameterHashMap.get(parameter.getName()) == null){
            parameterHashMap.put(parameter.getName(),parameter);
            parameterList.add(parameter);
        } else throw new SemanticException("No se puede declarar mas de un parámetro con el mismo nombre, "+parameter.getName(), parameter.getName(), parameter.getLine());
    }

    public boolean isParameter(String identifier){return parameterHashMap.get(identifier) != null;}
    public Parameter getParameter(String identifier){return parameterHashMap.get(identifier);}

    public void addBlock(NodeBlock block){this.block = block;}

    public void correctlyDeclared() throws SemanticException {
        for (Parameter p : parameterHashMap.values())
            p.correctlyDeclared();
    }
}
