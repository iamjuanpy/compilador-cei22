package minijavaCompiler.semantics.ast_nodes.sentence_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.NodeAccess;
import minijavaCompiler.semantics.types.Type;
import minijavaCompiler.semantics.types.primitives.VoidType;

import static minijavaCompiler.Main.symbolTable;

public class NodeCall implements NodeSentence{

    private NodeAccess access;
    private Type returnType;
    private Token semicolonToken;

    public NodeCall(NodeAccess access, Token semicolonToken){
        this.access = access;
        this.semicolonToken = semicolonToken;
    }

    public void check() throws SemanticException {
        returnType = access.check();
        if (!access.isMethodCall()) // Chequea que sea llamada a const/metodo/metodo estatico o multiples encadenados con encadenado final metodo
            throw new SemanticException("Se esperaba una llamada a método estático o dinámico/constructor", semicolonToken.lexeme, semicolonToken.lineNumber);
    }

    public boolean isReturn(){return false;}
    public boolean isVariableDeclaration() {return false;}

    public void generateCode() {
        access.generateCode();
        if (!returnType.equals(new VoidType())){ // Si la llamada reservo lugar para retorno y no se utiliza lo pierdo
            symbolTable.ceiASM_instructionList.add("    POP ; Borro retorno, que no me sirve");
        }
    }

}
