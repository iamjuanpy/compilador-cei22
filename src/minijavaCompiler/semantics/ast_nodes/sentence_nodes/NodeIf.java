package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.types.primitives.BoolType;

import static minijavaCompiler.Main.symbolTable;

public class NodeIf implements NodeSentence{

    private Token ifToken;
    private NodeExpression condition;
    private NodeSentence thenSentence;
    private NodeSentence elseSentence;

    public NodeIf(Token id, NodeExpression expression, NodeSentence thenSentence, NodeSentence elseSentence) {
        this.ifToken = id;
        this.condition = expression;
        this.thenSentence = thenSentence;
        this.elseSentence = elseSentence;
    }

    public void check() throws SemanticException {
        if (conditionIsBooleanExpression()) {
            checkThen();
            checkElseIfExists();
        } else throw new SemanticException("La condicion de un bloque if debe ser una expresión booleana", ifToken.lexeme, ifToken.lineNumber);
    }

    private void checkThen() throws SemanticException {
        thenSentence.check();
        if (thenSentence.isVariableDeclaration())
            throw new SemanticException("No se puede solo declarar una variable en un then", ifToken.lexeme, ifToken.lineNumber);
    }

    private void checkElseIfExists() throws SemanticException {
        if (elseSentence != null) {
            elseSentence.check();
            if (elseSentence.isVariableDeclaration())
                throw new SemanticException("No se puede solo declarar una variable en un else", ifToken.lexeme, ifToken.lineNumber);
        }
    }

    private boolean conditionIsBooleanExpression() throws SemanticException {return condition.check().equals(new BoolType());}

    public boolean isReturn(){
        if (elseSentence == null)
            return false;
        else return thenSentence.isReturn() && elseSentence.isReturn();
    }

    public boolean isVariableDeclaration() {return false;}

    public void generateCode() {
        if (elseSentence == null)
            generateIfCode();
        else generateIfElseCode();
    }

    private void generateIfCode() {
        String outIfLabel = symbolTable.getUniqueLabel();
        condition.generateCode();
        symbolTable.ceiASM_instructionList.add("    BF "+outIfLabel+" ; Si es falso, salto al final de then, sino ejecuto then");
        thenSentence.generateCode();
        symbolTable.ceiASM_instructionList.add(outIfLabel+": NOP ; Final del then");
    }

    private void generateIfElseCode() {
        String elseLabel = symbolTable.getUniqueLabel();
        String outIfLabel = symbolTable.getUniqueLabel();
        condition.generateCode();
        symbolTable.ceiASM_instructionList.add("    BF "+elseLabel+" ; Si es falso, salta a else, sino ejecuto then");
        thenSentence.generateCode();
        symbolTable.ceiASM_instructionList.add("    JUMP "+outIfLabel+" ; Termina de ejecutar then, salta a final de if");
        symbolTable.ceiASM_instructionList.add(elseLabel+": NOP ; Principio de else");
        elseSentence.generateCode();
        symbolTable.ceiASM_instructionList.add(outIfLabel+": NOP ; Final del if");
    }
}
