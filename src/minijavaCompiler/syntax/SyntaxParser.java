package minijavaCompiler.syntax;

import minijavaCompiler.file_manager.SourceFileReaderException;
import minijavaCompiler.lexical.LexicalAnalyser;
import minijavaCompiler.lexical.Token;
import minijavaCompiler.lexical.TokenType;
import minijavaCompiler.lexical.exceptions.LexicalException;
import minijavaCompiler.syntax.exceptions.SyntacticException;

import java.util.Arrays;

import static minijavaCompiler.lexical.TokenType.*;

public class SyntaxParser {

    LexicalAnalyser lexicalAnalyser;
    Token currentToken;

    public SyntaxParser(LexicalAnalyser lexicalAnalyser) throws LexicalException, SourceFileReaderException, SyntacticException {
        this.lexicalAnalyser = lexicalAnalyser;
        currentToken = lexicalAnalyser.getNextToken();
        inicial();
    }

    private void match(TokenType expectedTokenType) throws LexicalException, SourceFileReaderException, SyntacticException {
        if (expectedTokenType == currentToken.tokenType){
            currentToken = lexicalAnalyser.getNextToken();
        } else {
            throw new SyntacticException(expectedTokenType,currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
        }
    }

    private void inicial() throws LexicalException, SourceFileReaderException, SyntacticException {
        listadeclases();
        match(eof);
    }

    private void listadeclases() throws SyntacticException, LexicalException, SourceFileReaderException {
        if (currentToken.tokenType == TokenType.r_class || currentToken.tokenType == TokenType.r_interface) { // Primeros(<Clase>) = {class, interface}
            clase();
            listadeclases();
        } else {
            //Siguiente(ListaClases) = EOF
            if (currentToken.tokenType != eof) {
                throw new SyntacticException("Se esperaba el fin del archivo", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
        }
    }

    private void clase() throws SyntacticException, LexicalException, SourceFileReaderException {
        if (currentToken.tokenType == r_class) { // Primeros(<ClaseConcreta>)
            claseConcreta();
        } else if (currentToken.tokenType == r_interface) { // Primeros(<Interface>)
            nt_interface();
        } else throw new SyntacticException("Se esperaba declaración de clase o interface", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void claseConcreta() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(r_class);
        match(classID);
        heredaDe();
        implementaA();
        match(openCurly);
        listaMiembros();
        match(closeCurly);
    }

    private void nt_interface() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(r_interface);
        match(classID);
        extiendeA();
        match(openCurly);
        listaEncabezados();
        match(closeCurly);
    }

    private void heredaDe() throws LexicalException, SourceFileReaderException, SyntacticException {
        if(currentToken.tokenType == r_extends){
            match(r_extends);
            match(classID);
        } else {
            // Siguientes(HeredaDe) = Primeros(ImplementaA) U { { }
            if (currentToken.tokenType != openCurly && currentToken.tokenType != r_implements) {
                throw new SyntacticException("Se esperaba una lista de atributos/métodos", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
        }
    }

    private void implementaA() throws LexicalException, SourceFileReaderException, SyntacticException {
        if(currentToken.tokenType == r_implements){
            match(r_implements);
            listaTipoReferencia();
        } else {
            // Siguientes(implementaA) = { { }
            if (currentToken.tokenType != openCurly) {
                throw new SyntacticException("Se esperaba una lista de atributos/métodos", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
        }
    }

    private void extiendeA() throws LexicalException, SourceFileReaderException, SyntacticException {
        if(currentToken.tokenType == r_extends){
            match(r_extends);
            listaTipoReferencia();
        } else {
            // Siguientes(extiendeA) = { { }
            if (currentToken.tokenType != openCurly) {
                throw new SyntacticException("Se esperaba una lista de encabezados de métodos", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
        }
    }

    private void listaTipoReferencia() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(classID);
        listaTipoReferenciaFactorizada();
    }

    private void listaTipoReferenciaFactorizada() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == comma) {
            match(comma);
            listaTipoReferencia();
        } else {
            //Siguiente(listaTipoReferenciaFactorizada) = { { }
            if (currentToken.tokenType != openCurly) {
                throw new SyntacticException("Se esperaba una lista de métodos", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
        }
    }

    private void listaMiembros() throws LexicalException, SourceFileReaderException, SyntacticException {
        TokenType[] primerosMiembro = {r_public, r_private, r_static, r_boolean, r_int, r_char, classID, r_void};
        if (Arrays.asList(primerosMiembro).contains(currentToken.tokenType)) {
            miembro();
            listaMiembros();
        } else {
            //Siguiente(ListaMiembros) = { } }
            if (currentToken.tokenType != closeCurly) {
                throw new SyntacticException("Clase sin cerrar", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
        }
    }

    private void listaEncabezados() throws LexicalException, SourceFileReaderException, SyntacticException {
        TokenType[] primerosEncabezadoMet = {r_static, r_boolean, r_int, r_char, classID, r_void};
        if (Arrays.asList(primerosEncabezadoMet).contains(currentToken.tokenType)) { // Primeros(<EncabezadoMetodo>)
            encabezadoMetodo();
            match(semicolon);
            listaEncabezados();
        } else {
            // Siguiente(ListaEncabezados) = { } }
            if (currentToken.tokenType != closeCurly) {
                throw new SyntacticException("Interface sin cerrar", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
        }
    }

    private void miembro() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_public || currentToken.tokenType == r_private) {
            atributo();
        } else if ( currentToken.tokenType == r_static || currentToken.tokenType == r_void || currentToken.tokenType == r_boolean || currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == classID){
            atributoOMetodo();
        } else throw new SyntacticException("Se esperaba declaración de atributo o método", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void atributo() throws SyntacticException, LexicalException, SourceFileReaderException {
        visibilidad();
        tipo();
        listaDecAtrs();
        match(semicolon);
    }

    private void atributoOMetodo() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_boolean || currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == classID) {
            tipo();
            match(mvID);
            atributoOmisionOMetodo();
        }else if (currentToken.tokenType == r_void || currentToken.tokenType == r_static) {
            encabezadoMetodoStaticOVoid();
            bloque();
        } else throw new SyntacticException("Se esperaba declaración de atributo o método", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void atributoOmisionOMetodo() throws LexicalException, SourceFileReaderException, SyntacticException{
        if (currentToken.tokenType == comma || currentToken.tokenType == semicolon) {
            listaDecAtrsFactorizada();
            match(semicolon);
        } else if (currentToken.tokenType == openBr) {
            argsFormales();
            bloque();
        } else throw new SyntacticException("Se esperaba declaración de atributo o método", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void encabezadoMetodoStaticOVoid() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_static) {
            match(r_static);
            tipoMetodo();
            match(mvID);
            argsFormales();
        } else if (currentToken.tokenType == r_void) {
            match(r_void);
            match(mvID);
            argsFormales();
        } else throw new SyntacticException("Se esperaba declaración de método", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void encabezadoMetodo() throws LexicalException, SourceFileReaderException, SyntacticException {
        TokenType[] primerosEncabezadoMet = {r_static, r_boolean, r_int, r_char, classID, r_void};
        estaticoOpt();
        tipoMetodo();
        match(mvID);
        argsFormales();
    }

    private void visibilidad() throws SyntacticException, LexicalException, SourceFileReaderException {
        if (currentToken.tokenType == r_public){
            match(r_public);
        } else if (currentToken.tokenType == r_private) {
            match(r_private);
        } else throw new SyntacticException("Se esperaba identificador de visibilidad", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }
    private void tipo() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == r_boolean) {
            tipoPrimitivo();
        } else if (currentToken.tokenType == classID) {
            match(classID);
        } else throw new SyntacticException("Se esperaba identificador de tipo", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }
    private void tipoPrimitivo() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_int){
            match(r_int);
        } else if (currentToken.tokenType == r_char){
            match(r_char);
        } else if (currentToken.tokenType == r_boolean) {
            match(r_boolean);
        }else throw new SyntacticException("Se esperaba identificador de tipo", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void listaDecAtrs() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(mvID);
        listaDecAtrsFactorizada();
    }
    private void listaDecAtrsFactorizada() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == comma) {
            match(comma);
            listaDecAtrs();
        } else {
            if (currentToken.tokenType != semicolon) { //Siguiente(listaDecAtrsFactorizada) = { ; }
                throw new SyntacticException("Declaración de atributo sin cerrar", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
        }
    }

    private void estaticoOpt() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_static) {
            match(r_static);
        } else {
            //Siguiente(EstaticoOpt) = Primeros(TipoMetodo)
            if (currentToken.tokenType != r_int && currentToken.tokenType != r_char && currentToken.tokenType != r_boolean && currentToken.tokenType != classID && currentToken.tokenType != r_void) {
                throw new SyntacticException("Se esperaba el tipo del método", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
        }
    }
    private void tipoMetodo() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == r_boolean || currentToken.tokenType == classID) {
            tipo();
        } else if (currentToken.tokenType == r_void){
            match(r_void);
        } else throw new SyntacticException("Se esperaba identificador de tipo", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void argsFormales() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(openBr);
        listaArgsFormalesOpt();
        match(closeBr);
    }
    private void listaArgsFormalesOpt() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == r_boolean || currentToken.tokenType == classID){
            listaArgsFormales();
        } else {
            if (currentToken.tokenType != closeBr) { //Siguiente(listaArgsFormalesOpt) = { ) }
                throw new SyntacticException("Lista de argumentos sin cerrar", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
        }
    }
    private void listaArgsFormales() throws LexicalException, SourceFileReaderException, SyntacticException {
        argFormal();
        listaArgsFormalesFactorizada();
    }
    private void listaArgsFormalesFactorizada() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == comma) {
            match(comma);
            listaArgsFormales();
        } else {
            if (currentToken.tokenType != closeBr) { //Siguiente(listaArgsFormalesOpt) = { ) }
                throw new SyntacticException("Lista de argumentos sin cerrar", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
        }
    }

    private void argFormal() throws LexicalException, SourceFileReaderException, SyntacticException {
        tipo();
        match(mvID);
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
            if (currentToken.tokenType != closeCurly) { //Siguientes(ListaSentencias) = { } }
                throw new SyntacticException("Bloque sin cerrar", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
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
        } else throw new SyntacticException("Se esperaba sentencia", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
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
            if (currentToken.tokenType != semicolon) { // Siguientes(asignacionOLlamadaFactorizada) = { ; }
                throw new SyntacticException("Sentencia sin cerrar", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
        }
    }

    private void tipoDeAsignacion() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == assign){
            match(assign);
        } else if (currentToken.tokenType == addAssign) {
            match(addAssign);
        } else if (currentToken.tokenType == subAssign) {
            match(subAssign);
        } else throw new SyntacticException("Se esperaba asignación", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
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
            if (currentToken.tokenType != semicolon) { // Siguientes(expresionOpt) = { ; }
                throw new SyntacticException("Return sin cerrar", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
        }
    }

    private void nt_if() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(r_if);
        match(openBr);
        expresion();
        match(closeBr);
        sentencia();
        nt_ifFactorizado();
    }
    private void nt_ifFactorizado() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_else){
            match(r_else);
            sentencia();
        } else {
            //Siguiente(nt_ifFactorizado) = { Primeros(Sentencia) u } }
            if (currentToken.tokenType != semicolon && currentToken.tokenType != mvID && currentToken.tokenType != r_return && currentToken.tokenType != r_var && currentToken.tokenType != r_if && currentToken.tokenType != r_while && currentToken.tokenType != openCurly && currentToken.tokenType != classID && currentToken.tokenType != openBr && currentToken.tokenType != r_this && currentToken.tokenType != r_new && currentToken.tokenType != closeCurly){
                throw new SyntacticException("Sentencia mal cerrada", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
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
            //Siguiente(ExpresionRecursiva) = { , u ) u ; u Primeros(OperadorBinario)}
            if (currentToken.tokenType != comma && currentToken.tokenType != closeBr && currentToken.tokenType != semicolon && currentToken.tokenType != orOP && currentToken.tokenType != andOP && currentToken.tokenType != equals && currentToken.tokenType != notEquals && currentToken.tokenType != less && currentToken.tokenType != greater && currentToken.tokenType != lessOrEquals && currentToken.tokenType != greaterOrEquals && currentToken.tokenType != addOP && currentToken.tokenType != subOP && currentToken.tokenType != multOP && currentToken.tokenType != divOP && currentToken.tokenType != modOP) {
                throw new SyntacticException("Expresión mal formada", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
        }
    }

    private void operadorBinario() throws SyntacticException, LexicalException, SourceFileReaderException {
        TokenType[] primerosOperadorBinario = {orOP, andOP, equals, notEquals, less, greater, lessOrEquals, greaterOrEquals, addOP, subOP, multOP, divOP, modOP};
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
        } else throw new SyntacticException("Se esperaba operando", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
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
        } else throw new SyntacticException("Se esperaba operando", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
    }

    private void accesoVarMet() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(mvID);
        accesoVarMetFactorizado();
    }
    private void accesoVarMetFactorizado() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == openBr) {
            argsActuales();
        } else {
            //Siguientes(accesoVarMetFactorizado) = { ( u ) u , u . u Primeros(OperadorBinario) u ; u Primeros(TipoAsignacion)}
            if (currentToken.tokenType != openBr && currentToken.tokenType != closeBr && currentToken.tokenType != comma && currentToken.tokenType != dot && currentToken.tokenType != semicolon && currentToken.tokenType != orOP && currentToken.tokenType != andOP && currentToken.tokenType != equals && currentToken.tokenType != notEquals && currentToken.tokenType != less && currentToken.tokenType != greater && currentToken.tokenType != lessOrEquals && currentToken.tokenType != greaterOrEquals && currentToken.tokenType != addOP && currentToken.tokenType != subOP && currentToken.tokenType != multOP && currentToken.tokenType != divOP && currentToken.tokenType != modOP && currentToken.tokenType != assign && currentToken.tokenType != addAssign && currentToken.tokenType != subAssign ) {
                throw new SyntacticException("Expresión mal formada", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
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
        match(openBr);
        match(closeBr);
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
            //Siguiente(listaExpsOpt) = { ) }
            if (currentToken.tokenType != closeBr){
                throw new SyntacticException("Lista de argumentos sin cerrar", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
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
            // Siguiente(listaExpsFactorizada) = { ) }
            if (currentToken.tokenType != closeBr){
                throw new SyntacticException("Lista de argumentos sin cerrar", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
        }
    }

    private void encadenadoOpt() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == dot) {
            varOMetEncadenado();
        } else {
            //Siguientes(encadenadoOpt) = { Primeros(TipoAsignacion) u ; u Primeros(OperadorBinario) u ) }
            if (currentToken.tokenType != comma && currentToken.tokenType != closeBr && currentToken.tokenType != assign && currentToken.tokenType != addAssign && currentToken.tokenType != subAssign && currentToken.tokenType != semicolon && currentToken.tokenType != orOP && currentToken.tokenType != andOP && currentToken.tokenType != equals && currentToken.tokenType != notEquals && currentToken.tokenType != less && currentToken.tokenType != greater && currentToken.tokenType != lessOrEquals && currentToken.tokenType != greaterOrEquals && currentToken.tokenType != addOP && currentToken.tokenType != subOP && currentToken.tokenType != multOP && currentToken.tokenType != divOP && currentToken.tokenType != modOP) {
                throw new SyntacticException("Expresión mal formada", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
            }
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
            encadenadoOpt();
        } else {
            //Siguientes(varOMetEncadenadoFactorizado) = { . u ; u operadorBinario}
//            if (currentToken.tokenType != dot && currentToken.tokenType != semicolon && currentToken.tokenType != orOP && currentToken.tokenType != andOP && currentToken.tokenType != equals && currentToken.tokenType != notEquals && currentToken.tokenType != less && currentToken.tokenType != greater && currentToken.tokenType != lessOrEquals && currentToken.tokenType != greaterOrEquals && currentToken.tokenType != addOP && currentToken.tokenType != subOP && currentToken.tokenType != multOP && currentToken.tokenType != divOP && currentToken.tokenType != modOP) {
//                throw new SyntacticException("Expresión mal formada", currentToken.tokenType, currentToken.lexeme, currentToken.lineNumber);
//            }
        }
    }
}
