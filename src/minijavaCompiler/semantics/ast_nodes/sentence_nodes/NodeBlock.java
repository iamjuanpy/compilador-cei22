package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.semantics.entries.Method;
import minijavaCompiler.semantics.entries.Unit;

import java.util.HashMap;
import java.util.List;

public class NodeBlock implements NodeSentence {

    public List<NodeSentence> sentencesList;
    public HashMap<String, NodeVariable> variableHashMap;
    public Unit unit;
    public NodeBlock nestingIn;

    public NodeBlock(){
        sentencesList = null; // La lista la agrega, ListaSentencias en el sintáctico
        nestingIn = null; // Por default, el bloque no esta anidado en ningun otro
    }

    public void check(){
        for (NodeSentence s : sentencesList){
            s.check();
        }
        // el return si el bloque es de metodo
    }

}
