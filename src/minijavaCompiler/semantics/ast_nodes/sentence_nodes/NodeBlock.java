package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static minijavaCompiler.Main.symbolTable;

public class NodeBlock implements NodeSentence {

    public List<NodeSentence> sentencesList;
    public HashMap<String, NodeLocalVariable> variableHashMap;
    public Unit unit;
    public NodeBlock nestingIn;

    public NodeBlock(){
        sentencesList = new ArrayList<>();
        variableHashMap = new HashMap<>();
        unit = symbolTable.currentUnit;         // Usado para acceder a los atributos/variables de instancia
        nestingIn = symbolTable.currentBlock;   // Usado para recuperar a donde volver cuando termino de leer este bloque
        if (nestingIn != null) {
            variableHashMap.putAll(nestingIn.variableHashMap);
        }
    }

    public void addSentence(NodeSentence sentence) throws SemanticException {
        sentencesList.add(sentence);
        if (sentence.isVariableDeclaration())
            addVariable((NodeLocalVariable) sentence);
    }

    private void addVariable(NodeLocalVariable variable) throws SemanticException {
        if (variableHashMap.get(variable.getName()) == null) {
            variableHashMap.put(variable.getName(), variable);
        } else throw new SemanticException("Ya hay una variable "+variable.getName()+" definida en el ambiente de referenciamiento", variable.getName(), variable.getLine());
    }

    public boolean isLocalVariable(String identifier){return variableHashMap.get(identifier) != null;}
    public NodeLocalVariable getLocalVariable(String identifier){return variableHashMap.get(identifier);}

    public void check(){
        symbolTable.currentUnit = unit;
        symbolTable.currentBlock = this;
        for (NodeSentence sentence : sentencesList){
            sentence.check();
        }
        // el return si el bloque es de metodo
        symbolTable.currentBlock = nestingIn; // retorna al bloque padre una vez termina el check
    }

    public boolean isVariableDeclaration() {
        return false;
    }

}
