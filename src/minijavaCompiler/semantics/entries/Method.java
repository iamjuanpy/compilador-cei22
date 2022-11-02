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

    private String label;
    private int offset;

    public Method(boolean isStatic, Type type, Token methodToken) {
        this.classDeclared = symbolTable.currentClass;
        this.methodToken = methodToken;
        this.isStatic = isStatic;
        this.returnType = type;
        parameterHashMap = new HashMap<>();
        parameterList = new ArrayList<>();
        label = classDeclared.getName()+"_"+methodToken.lexeme; // A_m1
    }

    public String getName() {return methodToken.lexeme;}
    public int getLine() {return methodToken.lineNumber;}
    public String getLabel() {return label;}
    public boolean isStatic() {return isStatic;}
    public List<Parameter> getParametersList() {return parameterList;}
    public ClassEntry getClassDeclared() {return classDeclared;}
    public Type getReturnType() {return returnType;}
    public boolean isMain() {return isStatic && returnType.equals(new VoidType()) && methodToken.lexeme.equals("main") && parameterHashMap.size() == 0;}

    public void addParameter(Parameter parameter) throws SemanticException {
        if (parameterHashMap.get(parameter.getName()) == null){
            parameterHashMap.put(parameter.getName(),parameter);
            parameterList.add(parameter);
        } else throw new SemanticException("No se puede declarar mas de un parámetro con el mismo nombre, "+parameter.getName(),parameter.getName(), parameter.getLine());
    }

    public boolean isParameter(String identifier){return parameterHashMap.get(identifier) != null;}
    public Parameter getParameter(String identifier){return parameterHashMap.get(identifier);}

    public boolean isMethod() {return true;}

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

    public void checkSentences() throws SemanticException{
        block.check();
    }

    public void generateCode(){
        int memToFree = isStatic ? parameterList.size() : parameterList.size() + 1; // Si es dinamico, tiene que borrar el this

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
        int i = isStatic ? parameterList.size() + 2 : parameterList.size() + 3; // Si es estatico PR + ED, si es dinamico this + PR + ED
        for (Parameter p : parameterList){
            p.setOffset(i--);
        }
    }

    public void setOffset(int offset){
        this.offset = offset;
    }

    public int getOffset(){
        return offset;
    }

}
