package minijavaCompiler.semantics;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.ast_nodes.sentence_nodes.NodeBlock;
import minijavaCompiler.semantics.entries.classes.ConcreteClass;
import minijavaCompiler.semantics.entries.Method;
import minijavaCompiler.semantics.entries.Parameter;
import minijavaCompiler.semantics.types.*;
import minijavaCompiler.semantics.types.primitives.BoolType;
import minijavaCompiler.semantics.types.primitives.CharType;
import minijavaCompiler.semantics.types.primitives.IntType;
import minijavaCompiler.semantics.types.primitives.VoidType;

import static minijavaCompiler.Main.symbolTable;
import static minijavaCompiler.lexical.TokenType.classID;
import static minijavaCompiler.lexical.TokenType.mvID;

public class DefaultClasses {

    public static void instanceSymbolTableDefaults(){

        try {
            buildObject();
            buildString();
            buildSystem();
        } catch (SemanticException e) {
            // imposible, por eso no throweo
        }

    }

    private static void buildObject() throws SemanticException {
        symbolTable.setCurrentClass(new ConcreteClass(new Token(classID,"Object",0)));
        symbolTable.currentClass.setAncestorClass(null); // unica clase que no hereda
        // static void debugPrint(int i)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"debugPrint",0));
        symbolTable.currentUnit.addParameter(new Parameter(new IntType(),new Token(mvID,"i",0)));
        symbolTable.currentUnit.addBlock(new NodeBlock());
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        //
        symbolTable.saveCurrentClass();
    }

    private static void buildString() throws SemanticException {
        symbolTable.setCurrentClass(new ConcreteClass(new Token(classID,"String",0)));
        symbolTable.currentClass.setAncestorClass(new Token(classID,"Object",0));
        symbolTable.saveCurrentClass();
    }

    private static void buildSystem() throws SemanticException {
        symbolTable.setCurrentClass(new ConcreteClass(new Token(classID,"System",0)));
        symbolTable.currentClass.setAncestorClass(new Token(classID,"Object",0));
        // static int read()
        symbolTable.currentUnit = new Method(true,new IntType(),new Token(mvID,"read",0));
        symbolTable.currentUnit.addBlock(new NodeBlock());
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printB(boolean b)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printB",0));
        symbolTable.currentUnit.addParameter(new Parameter(new BoolType(),new Token(mvID,"b",0)));
        symbolTable.currentUnit.addBlock(new NodeBlock());
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printC(char c)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printC",0));
        symbolTable.currentUnit.addParameter(new Parameter(new CharType(),new Token(mvID,"c",0)));
        symbolTable.currentUnit.addBlock(new NodeBlock());
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printI(int i)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printI",0));
        symbolTable.currentUnit.addParameter(new Parameter(new IntType(),new Token(mvID,"i",0)));
        symbolTable.currentUnit.addBlock(new NodeBlock());
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printS(String s)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printS",0));
        symbolTable.currentUnit.addParameter(new Parameter(new ReferenceType(new Token(classID,"String",0)),new Token(mvID,"s",0)));
        symbolTable.currentUnit.addBlock(new NodeBlock());
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void println()
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"println",0));
        symbolTable.currentUnit.addBlock(new NodeBlock());
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printBln(boolean b)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printBln",0));
        symbolTable.currentUnit.addParameter(new Parameter(new BoolType(),new Token(mvID,"b",0)));
        symbolTable.currentUnit.addBlock(new NodeBlock());
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printCln(char c)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printCln",0));
        symbolTable.currentUnit.addParameter(new Parameter(new CharType(),new Token(mvID,"c",0)));
        symbolTable.currentUnit.addBlock(new NodeBlock());
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printIln(int i)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printIln",0));
        symbolTable.currentUnit.addParameter(new Parameter(new IntType(),new Token(mvID,"i",0)));
        symbolTable.currentUnit.addBlock(new NodeBlock());
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printSln(String s)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printSln",0));
        symbolTable.currentUnit.addParameter(new Parameter(new ReferenceType(new Token(classID,"String",0)),new Token(mvID,"s",0)));
        symbolTable.currentUnit.addBlock(new NodeBlock());
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        //
        symbolTable.saveCurrentClass();
    }

    public static void generateDefaultMethodsCode(){
        translateObjectMethods();
        translateSystemMethods();
    }

    private static void translateObjectMethods() {
        // static void debugPrint(int)
        symbolTable.ceiASM_instructionList.add("Object_debugPrint:");
        symbolTable.ceiASM_instructionList.add("    LOADFP");
        symbolTable.ceiASM_instructionList.add("    LOADSP");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    RET 1");
        symbolTable.ceiASM_instructionList.add("");
    }

    private static void translateSystemMethods() {
        // static int read()
        symbolTable.ceiASM_instructionList.add("System_read:");
        symbolTable.ceiASM_instructionList.add("    LOADFP");
        symbolTable.ceiASM_instructionList.add("    LOADSP");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    READ ; Lee tope de la pila");
        symbolTable.ceiASM_instructionList.add("    STORE 3 ; Guarda en retorno el tope");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    RET 0");
        symbolTable.ceiASM_instructionList.add("");
        // static void printB(boolean)
        symbolTable.ceiASM_instructionList.add("System_printB:");
        symbolTable.ceiASM_instructionList.add("    LOADFP");
        symbolTable.ceiASM_instructionList.add("    LOADSP");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    LOAD 3 ; Pone en el tope la pila el parametro");
        symbolTable.ceiASM_instructionList.add("    BPRINT ; Imprime el tope de la pila");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    RET 1 ; Libera el parametro");
        symbolTable.ceiASM_instructionList.add("");
        // static void printC(char)
        symbolTable.ceiASM_instructionList.add("System_printC:");
        symbolTable.ceiASM_instructionList.add("    LOADFP");
        symbolTable.ceiASM_instructionList.add("    LOADSP");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    LOAD 3 ; Pone en el tope la pila el parametro");
        symbolTable.ceiASM_instructionList.add("    CPRINT ; Imprime el tope de la pila");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    RET 1 ; Libera el parametro");
        symbolTable.ceiASM_instructionList.add("");
        // static void printI(integer)
        symbolTable.ceiASM_instructionList.add("System_printI:");
        symbolTable.ceiASM_instructionList.add("    LOADFP");
        symbolTable.ceiASM_instructionList.add("    LOADSP");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    LOAD 3 ; Pone en el tope la pila el parametro");
        symbolTable.ceiASM_instructionList.add("    IPRINT ; Imprime el tope de la pila");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    RET 1 ; Libera el parametro");
        symbolTable.ceiASM_instructionList.add("");
        // static void printS(string)
        symbolTable.ceiASM_instructionList.add("System_printS:");
        symbolTable.ceiASM_instructionList.add("    LOADFP");
        symbolTable.ceiASM_instructionList.add("    LOADSP");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    LOAD 3 ; Pone en el tope la pila el parametro");
        symbolTable.ceiASM_instructionList.add("    SPRINT ; Imprime el tope de la pila");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    RET 1 ; Libera el parametro");
        symbolTable.ceiASM_instructionList.add("");
        // static void println()
        symbolTable.ceiASM_instructionList.add("System_println:");
        symbolTable.ceiASM_instructionList.add("    LOADFP");
        symbolTable.ceiASM_instructionList.add("    LOADSP");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    PRNLN");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    RET 0");
        symbolTable.ceiASM_instructionList.add("");
        // static void printBln(boolean)
        symbolTable.ceiASM_instructionList.add("System_printBln:");
        symbolTable.ceiASM_instructionList.add("    LOADFP");
        symbolTable.ceiASM_instructionList.add("    LOADSP");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    LOAD 3 ; Pone en el tope la pila el parametro");
        symbolTable.ceiASM_instructionList.add("    BPRINT ; Imprime el tope de la pila");
        symbolTable.ceiASM_instructionList.add("    PRNLN");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    RET 1 ; Libera el parametro");
        symbolTable.ceiASM_instructionList.add("");
        // static void printCln(char)
        symbolTable.ceiASM_instructionList.add("System_printCln:");
        symbolTable.ceiASM_instructionList.add("    LOADFP");
        symbolTable.ceiASM_instructionList.add("    LOADSP");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    LOAD 3 ; Pone en el tope la pila el parametro");
        symbolTable.ceiASM_instructionList.add("    CPRINT ; Imprime el tope de la pila");
        symbolTable.ceiASM_instructionList.add("    PRNLN");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    RET 1 ; Libera el parametro");
        symbolTable.ceiASM_instructionList.add("");
        // static void printIln(integer)
        symbolTable.ceiASM_instructionList.add("System_printIln:");
        symbolTable.ceiASM_instructionList.add("    LOADFP");
        symbolTable.ceiASM_instructionList.add("    LOADSP");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    LOAD 3 ; Pone en el tope la pila el parametro");
        symbolTable.ceiASM_instructionList.add("    IPRINT ; Imprime el tope de la pila");
        symbolTable.ceiASM_instructionList.add("    PRNLN");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    RET 1 ; Libera el parametro");
        symbolTable.ceiASM_instructionList.add("");
        // static void printSln(string)
        symbolTable.ceiASM_instructionList.add("System_printSln:");
        symbolTable.ceiASM_instructionList.add("    LOADFP");
        symbolTable.ceiASM_instructionList.add("    LOADSP");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    LOAD 3 ; Pone en el tope la pila el parametro");
        symbolTable.ceiASM_instructionList.add("    SPRINT ; Imprime el tope de la pila");
        symbolTable.ceiASM_instructionList.add("    PRNLN");
        symbolTable.ceiASM_instructionList.add("    STOREFP");
        symbolTable.ceiASM_instructionList.add("    RET 1 ; Libera el parametro");
        symbolTable.ceiASM_instructionList.add("");
    }

}
