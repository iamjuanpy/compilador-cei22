package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.types.primitives.BoolType;

import static minijavaCompiler.Main.symbolTable;

public class NodeWhile implements NodeSentence {

    private Token whileToken;
    private NodeExpression condition;
    private NodeSentence sentence;

    public NodeWhile(Token id, NodeExpression expression, NodeSentence sentence) {
        this.whileToken = id;
        this.condition = expression;
        this.sentence = sentence;
    }

    public void check() throws SemanticException {
        if (conditionIsBooleanExpression()) {
            sentence.check();
            if (sentence.isVariableDeclaration())
                throw new SemanticException("No se puede solo declarar una variable en un while", whileToken.lexeme, whileToken.lineNumber);
        } else throw new SemanticException("La condicion de un bloque while debe ser una expresión booleana", whileToken.lexeme, whileToken.lineNumber);
    }

    private boolean conditionIsBooleanExpression() throws SemanticException {return condition.check().equals(new BoolType());}

    public boolean isReturn(){return false;}
    public boolean isVariableDeclaration() {return false;}

    public void generateCode() {
        String conditionLabel = symbolTable.getUniqueLabel();
        String outOfWhileLabel = symbolTable.getUniqueLabel();
        symbolTable.ceiASM_instructionList.add(conditionLabel+": NOP ; Condicion de while");
        condition.generateCode(); // Codigo evaluar condicion
        symbolTable.ceiASM_instructionList.add("    BF "+outOfWhileLabel+" ; Si no cumple, sale");
        sentence.generateCode(); // Codigo a repetir
        symbolTable.ceiASM_instructionList.add("    JUMP "+conditionLabel+" ; Prueba condicion de nuevo");
        symbolTable.ceiASM_instructionList.add(outOfWhileLabel+": NOP ; Fin de while");
    }
}
