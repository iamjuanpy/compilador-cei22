package minijavaCompiler.syntax;

import minijavaCompiler.file_manager.SourceFileReaderException;
import minijavaCompiler.lexical.LexicalAnalyser;
import minijavaCompiler.lexical.Token;
import minijavaCompiler.lexical.TokenType;
import minijavaCompiler.lexical.exceptions.LexicalException;
import minijavaCompiler.semantics.SemanticException;
import minijavaCompiler.semantics.entries.classes.ConcreteClass;
import minijavaCompiler.semantics.entries.classes.Interface;
import minijavaCompiler.semantics.entries.Attribute;
import minijavaCompiler.semantics.entries.Constructor;
import minijavaCompiler.semantics.entries.Method;
import minijavaCompiler.semantics.entries.Parameter;
import minijavaCompiler.semantics.entries.types.PrimitiveType;
import minijavaCompiler.semantics.entries.types.ReferenceType;
import minijavaCompiler.semantics.entries.types.Type;
import minijavaCompiler.semantics.entries.types.primitives.BoolType;
import minijavaCompiler.semantics.entries.types.primitives.CharType;
import minijavaCompiler.semantics.entries.types.primitives.IntType;
import minijavaCompiler.syntax.exceptions.SyntacticException;

import java.util.Arrays;

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
        if (currentToken.tokenType == TokenType.r_class || currentToken.tokenType == TokenType.r_interface) {
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
            bloque();
        } else throw new SyntacticException("Se esperaba declaración de atributo o método o constructor", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void constructorOAttrOMetodo(Type tipo, Token tokenIdentificador) throws LexicalException, SourceFileReaderException, SyntacticException, SemanticException {
        if (currentToken.tokenType == openBr){
            symbolTable.currentUnit = new Constructor(tokenIdentificador);
            argsFormales();
            bloque();
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
            argsFormales();
            bloque();
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
        argsFormales();
        symbolTable.currentClass.addMethod((Method) symbolTable.currentUnit);
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
            return new PrimitiveType("void");
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

    private void bloque() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(openCurly);
        listaSentencias();
        match(closeCurly);
    }

    private void listaSentencias() throws LexicalException, SourceFileReaderException, SyntacticException {
        TokenType[] primerosSentencia = {semicolon, mvID, r_return, r_var, r_if, r_while, openCurly, classID, openBr, r_this, r_new};
        if (Arrays.asList(primerosSentencia).contains(currentToken.tokenType)) {
            sentencia();
            listaSentencias();
        } else {
            if (currentToken.tokenType == closeCurly) { // Siguientes(...) = { } }
                //Nada
            } else throw new SyntacticException("Se esperaba una sentencia o fin de bloque", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void sentencia() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == semicolon){
            match(semicolon);
        } else if (currentToken.tokenType == mvID || currentToken.tokenType == classID || currentToken.tokenType == openBr || currentToken.tokenType == r_this || currentToken.tokenType == r_new){
            asignacionOLlamada();
            match(semicolon);
        } else if (currentToken.tokenType == r_var){
            varLocal();
            match(semicolon);
        } else if (currentToken.tokenType == r_return){
            nt_return();
            match(semicolon);
        } else if (currentToken.tokenType == r_if){
            nt_if();
        } else if (currentToken.tokenType == r_while){
            nt_while();
        } else if (currentToken.tokenType == openCurly){
            bloque();
        } else throw new SyntacticException("Se esperaba una sentencia", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }
    private void asignacionOLlamada() throws LexicalException, SourceFileReaderException, SyntacticException {
        acceso();
        asignacionOLlamadaFactorizada();
    }
    private void asignacionOLlamadaFactorizada() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == assign || currentToken.tokenType == addAssign || currentToken.tokenType == subAssign) {
            tipoDeAsignacion();
            expresion();
        } else {
            if (currentToken.tokenType == semicolon) { // Siguientes(...) = { ; }
                // Nada
            } else throw new SyntacticException("Sentencia sin cerrar, se esperaba ;", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void tipoDeAsignacion() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == assign){
            match(assign);
        } else if (currentToken.tokenType == addAssign) {
            match(addAssign);
        } else if (currentToken.tokenType == subAssign) {
            match(subAssign);
        } else throw new SyntacticException("Se esperaba un operador de asignación", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void varLocal() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(r_var);
        match(mvID);
        match(assign);
        expresion();
    }

    private void nt_return() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(r_return);
        expresionOpt();
    }

    private void expresionOpt() throws LexicalException, SourceFileReaderException, SyntacticException {
        TokenType[] primerosExpresion = {addOP, subOP, not, r_null, r_true, r_false, intLit, charLit, strLit, r_this, mvID, r_new, openBr, classID};
        if (Arrays.asList(primerosExpresion).contains(currentToken.tokenType)) {
            expresion();
        } else {
            if (currentToken.tokenType == semicolon) { // Siguientes(...) = { ; }
                // nada
            } else throw new SyntacticException("Return sin cerrar, se esperaba ;", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void nt_if() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(r_if);
        match(openBr);
        expresion();
        match(closeBr);
        sentencia();
        nt_else();
    }
    private void nt_else() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_else){
            match(r_else);
            sentencia();
        } else {
            TokenType[] siguientesElseFactorizado = {semicolon, mvID, r_return, r_var, r_if, r_while, openCurly, classID, openBr, r_this, r_new, closeCurly};
            if (Arrays.asList(siguientesElseFactorizado).contains(currentToken.tokenType)){ // Siguiente(...) = { Primeros(<Sentencia>), } }
                // Nada por ahora
            } else throw new SyntacticException("Se esperaba una sentencia o fin de bloque", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void nt_while() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(r_while);
        match(openBr);
        expresion();
        match(closeBr);
        sentencia();
    }

    private void expresion() throws LexicalException, SourceFileReaderException, SyntacticException {
        expresionUnaria();
        expresionRecursiva();
    }
    private void expresionRecursiva() throws LexicalException, SourceFileReaderException, SyntacticException {
        TokenType[] primerosOperadorBinario = {orOP, andOP, equals, notEquals, less, greater, lessOrEquals, greaterOrEquals, addOP, subOP, multOP, divOP, modOP};
        if (Arrays.asList(primerosOperadorBinario).contains(currentToken.tokenType)){
            operadorBinario();
            expresionUnaria();
            expresionRecursiva();
        } else {
            TokenType[] siguientesExpresionRecursiva = {comma, semicolon, closeBr };
            if (Arrays.asList(siguientesExpresionRecursiva).contains(currentToken.tokenType)) { // Siguiente(...) = { , , ) , ; }
                // nada
            } else throw new SyntacticException("Expresión sin cerrar o mal formateada", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void operadorBinario() throws SyntacticException, LexicalException, SourceFileReaderException {
        //TokenType[] primerosOperadorBinario = {orOP, andOP, equals, notEquals, less, greater, lessOrEquals, greaterOrEquals, addOP, subOP, multOP, divOP, modOP};
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
    }
    private void expresionUnaria() throws SyntacticException, LexicalException, SourceFileReaderException {
        TokenType[] primerosOperadorUn = {addOP, subOP, not};
        TokenType[] primerosOperando = {r_null, r_true, r_false, intLit, charLit, strLit, r_this, mvID, r_new, openBr, classID};
        if (Arrays.asList(primerosOperadorUn).contains(currentToken.tokenType)){
            operadorUnario();
            operando();
        } else if (Arrays.asList(primerosOperando).contains(currentToken.tokenType)) {
            operando();
        } else throw new SyntacticException("Se esperaba expresión", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }
    private void operadorUnario() throws SyntacticException, LexicalException, SourceFileReaderException {
        if (currentToken.tokenType == addOP){
            match(addOP);
        } else if (currentToken.tokenType == subOP){
            match(subOP);
        } else if (currentToken.tokenType == not){
            match(not);
        } else throw new SyntacticException("Se esperaba operador", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }
    private void operando() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_null || currentToken.tokenType == r_true || currentToken.tokenType == r_false || currentToken.tokenType == intLit || currentToken.tokenType == charLit || currentToken.tokenType == strLit ){
            literal();
        } else if (currentToken.tokenType == r_this || currentToken.tokenType == mvID || currentToken.tokenType == r_new || currentToken.tokenType == openBr || currentToken.tokenType == classID){
            acceso();
        } else throw new SyntacticException("Se esperaba operando", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void literal() throws SyntacticException, LexicalException, SourceFileReaderException {
        if (currentToken.tokenType == r_null){
            match(r_null);
        } else if (currentToken.tokenType == r_true){
            match(r_true);
        } else if (currentToken.tokenType == r_false){
            match(r_false);
        } else if (currentToken.tokenType == intLit) {
            match(intLit);
        } else if (currentToken.tokenType == charLit) {
            match(charLit);
        } else if (currentToken.tokenType == strLit) {
            match(strLit);
        } else throw new SyntacticException("Se esperaba literal", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void acceso() throws LexicalException, SourceFileReaderException, SyntacticException {
        primario();
        encadenadoOpt();
    }
    private void primario() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_this) {
            accesoThis();
        } else if (currentToken.tokenType == mvID){
            accesoVarMet();
        } else if (currentToken.tokenType == r_new){
            accesoConstructor();
        } else if (currentToken.tokenType == classID){
            accesoMetodoEstatico();
        } else if (currentToken.tokenType == openBr){
            expresionParentizada();
        } else throw new SyntacticException("Se esperaba un acceso", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void accesoVarMet() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(mvID);
        accesoVarMetFactorizado();
    }
    private void accesoVarMetFactorizado() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == openBr) {
            argsActuales();
        } else {
            TokenType[] siguientesAccesoVarMetFactorizado = { closeBr,comma, dot, assign, addAssign, subAssign, semicolon, orOP, andOP, equals, notEquals, less, greater, lessOrEquals, greaterOrEquals, addOP, subOP, multOP, divOP, modOP};
            if (Arrays.asList(siguientesAccesoVarMetFactorizado).contains(currentToken.tokenType)) { // Siguientes(...) = { . , ; , ) , , ,Primeros(<OperadorBinario>) , Primeros(<TipoAsignacion>) }
                // nada
            } else throw new SyntacticException("Expresión/Sentencia mal formada", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }
    private void accesoMetodoEstatico() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(classID);
        match(dot);
        match(mvID);
        argsActuales();
    }
    private void accesoThis() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(r_this);
    }
    private void accesoConstructor() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(r_new);
        match(classID);
        argsActuales();
    }
    private void expresionParentizada() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(openBr);
        expresion();
        match(closeBr);
    }

    private void argsActuales() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(openBr);
        listaExpsOpt();
        match(closeBr);
    }
    private void listaExpsOpt() throws LexicalException, SourceFileReaderException, SyntacticException {
        TokenType[] primerosListaExps = {addOP, subOP, not, r_null, r_true, r_false, intLit, charLit, strLit, r_this, mvID, r_new, openBr, classID};
        if (Arrays.asList(primerosListaExps).contains(currentToken.tokenType)){
            listaExps();
        } else {
            if (currentToken.tokenType == closeBr) { // Siguientes(...) = { ) }
                // nada
            } else throw new SyntacticException("Lista de argumentos sin cerrar, se esperaba )", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }
    private void listaExps() throws LexicalException, SourceFileReaderException, SyntacticException {
        expresion();
        listaExpsFactorizada();
    }
    private void listaExpsFactorizada() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == comma){
            match(comma);
            listaExps();
        } else {
            if (currentToken.tokenType == closeBr) { // Siguientes(...) = { ) }
                // nada
            } else throw new SyntacticException("Lista de argumentos sin cerrar, se esperaba )", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void encadenadoOpt() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == dot) {
            varOMetEncadenado();
        } else {
            TokenType[] siguientesEncadenadoOpt = { closeBr,comma, assign, addAssign, subAssign, semicolon, orOP, andOP, equals, notEquals, less, greater, lessOrEquals, greaterOrEquals, addOP, subOP, multOP, divOP, modOP};
            if (Arrays.asList(siguientesEncadenadoOpt).contains(currentToken.tokenType)) { // Siguientes(...) = { ; , ), , ,Primeros(<OperadorBinario>) , Primeros(<TipoAsignacion>) }
                // nada
            } else throw new SyntacticException("Encadenado mal formado", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }
    private void varOMetEncadenado() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(dot);
        match(mvID);
        varOMetEncadenadoFactorizado();
        encadenadoOpt();
    }
    private void varOMetEncadenadoFactorizado() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == openBr) {
            argsActuales();
        } else {
            TokenType[] siguientesVarOMetEncFact = {dot, semicolon, closeBr, comma, orOP, andOP, equals, notEquals, less, greater, lessOrEquals, greaterOrEquals, addOP, subOP, multOP, divOP, modOP, assign,addAssign, subAssign};
            if (Arrays.asList(siguientesVarOMetEncFact).contains(currentToken.tokenType)) { // Siguientes(...) = { . , ; , ), , ,Primeros(<OperadorBinario>) , Primeros(<TipoAsignacion>) }
                //nada
            } else throw new SyntacticException("Encadenado mal formado", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }
}
