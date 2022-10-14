package minijavaCompiler.syntax;

import minijavaCompiler.file_manager.SourceFileReaderException;
import minijavaCompiler.lexical.LexicalAnalyser;
import minijavaCompiler.lexical.Token;
import minijavaCompiler.lexical.TokenType;
import minijavaCompiler.lexical.exceptions.LexicalException;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.ast_nodes.access_nodes.*;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeChaining;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeMethodChaining;
import minijavaCompiler.semantics.ast_nodes.access_nodes.chaining.NodeVarChaining;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeBinaryExpression;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeExpression;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeOperand;
import minijavaCompiler.semantics.ast_nodes.expression_nodes.NodeUnaryExpression;
import minijavaCompiler.semantics.ast_nodes.literal_nodes.*;
import minijavaCompiler.semantics.ast_nodes.sentence_nodes.*;
import minijavaCompiler.semantics.entries.classes.ConcreteClass;
import minijavaCompiler.semantics.entries.classes.Interface;
import minijavaCompiler.semantics.entries.Attribute;
import minijavaCompiler.semantics.entries.Constructor;
import minijavaCompiler.semantics.entries.Method;
import minijavaCompiler.semantics.entries.Parameter;
import minijavaCompiler.semantics.types.ReferenceType;
import minijavaCompiler.semantics.types.Type;
import minijavaCompiler.semantics.types.primitives.BoolType;
import minijavaCompiler.semantics.types.primitives.CharType;
import minijavaCompiler.semantics.types.primitives.IntType;
import minijavaCompiler.semantics.types.primitives.VoidType;
import minijavaCompiler.syntax.exceptions.SyntacticException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static minijavaCompiler.Main.symbolTable;
import static minijavaCompiler.lexical.TokenType.*;

public class SyntaxParser {

    LexicalAnalyser lexicalAnalyser;
    Token currentToken;

    public SyntaxParser(LexicalAnalyser lexicalAnalyser) throws LexicalException, SourceFileReaderException{
        this.lexicalAnalyser = lexicalAnalyser;
        currentToken = lexicalAnalyser.getNextToken();
    }

    private void match(TokenType expectedTokenType) throws LexicalException, SourceFileReaderException, SyntacticException {
        if (expectedTokenType == currentToken.tokenType){
            currentToken = lexicalAnalyser.getNextToken();
        } else {
            throw new SyntacticException(expectedTokenType,currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    public void startParse() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        inicial();
    }

    private void inicial() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        listaClases();
        symbolTable.eofToken = currentToken;
        match(eof);
    }

    private void listaClases() throws SyntacticException, LexicalException, SourceFileReaderException, SemanticException {
        if (currentToken.tokenType == r_class || currentToken.tokenType == r_interface) {
            clase();
            listaClases();
        } else {
            if (currentToken.tokenType == eof) { // Siguientes(...) = EOF
                // nada
            } else throw new SyntacticException("Se esperaba una clase o el fin del archivo", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void clase() throws SyntacticException, LexicalException, SourceFileReaderException, SemanticException {
        if (currentToken.tokenType == r_class) {
            claseConcreta();
        } else if (currentToken.tokenType == r_interface) {
            nt_interface();
        } else throw new SyntacticException("Se esperaba declaración de clase o interface", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void claseConcreta() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        match(r_class);
        symbolTable.setCurrentClass(new ConcreteClass(currentToken));
        symbolTable.currentBlock = null;
        match(classID);
        heredaDe();
        implementaA();
        match(openCurly);
        listaMiembros();
        match(closeCurly);
        symbolTable.saveCurrentClass();
    }

    private void nt_interface() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        match(r_interface);
        symbolTable.setCurrentClass(new Interface(currentToken));
        match(classID);
        extiendeA();
        match(openCurly);
        listaEncabezados();
        match(closeCurly);
        symbolTable.saveCurrentClass();
    }

    private void heredaDe() throws LexicalException, SourceFileReaderException, SyntacticException { // ONLY CONCRETA
        if(currentToken.tokenType == r_extends){
            match(r_extends);
            Token classToken = currentToken;
            match(classID);
            symbolTable.currentClass.setAncestorClass(classToken);
        } else {
            if (currentToken.tokenType == openCurly || currentToken.tokenType == r_implements) { // Siguientes(...) = { implements, { }
                //nada
            } else throw new SyntacticException("Se esperaba el bloque de la clase/interface", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void implementaA() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException { // ONLY CONCRETA
        if(currentToken.tokenType == r_implements){
            match(r_implements);
            listaTipoReferencia();
        } else {
            if (currentToken.tokenType == openCurly) { // Siguientes(...) = { { }
                //nada
            } else throw new SyntacticException("Se esperaba el bloque de la clase/interface", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void extiendeA() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException { // ONLY INTERFACE
        if(currentToken.tokenType == r_extends){
            match(r_extends);
            listaTipoReferencia();
        } else {
            if (currentToken.tokenType == openCurly) { // Siguientes(...) = { { }
                // nada
            } else throw new SyntacticException("Se esperaba el bloque de la clase/interface", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void listaTipoReferencia() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        Token classOrInterfaceToken = currentToken;
        match(classID);
        symbolTable.currentClass.addMultipleInheritence(classOrInterfaceToken);
        listaTipoReferenciaFactorizada();
    }

    private void listaTipoReferenciaFactorizada() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        if (currentToken.tokenType == comma) {
            match(comma);
            listaTipoReferencia();
        } else {
            if (currentToken.tokenType == openCurly) { // Siguientes(...) = { { }
                //nada
            } else throw new SyntacticException("Se esperaba el bloque de la clase/interface", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void listaMiembros() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        TokenType[] primerosMiembro = {r_public, r_private, r_static, r_boolean, r_int, r_char, classID, r_void};
        if (Arrays.asList(primerosMiembro).contains(currentToken.tokenType)) {
            miembro();
            listaMiembros();
        } else {
            if (currentToken.tokenType == closeCurly) { // Siguientes(...) = { } }
                //nada
            } else throw new SyntacticException("Se esperaba un método/atributo o el cierre de la clase", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void listaEncabezados() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        TokenType[] primerosEncabezadoMet = {r_static, r_boolean, r_int, r_char, classID, r_void};
        if (Arrays.asList(primerosEncabezadoMet).contains(currentToken.tokenType)) {
            encabezadoMetodo();
            match(semicolon);
            symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
            listaEncabezados();
        } else {
            if (currentToken.tokenType == closeCurly) { // Siguiente(...) = { } }
                // nada
            } else throw new SyntacticException("Se esperaba un encabezado de método o el cierre de la interface", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void miembro() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        if (currentToken.tokenType == r_public || currentToken.tokenType == r_private) {
            atributo();
        } else if ( currentToken.tokenType == r_static || currentToken.tokenType == r_void || currentToken.tokenType == r_boolean || currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == classID){
            atributoOMetodoOConstructor();
        } else throw new SyntacticException("Se esperaba declaración de atributo o método", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void atributo() throws SyntacticException, LexicalException, SourceFileReaderException, SemanticException {
        boolean esPublic = visibilidad();
        Type tipo = tipo();
        listaDecAtrs(esPublic,tipo);
        match(semicolon);
    }

    private void atributoOMetodoOConstructor() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        if (currentToken.tokenType == r_boolean || currentToken.tokenType == r_int || currentToken.tokenType == r_char ) {
            Type tipo = tipoPrimitivo();
            Token tokenIdentificador = currentToken;
            match(mvID);
            atributoOmisionOMetodo(tipo, tokenIdentificador);
        }else if (currentToken.tokenType == classID){
            Type tipo = new ReferenceType(currentToken);
            Token tokenIdentificador = currentToken;
            match(classID);
            constructorOAttrOMetodo(tipo, tokenIdentificador); // Guardo el id de clase como tipo y como constructor
        }else if (currentToken.tokenType == r_void || currentToken.tokenType == r_static) {
            encabezadoMetodo();
            symbolTable.currentUnit.addBlock(bloque());
            symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        } else throw new SyntacticException("Se esperaba declaración de atributo o método o constructor", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void constructorOAttrOMetodo(Type tipo, Token tokenIdentificador) throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        if (currentToken.tokenType == openBr){
            symbolTable.currentUnit = new Constructor(tokenIdentificador);
            symbolTable.currentBlock = null;
            argsFormales();
            symbolTable.currentUnit.addBlock(bloque());
            symbolTable.currentClass.addConstructor((Constructor) symbolTable.currentUnit);
        } else if (currentToken.tokenType == mvID) {
            Token nombreMetodoOAttr = currentToken;
            match(mvID);
            atributoOmisionOMetodo(tipo, nombreMetodoOAttr);
        } else throw new SyntacticException("Se esperaba declaración de atributo o método o constructor", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void atributoOmisionOMetodo(Type tipo, Token tokenIdentificador) throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        if (currentToken.tokenType == comma || currentToken.tokenType == semicolon) {
            symbolTable.currentClass.addAttribute(new Attribute(true,tipo,tokenIdentificador));
            listaDecAtrsFactorizada(true, tipo);
            match(semicolon);
        } else if (currentToken.tokenType == openBr) {
            symbolTable.currentUnit = new Method(false, tipo, tokenIdentificador);
            symbolTable.currentBlock = null;
            argsFormales();
            symbolTable.currentUnit.addBlock(bloque());
            symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
        } else throw new SyntacticException("Se esperaba declaración de atributo o método o constructor", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void encabezadoMetodo() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        //TokenType[] primerosEncabezadoMet = {r_static, r_boolean, r_int, r_char, classID, r_void};
        boolean esEstatico = estaticoOpt();
        Type tipo = tipoMetodo();
        Token tokenMetodo = currentToken;
        match(mvID);
        symbolTable.currentUnit = new Method(esEstatico, tipo, tokenMetodo);
        symbolTable.currentBlock = null;
        argsFormales();
    }

    private boolean visibilidad() throws SyntacticException, LexicalException, SourceFileReaderException {
        if (currentToken.tokenType == r_public){
            match(r_public);
            return true;
        } else if (currentToken.tokenType == r_private) {
            match(r_private);
            return false;
        } else throw new SyntacticException("Se esperaba identificador de visibilidad", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }
    private Type tipo() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == r_boolean) {
            return tipoPrimitivo();
        } else if (currentToken.tokenType == classID) {
            Token tokenTipo = currentToken;
            match(classID);
            return new ReferenceType(tokenTipo);
        } else throw new SyntacticException("Se esperaba identificador de tipo", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }
    private Type tipoPrimitivo() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_int){
            match(r_int);
            return new IntType();
        } else if (currentToken.tokenType == r_char){
            match(r_char);
            return new CharType();
        } else if (currentToken.tokenType == r_boolean) {
            match(r_boolean);
            return new BoolType();
        }else throw new SyntacticException("Se esperaba identificador de tipo", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void listaDecAtrs(boolean esPublic, Type tipoAtributo) throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        Token tokenAtributo = currentToken;
        match(mvID);
        symbolTable.currentClass.addAttribute(new Attribute(esPublic,tipoAtributo,tokenAtributo));
        listaDecAtrsFactorizada(esPublic,tipoAtributo);
    }
    private void listaDecAtrsFactorizada(boolean esPublic, Type tipoAtributo) throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        if (currentToken.tokenType == comma) {
            match(comma);
            listaDecAtrs(esPublic,tipoAtributo);
        } else {
            if (currentToken.tokenType == semicolon) { // Siguientes(...) = { ; }
                //nada
            } else throw new SyntacticException("Declaración de atributo sin cerrar", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private boolean estaticoOpt() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_static) {
            match(r_static);
            return true;
        } else {
            TokenType[] siguientesEstaticoOpt = {r_boolean, r_int, r_char, classID, r_void};
            if (Arrays.asList(siguientesEstaticoOpt).contains(currentToken.tokenType)) { // Siguientes(...) = Primeros(TipoMetodo) = {idClass, boolean, void, int, char}
                return false;
            } else throw new SyntacticException("Se esperaba identificador de tipo del método", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }
    private Type tipoMetodo() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == r_boolean || currentToken.tokenType == classID) {
            return tipo();
        } else if (currentToken.tokenType == r_void){
            match(r_void);
            return new VoidType();
        } else throw new SyntacticException("Se esperaba identificador de tipo del método", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void argsFormales() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        match(openBr);
        listaArgsFormalesOpt();
        match(closeBr);
    }
    private void listaArgsFormalesOpt() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        if (currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == r_boolean || currentToken.tokenType == classID){
            listaArgsFormales();
        } else {
            if (currentToken.tokenType == closeBr) { // Siguientes(...) = { ) }
                // nada
            } else throw new SyntacticException("Lista de argumentos sin cerrar, se esperaba )", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }
    private void listaArgsFormales() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        argFormal();
        listaArgsFormalesFactorizada();
    }
    private void listaArgsFormalesFactorizada() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        if (currentToken.tokenType == comma) {
            match(comma);
            listaArgsFormales();
        } else {
            if (currentToken.tokenType == closeBr) { // Siguientes(...) = { ) }
                // nada
            } else throw new SyntacticException("Lista de argumentos sin cerrar, se esperaba )", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void argFormal() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        Type tipoArgumento = tipo();
        Token tokenArgumento = currentToken;
        match(mvID);
        symbolTable.currentUnit.addParameter(new Parameter(tipoArgumento, tokenArgumento));
    }

    // Sentencias

    private NodeBlock bloque() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        match(openCurly);
        NodeBlock bloque = new NodeBlock();                         // Crea bloque en el scope actual (clase, método/constructor, bloque padre)
        symbolTable.currentBlock = bloque;                          // Cambia el bloque actual al bloque nuevo
        listaSentencias();                                          // Lee las sentencias del bloque
        symbolTable.currentBlock = bloque.nestingIn;                // Antes de retornar vuelve el bloque actual al anterior
        match(closeCurly);
        return bloque;
    }

    private void listaSentencias() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        TokenType[] primerosSentencia = {semicolon, mvID, r_return, r_var, r_if, r_while, openCurly, classID, openBr, r_this, r_new, r_int, r_char, r_boolean};
        if (Arrays.asList(primerosSentencia).contains(currentToken.tokenType)) {
            symbolTable.currentBlock.addSentence(sentencia());
            listaSentencias();
        } else {
            if (currentToken.tokenType == closeCurly) { // Siguientes(...) = { } }
                // nada
            } else throw new SyntacticException("Se esperaba una sentencia o fin de bloque", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private NodeSentence sentencia() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        NodeSentence sentence;
        if (currentToken.tokenType == semicolon){
            sentence = new NodeEmptySentence();
            match(semicolon);
        } else if (currentToken.tokenType == mvID || currentToken.tokenType == openBr || currentToken.tokenType == r_this || currentToken.tokenType == r_new){
            sentence = asignacionOLlamada();
            match(semicolon);
        } else if (currentToken.tokenType == classID || currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == r_boolean) {
            sentence = varLocalClasicaOMetodoEstatico();
            match(semicolon);
        } else if (currentToken.tokenType == r_var){
            sentence = varLocal();
            match(semicolon);
        } else if (currentToken.tokenType == r_return){
            sentence = nt_return();
            match(semicolon);
        } else if (currentToken.tokenType == r_if){
            sentence = nt_if();
        } else if (currentToken.tokenType == r_while){
            sentence = nt_while();
        } else if (currentToken.tokenType == openCurly){
            sentence = bloque();
        } else throw new SyntacticException("Se esperaba una sentencia", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        return sentence;
    }
    private NodeSentence asignacionOLlamada() throws LexicalException, SourceFileReaderException, SyntacticException {
        NodeAccess acceso = acceso();
        NodeSentence asignacionOLlamada = asignacionOLlamadaFactorizada(acceso);
        return asignacionOLlamada;
    }
    private NodeSentence asignacionOLlamadaFactorizada(NodeAccess nodoAcceso) throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == assign || currentToken.tokenType == addAssign || currentToken.tokenType == subAssign) {
            Token tipoAsignacion = tipoDeAsignacion();
            NodeExpression expresion = expresion();
            return new NodeAssign(nodoAcceso, tipoAsignacion, expresion);
        } else {
            if (currentToken.tokenType == semicolon) { // Siguientes(...) = { ; }
                return new NodeCall(nodoAcceso); // TODO PREGUNTAR ESTO
            } else throw new SyntacticException("Sentencia sin cerrar, se esperaba ;", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private Token tipoDeAsignacion() throws LexicalException, SourceFileReaderException, SyntacticException {
        Token tipoAsignacion = currentToken;
        if (currentToken.tokenType == assign){
            match(assign);
        } else if (currentToken.tokenType == addAssign) {
            match(addAssign);
        } else if (currentToken.tokenType == subAssign) {
            match(subAssign);
        } else throw new SyntacticException("Se esperaba un operador de asignación", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        return tipoAsignacion;
    }

    private NodeSentence varLocalClasicaOMetodoEstatico() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        if (currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == r_boolean) {
            Type tipo = tipoPrimitivo();
            varLocalClasica(tipo);
            return null;
        } else if (currentToken.tokenType == classID) {
            Token identificadorClase = currentToken;
            Type tipo = tipo();
            return varLocalClasicaOMetodoEstaticoFactorizado(identificadorClase, tipo);
        } else throw new SyntacticException("Se esperaba un tipo", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private NodeSentence varLocalClasicaOMetodoEstaticoFactorizado(Token idClase, Type tipo) throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        if (currentToken.tokenType == dot){
            match(dot);
            Token idMetodo = currentToken;
            match(mvID);
            NodeStaticMethodCall accesoMetStatic = new NodeStaticMethodCall(idClase, idMetodo);
            accesoMetStatic.setParameterList(argsActuales());
            accesoMetStatic.setChaining(encadenadoOpt());
            return asignacionOLlamadaFactorizada(accesoMetStatic);
        } else if (currentToken.tokenType == mvID) {
            varLocalClasica(tipo);
            return null;
        } else throw new SyntacticException("Se esperaba una declaración de variable o sentencia", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void varLocalClasica(Type tipo) throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        Token idVariable = currentToken;
        match(mvID);
        NodeSentence variable = asignacionOpcional(tipo, idVariable);
        symbolTable.currentBlock.addSentence(variable);
        varLocalClasicaFactorizada(tipo);
    }

    private NodeSentence asignacionOpcional(Type tipo, Token idVariable) throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == assign) {
            match(assign);
            NodeExpression valor = expresion();
            return new NodeLocalVariable(tipo, idVariable, valor);
        } else {
            if (currentToken.tokenType == comma || currentToken.tokenType == semicolon){ // Siguientes(...) = { , , ;}
                return new NodeLocalVariable(tipo, idVariable);
            } else throw new SyntacticException("Declaracion de variable sin cerrar", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void varLocalClasicaFactorizada(Type tipo) throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        if (currentToken.tokenType == comma){
            match(comma);
            varLocalClasica(tipo);
        } else {
            if (currentToken.tokenType == semicolon){ // Siguientes(...) = { , , ;}
                //
            } else throw new SyntacticException("Declaracion de variable sin cerrar", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private NodeSentence varLocal() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(r_var);
        Token identificador = currentToken;
        match(mvID);
        match(assign);
        NodeExpression expresion = expresion();
        return new NodeLocalVariable(identificador, expresion);
    }

    private NodeSentence nt_return() throws LexicalException, SourceFileReaderException, SyntacticException {
        Token tokenRetorno = currentToken;
        match(r_return);
        return new NodeReturn(tokenRetorno, expresionOpt());
    }

    private NodeExpression expresionOpt() throws LexicalException, SourceFileReaderException, SyntacticException {
        TokenType[] primerosExpresion = {addOP, subOP, not, r_null, r_true, r_false, intLit, charLit, strLit, r_this, mvID, r_new, openBr, classID};
        if (Arrays.asList(primerosExpresion).contains(currentToken.tokenType)) {
            return expresion();
        } else {
            if (currentToken.tokenType == semicolon) { // Siguientes(...) = { ; }
                return null;
            } else throw new SyntacticException("Return sin cerrar, se esperaba ;", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private NodeSentence nt_if() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        Token ifToken = currentToken;
        match(r_if);
        match(openBr);
        NodeExpression condicion = expresion();
        match(closeBr);
        NodeSentence sentenciaThen = sentencia();
        NodeSentence sentenciaElse = nt_else();
        return new NodeIf(ifToken, condicion, sentenciaThen, sentenciaElse);
    }
    private NodeSentence nt_else() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        if (currentToken.tokenType == r_else){
            match(r_else);
            return sentencia();
        } else {
            TokenType[] siguientesElseFactorizado = {semicolon, mvID, r_return, r_var, r_if, r_while, openCurly, classID, openBr, r_this, r_new, closeCurly, r_int, r_char, r_boolean};
            if (Arrays.asList(siguientesElseFactorizado).contains(currentToken.tokenType)){ // Siguiente(...) = { Primeros(<Sentencia>), } }
                return null;
            } else throw new SyntacticException("Se esperaba una sentencia o fin de bloque", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private NodeSentence nt_while() throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        Token whileToken = currentToken;
        match(r_while);
        match(openBr);
        NodeExpression condicion = expresion();
        match(closeBr);
        NodeSentence sentencia = sentencia();
        return new NodeWhile(whileToken, condicion, sentencia);
    }

    private NodeExpression expresion() throws LexicalException, SourceFileReaderException, SyntacticException {
        NodeExpression posibleLadoIzquierdo = expresionUnaria();
        return expresionRecursiva(posibleLadoIzquierdo);
    }
    private NodeExpression expresionRecursiva(NodeExpression ladoIzquierdo) throws LexicalException, SourceFileReaderException, SyntacticException {
        TokenType[] primerosOperadorBinario = {orOP, andOP, equals, notEquals, less, greater, lessOrEquals, greaterOrEquals, addOP, subOP, multOP, divOP, modOP};
        if (Arrays.asList(primerosOperadorBinario).contains(currentToken.tokenType)){
            Token operador = operadorBinario();
            NodeExpression ladoDerecho = expresionUnaria();
            return expresionRecursiva(new NodeBinaryExpression(operador, ladoIzquierdo, ladoDerecho));
        } else {
            TokenType[] siguientesExpresionRecursiva = {comma, semicolon, closeBr };
            if (Arrays.asList(siguientesExpresionRecursiva).contains(currentToken.tokenType)) { // Siguiente(...) = { , , ) , ; }
                return ladoIzquierdo; // Era solo unaria
            } else throw new SyntacticException("Expresión sin cerrar o mal formateada", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private Token operadorBinario() throws SyntacticException, LexicalException, SourceFileReaderException {
        //TokenType[] primerosOperadorBinario = {orOP, andOP, equals, notEquals, less, greater, lessOrEquals, greaterOrEquals, addOP, subOP, multOP, divOP, modOP};
        Token operadorBinario = currentToken;
        if (currentToken.tokenType == orOP) {
            match(orOP);
        } else if (currentToken.tokenType == andOP) {
            match(andOP);
        } else if (currentToken.tokenType == equals) {
            match(equals);
        } else if (currentToken.tokenType == notEquals) {
            match(notEquals);
        } else if (currentToken.tokenType == less) {
            match(less);
        } else if (currentToken.tokenType == greater) {
            match(greater);
        } else if (currentToken.tokenType == lessOrEquals){
            match(lessOrEquals);
        } else if (currentToken.tokenType == greaterOrEquals){
            match(greaterOrEquals);
        } else if (currentToken.tokenType == addOP){
            match(addOP);
        } else if (currentToken.tokenType == subOP){
            match(subOP);
        } else if (currentToken.tokenType == multOP){
            match(multOP);
        } else if (currentToken.tokenType == divOP) {
            match(divOP);
        } else if (currentToken.tokenType == modOP){
            match(modOP);
        } else throw new SyntacticException("Se esperaba operador", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        return operadorBinario;
    }
    private NodeExpression expresionUnaria() throws SyntacticException, LexicalException, SourceFileReaderException {
        TokenType[] primerosOperadorUn = {addOP, subOP, not};
        TokenType[] primerosOperando = {r_null, r_true, r_false, intLit, charLit, strLit, r_this, mvID, r_new, openBr, classID};
        if (Arrays.asList(primerosOperadorUn).contains(currentToken.tokenType)){
            return new NodeUnaryExpression(operadorUnario(), operando());
        } else if (Arrays.asList(primerosOperando).contains(currentToken.tokenType)) {
            return operando();
        } else throw new SyntacticException("Se esperaba expresión", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }
    private Token operadorUnario() throws SyntacticException, LexicalException, SourceFileReaderException {
        Token operadorUnario = currentToken;
        if (currentToken.tokenType == addOP){
            match(addOP);
        } else if (currentToken.tokenType == subOP){
            match(subOP);
        } else if (currentToken.tokenType == not){
            match(not);
        } else throw new SyntacticException("Se esperaba operador", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        return operadorUnario;
    }
    private NodeOperand operando() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_null || currentToken.tokenType == r_true || currentToken.tokenType == r_false || currentToken.tokenType == intLit || currentToken.tokenType == charLit || currentToken.tokenType == strLit ){
            return literal();
        } else if (currentToken.tokenType == r_this || currentToken.tokenType == mvID || currentToken.tokenType == r_new || currentToken.tokenType == openBr || currentToken.tokenType == classID){
            return acceso();
        } else throw new SyntacticException("Se esperaba operando", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private NodeLiteral literal() throws SyntacticException, LexicalException, SourceFileReaderException {
        Token literal = currentToken;
        if (currentToken.tokenType == r_null){
            match(r_null);
            return new NodeNull(literal);
        } else if (currentToken.tokenType == r_true){
            match(r_true);
            return new NodeBoolean(literal);
        } else if (currentToken.tokenType == r_false){
            match(r_false);
            return new NodeBoolean(literal);
        } else if (currentToken.tokenType == intLit) {
            match(intLit);
            return new NodeInt(literal);
        } else if (currentToken.tokenType == charLit) {
            match(charLit);
            return new NodeChar(literal);
        } else if (currentToken.tokenType == strLit) {
            match(strLit);
            return new NodeString(literal);
        } else throw new SyntacticException("Se esperaba literal", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private NodeAccess acceso() throws LexicalException, SourceFileReaderException, SyntacticException {
        NodeAccess acceso = primario();
        acceso.setChaining(encadenadoOpt());
        return acceso;
    }
    private NodeAccess primario() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_this) {
            return accesoThis();
        } else if (currentToken.tokenType == mvID){
            return accesoVarMet();
        } else if (currentToken.tokenType == r_new){
            return accesoConstructor();
        } else if (currentToken.tokenType == classID){
            return accesoMetodoEstatico();
        } else if (currentToken.tokenType == openBr){
            return expresionParentizada();
        } else throw new SyntacticException("Se esperaba un acceso", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private NodeAccess accesoVarMet() throws LexicalException, SourceFileReaderException, SyntacticException {
        Token identificador = currentToken;
        match(mvID);
        return accesoVarMetFactorizado(identificador);
    }
    private NodeAccess accesoVarMetFactorizado(Token identificador) throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == openBr) {
            NodeMethodCall llamada = new NodeMethodCall(identificador);
            llamada.setParameterList(argsActuales());
            return llamada;
        } else {
            TokenType[] siguientesAccesoVarMetFactorizado = { closeBr,comma, dot, assign, addAssign, subAssign, semicolon, orOP, andOP, equals, notEquals, less, greater, lessOrEquals, greaterOrEquals, addOP, subOP, multOP, divOP, modOP};
            if (Arrays.asList(siguientesAccesoVarMetFactorizado).contains(currentToken.tokenType)) { // Siguientes(...) = { . , ; , ) , , ,Primeros(<OperadorBinario>) , Primeros(<TipoAsignacion>) }
                return new NodeVariableAccess(identificador);
            } else throw new SyntacticException("Expresión/Sentencia mal formada", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }
    private NodeAccess accesoMetodoEstatico() throws LexicalException, SourceFileReaderException, SyntacticException {
        Token identificadorClase = currentToken;
        match(classID);
        match(dot);
        Token identificadorMetodo = currentToken;
        match(mvID);
        NodeStaticMethodCall llamadaMet = new NodeStaticMethodCall(identificadorClase, identificadorMetodo);
        llamadaMet.setParameterList(argsActuales());
        return llamadaMet;
    }
    private NodeAccess accesoThis() throws LexicalException, SourceFileReaderException, SyntacticException {
        Token tokenThis = currentToken;
        match(r_this);
        return new NodeThisAccess(tokenThis, symbolTable.currentClass.getName());
    }
    private NodeAccess accesoConstructor() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(r_new);
        Token identificadorClase = currentToken;
        match(classID);
        NodeConstructorCall constructor = new NodeConstructorCall(identificadorClase);
        constructor.setParameterList(argsActuales());
        return constructor;
    }
    private NodeAccess expresionParentizada() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(openBr);
        NodeExpression expresion = expresion();
        match(closeBr);
        return new NodeParenthesizedExp(expresion);
    }

    private List<NodeExpression> argsActuales() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(openBr);
        List<NodeExpression> listaArgsActuales = listaExpsOpt();
        match(closeBr);
        return listaArgsActuales;
    }
    private List<NodeExpression> listaExpsOpt() throws LexicalException, SourceFileReaderException, SyntacticException {
        TokenType[] primerosListaExps = {addOP, subOP, not, r_null, r_true, r_false, intLit, charLit, strLit, r_this, mvID, r_new, openBr, classID};
        if (Arrays.asList(primerosListaExps).contains(currentToken.tokenType)){
            return listaExps(new ArrayList<>());
        } else {
            if (currentToken.tokenType == closeBr) { // Siguientes(...) = { ) }
                return new ArrayList<>();
            } else throw new SyntacticException("Lista de argumentos sin cerrar, se esperaba )", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }
    private List<NodeExpression> listaExps(List<NodeExpression> lista) throws LexicalException, SourceFileReaderException, SyntacticException {
        lista.add(expresion());
        return listaExpsFactorizada(lista);
    }
    private List<NodeExpression> listaExpsFactorizada(List<NodeExpression> lista) throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == comma){
            match(comma);
            return listaExps(lista);
        } else {
            if (currentToken.tokenType == closeBr) { // Siguientes(...) = { ) }
                return lista;
            } else throw new SyntacticException("Lista de argumentos sin cerrar, se esperaba )", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private NodeChaining encadenadoOpt() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == dot) {
            return varOMetEncadenado();
        } else {
            TokenType[] siguientesEncadenadoOpt = { closeBr,comma, assign, addAssign, subAssign, semicolon, orOP, andOP, equals, notEquals, less, greater, lessOrEquals, greaterOrEquals, addOP, subOP, multOP, divOP, modOP};
            if (Arrays.asList(siguientesEncadenadoOpt).contains(currentToken.tokenType)) { // Siguientes(...) = { ; , ), , ,Primeros(<OperadorBinario>) , Primeros(<TipoAsignacion>) }
                return null;
            } else throw new SyntacticException("Encadenado mal formado", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }
    private NodeChaining varOMetEncadenado() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(dot);
        Token identificador = currentToken;
        match(mvID);
        NodeChaining encadenado = varOMetEncadenadoFactorizado(identificador);
        encadenado.setChaining(encadenadoOpt());
        return encadenado;
    }
    private NodeChaining varOMetEncadenadoFactorizado(Token identificador) throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == openBr) {
            NodeMethodChaining encadenadoMetodo = new NodeMethodChaining(identificador);
            encadenadoMetodo.setParameterList(argsActuales());
            return encadenadoMetodo;
        } else {
            TokenType[] siguientesVarOMetEncFact = {dot, semicolon, closeBr, comma, orOP, andOP, equals, notEquals, less, greater, lessOrEquals, greaterOrEquals, addOP, subOP, multOP, divOP, modOP, assign,addAssign, subAssign};
            if (Arrays.asList(siguientesVarOMetEncFact).contains(currentToken.tokenType)) { // Siguientes(...) = { . , ; , ), , ,Primeros(<OperadorBinario>) , Primeros(<TipoAsignacion>) }
                return new NodeVarChaining(identificador);
            } else throw new SyntacticException("Encadenado mal formado", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }
}
