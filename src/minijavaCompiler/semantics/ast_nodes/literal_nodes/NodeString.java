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
        String stringLabel = symbolTable.getUniqueLabel();
        symbolTable.ceiASM_instructionList.add(".data"); // Ver para logro coercion
        symbolTable.ceiASM_instructionList.add(stringLabel+": DW "+token.lexeme+",0");
        symbolTable.ceiASM_instructionList.add("");
        symbolTable.ceiASM_instructionList.add(".code");
        symbolTable.ceiASM_instructionList.add("    PUSH "+stringLabel+" ; Direccion del string");
        symbolTable.ceiASM_instructionList.add("");
    }

}
