package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.Unit;
import minijavaCompiler.semantics.entries.classes.ClassEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static minijavaCompiler.Main.symbolTable;

public class NodeBlock implements NodeSentence {

    public List<NodeSentence> sentencesList;
    public HashMap<String, NodeLocalVariable> variableHashMap;
    public ClassEntry ownerClass;
    public Unit unit;
    public NodeBlock nestingIn;

    public NodeBlock(){
        sentencesList = new ArrayList<>();
        variableHashMap = new HashMap<>();
        ownerClass = symbolTable.currentClass;  // Usado para acceder a atributos
        unit = symbolTable.currentUnit;         // Usado para acceder a los parametros
        nestingIn = symbolTable.currentBlock;   // Usado para recuperar a donde volver cuando termino de leer este bloque/ acceder a variables locales
    }

    public void addSentence(NodeSentence sentence) {
        if (sentence != null)
            sentencesList.add(sentence);
    }

    public void addVariable(NodeLocalVariable variable) throws SemanticException {
        if (unit.isParameter(variable.getName()))
            throw new SemanticException("Ya hay un parametro "+variable.getName()+" en el ambiente de referenciamiento", variable.getName(), variable.getLine());

        if (isLocalVariable(variable.getName()))
            throw new SemanticException("Ya hay una variable "+variable.getName()+" definida en el ambiente de referenciamiento", variable.getName(), variable.getLine());

        variableHashMap.put(variable.getName(), variable);
    }

    public boolean isLocalVariable(String identifier) {
        if (variableHashMap.get(identifier) == null){
            if (nestingIn != null)
                return nestingIn.isLocalVariable(identifier);
            else return false;
        } else return true;
    }

    public NodeLocalVariable getLocalVariable(String identifier){
        if (variableHashMap.get(identifier) == null){
            if (nestingIn != null)
                return nestingIn.getLocalVariable(identifier);
            else return null;
        } else return variableHashMap.get(identifier) ;
    }

    public void check() throws SemanticException {
        // Set los valores en la tabla de simbolos
        symbolTable.currentClass = ownerClass;
        symbolTable.currentUnit = unit;
        symbolTable.currentBlock = this;
        for (NodeSentence sentence : sentencesList){
            sentence.check();
        }
        // Retorna al bloque padre una vez termina el check
        symbolTable.currentBlock = nestingIn;
    }

    public boolean isVariableDeclaration() {
        return false;
    }

}
