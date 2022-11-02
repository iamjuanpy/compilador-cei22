package minijavaCompiler.semantics.ast_nodes.literal_nodes;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.lexical.TokenType;
import minijavaCompiler.semantics.types.ReferenceType;
import minijavaCompiler.semantics.types.Type;

import static minijavaCompiler.Main.symbolTable;

public class NodeString implements NodeLiteral {

    private Token token;

    public NodeString(Token literal){
        this.token = literal;
    }

    public Type check(){return new ReferenceType(new Token(TokenType.classID, "String", 0));}

    public void generateCode() {
        //symbolTable.ceiASM_instructionList.add("    DW "+token.lexeme+",0"); TODO Los objetos string no tienen atributos, y podes declarar "objetos" implicitamente
    }

}
