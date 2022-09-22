package minijavaCompiler.semantics;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.entries.classes.ConcreteClass;
import minijavaCompiler.semantics.entries.Method;
import minijavaCompiler.semantics.entries.Parameter;
import minijavaCompiler.semantics.entries.types.*;
import minijavaCompiler.semantics.entries.types.primitives.BoolType;
import minijavaCompiler.semantics.entries.types.primitives.CharType;
import minijavaCompiler.semantics.entries.types.primitives.IntType;
import minijavaCompiler.semantics.entries.types.primitives.VoidType;

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
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printB(boolean b)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printB",0));
        symbolTable.currentUnit.addParameter(new Parameter(new BoolType(),new Token(mvID,"b",0)));
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printC(char c)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printC",0));
        symbolTable.currentUnit.addParameter(new Parameter(new CharType(),new Token(mvID,"c",0)));
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printI(int i)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printI",0));
        symbolTable.currentUnit.addParameter(new Parameter(new IntType(),new Token(mvID,"i",0)));
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printS(String s)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printS",0));
        symbolTable.currentUnit.addParameter(new Parameter(new ReferenceType(new Token(classID,"String",0)),new Token(mvID,"s",0)));
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void println()
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"println",0));
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printBln(boolean b)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printBln",0));
        symbolTable.currentUnit.addParameter(new Parameter(new BoolType(),new Token(mvID,"b",0)));
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printCln(char c)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printCln",0));
        symbolTable.currentUnit.addParameter(new Parameter(new CharType(),new Token(mvID,"c",0)));
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printIln(int i)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printIln",0));
        symbolTable.currentUnit.addParameter(new Parameter(new IntType(),new Token(mvID,"i",0)));
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        // static void printSln(String s)
        symbolTable.currentUnit = new Method(true,new VoidType(),new Token(mvID,"printSln",0));
        symbolTable.currentUnit.addParameter(new Parameter(new ReferenceType(new Token(classID,"String",0)),new Token(mvID,"s",0)));
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        //
        symbolTable.saveCurrentClass();
    }

}
