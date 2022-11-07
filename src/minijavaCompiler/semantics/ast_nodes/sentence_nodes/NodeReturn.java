package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.entries.Method;
import minijavaCompiler.semantics.entries.Unit;
import minijavaCompiler.semantics.types.primitives.VoidType;

import static minijavaCompiler.Main.symbolTable;

public class NodeReturn implements NodeSentence{

    private Token token;
    private NodeExpression expression;

    private Unit unit;
    private NodeBlock block;
    private int parameterCount;

    public NodeReturn(Token token, NodeExpression expression) {
        this.token = token;
        this.expression = expression;
    }

    public void check() throws SemanticException {
        unit = symbolTable.currentUnit;

        block = symbolTable.currentBlock;
        if (unit.isMethod()) {

            if (returnHasValue() && methodIsVoid()) // void m(){ return 1;}
                throw new SemanticException("Un metodo tipo void no puede retornar un valor",token.lexeme, token.lineNumber);

            if (!returnHasValue() && !methodIsVoid()) // int m(){ return;}
                throw new SemanticException("Un metodo tipo "+symbolTable.currentUnit.getReturnType().getTypeName()+" no puede retornar nada",token.lexeme, token.lineNumber);

            if (returnHasValue() && !expressionTypeIsSubtypeOfReturnType()) // boolean m(){ return "hola";}
                throw new SemanticException("Un metodo tipo "+symbolTable.currentUnit.getReturnType().getTypeName()+" no puede retornar un tipo "+expression.check().getTypeName(), token.lexeme, token.lineNumber);

        } else if (returnHasValue()) // Constructor() { return 'a';}
            throw new SemanticException("Un constructor no puede tener return no vacio",token.lexeme, token.lineNumber);
    }

    private boolean expressionTypeIsSubtypeOfReturnType() throws SemanticException {return expression.check().isSubtypeOf(symbolTable.currentUnit.getReturnType());}

    private boolean methodIsVoid() {return symbolTable.currentUnit.getReturnType().equals(new VoidType());}

    private boolean returnHasValue() {return expression != null;}

    public boolean isReturn(){return true;}
    public boolean isVariableDeclaration() {return false;}

    public void generateCode() {
        parameterCount = unit.getParametersList().size();
        if (expression != null) {
            generateStoreCode(); // Return tiene valor, lo guarda donde debe
        }
        generateReturnCode(); // Libera parametros, var locales y vuelve de la unidad
    }

    private void generateStoreCode() {
        int offsetReturn;
        expression.generateCode();
        offsetReturn = ((Method)unit).isStatic() ? parameterCount + 3 : parameterCount + 4; // Estatico: PR, ED, Parametros, Ret || Dinamico: PR, ED, THIS, Parametros, Ret
        symbolTable.ceiASM_instructionList.add("    STORE "+offsetReturn+" ; Guarda retorno en su lugar");
    }

    private void generateReturnCode() {
        int localVariablesToFree,memToFree;
        memToFree = unit.isMethod() && ((Method) unit).isStatic() ? parameterCount : parameterCount + 1; // Si es dinamico o constructor, tiene que borrar el this
        localVariablesToFree = block.getAmountOfVariablesInMemory();
        symbolTable.ceiASM_instructionList.add("    FMEM "+localVariablesToFree+" ; Borra variables locales reservadas");
        symbolTable.ceiASM_instructionList.add("    STOREFP ; Usa ED para volver a RA llamador");
        symbolTable.ceiASM_instructionList.add("    RET "+memToFree+" ; Libera los parametros y retorna de la unidad");
    }

}
