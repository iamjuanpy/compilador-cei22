package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.Method;
import minijavaCompiler.semantics.entries.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static minijavaCompiler.Main.symbolTable;

public class NodeBlock implements NodeSentence {

    public List<NodeSentence> sentencesList;
    public HashMap<String, NodeVariable> variableHashMap;
    public Unit unit;
    public NodeBlock nestingIn;

    public NodeBlock(){
        sentencesList = new ArrayList<>();
        variableHashMap = new HashMap<>();
        unit = symbolTable.currentUnit;
        nestingIn = symbolTable.currentBlock;
    }

    public void addSentence(NodeSentence sentence){
        sentencesList.add(sentence);
        if (sentence.isVariableDeclaration())
            addVariable((NodeVariable) sentence);
    }

    private void addVariable(NodeVariable variable) /* throws SemanticException */{
//        if (variableHashMap.get(variable.getName()) == null) {
//            variableHashMap.put(variable.getName(), variable);
//        } else throw new SemanticException();
    }

    public void check(){
        for (NodeSentence s : sentencesList){
            s.check();
        }
        // el return si el bloque es de metodo
    }

    public boolean isVariableDeclaration() {
        return false;
    }

}
