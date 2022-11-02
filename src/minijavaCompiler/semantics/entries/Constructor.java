package minijavaCompiler.semantics.entries;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.sentence_nodes.NodeBlock;
import minijavaCompiler.semantics.types.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static minijavaCompiler.Main.symbolTable;

public class Constructor implements Unit {

    private Token constructorToken;
    private HashMap<String, Parameter> parameterHashMap;
    private List<Parameter> parameterList;
    private NodeBlock block;

    private String label;

    public Constructor(Token constructorId) {
        constructorToken = constructorId;
        parameterHashMap = new HashMap<>();
        parameterList = new ArrayList<>();
        label = "const_"+constructorToken.lexeme; // const_A
    }

    public String getName() {return constructorToken.lexeme;}
    public int getLine() {return constructorToken.lineNumber;}
    public String getLabel() {return label;}
    public Type getReturnType() {return null;}
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

    public void checkSentences() throws SemanticException {
        block.check();
    }

    public void generateCode(){
        int memToFree = parameterList.size() + 1; // Tiene this

        symbolTable.ceiASM_instructionList.add(".code");
        symbolTable.ceiASM_instructionList.add(label+":");
        symbolTable.ceiASM_instructionList.add("    LOADFP ; Guarda ED");
        symbolTable.ceiASM_instructionList.add("    LOADSP ; Guarda SP");
        symbolTable.ceiASM_instructionList.add("    STOREFP ; Corre FP al SP");

        block.generateCode();

        symbolTable.ceiASM_instructionList.add("    STOREFP ; Usa ED para volver a RA llamador");
        symbolTable.ceiASM_instructionList.add("    RET "+memToFree+" ; Libera los parametros y retorna de la unidad");
    }

    public void setParametersOffsets(){
        int i = parameterList.size() + 3; // n parametros + 1 ED + 1 PR + 1 THIS
        for (Parameter p : parameterList){
            p.setOffset(i--);
        }
    }

}
