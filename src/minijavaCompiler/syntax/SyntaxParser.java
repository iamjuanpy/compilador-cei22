package minijavaCompiler.syntax;

import minijavaCompiler.file_manager.SourceFileReaderException;
import minijavaCompiler.lexical.LexicalAnalyser;
import minijavaCompiler.lexical.Token;
import minijavaCompiler.lexical.TokenType;
import minijavaCompiler.lexical.exceptions.LexicalException;

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

    private void match(TokenType tokenName) throws LexicalException, SourceFileReaderException, SyntacticException {
        if (tokenName == currentToken.tokenType){
            currentToken = lexicalAnalyser.getNextToken();
        } else {
            throw new SyntacticException();
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
        } else { // Epsilon

        }
    }

    private void clase() throws SyntacticException, LexicalException, SourceFileReaderException {
        if (currentToken.tokenType == r_class) { // Primeros(<ClaseConcreta>)
            claseConcreta();
        } else if (currentToken.tokenType == r_interface) { // Primeros(<Interface>)
            nt_interface();
        } else throw new SyntacticException();
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

        }
    }

    private void implementaA() throws LexicalException, SourceFileReaderException, SyntacticException {
        if(currentToken.tokenType == r_implements){
            match(r_implements);
            listaTipoReferencia();
        } else {

        }
    }

    private void extiendeA() throws LexicalException, SourceFileReaderException, SyntacticException {
        if(currentToken.tokenType == r_extends){
            match(r_extends);
            listaTipoReferencia();
        } else {

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

        }
    }

    private void listaMiembros() throws LexicalException, SourceFileReaderException, SyntacticException {
        TokenType[] primerosMiembro = {r_public, r_private, r_static, r_boolean, r_int, r_char, classID};
        if (Arrays.asList(primerosMiembro).contains(currentToken.tokenType)) {
            miembro();
            listaMiembros();
        } else {

        }
    }

    private void listaEncabezados() throws LexicalException, SourceFileReaderException, SyntacticException {
        TokenType[] primerosEncabezadoMet = {r_static, r_boolean, r_int, r_char, classID};
        if (Arrays.asList(primerosEncabezadoMet).contains(currentToken.tokenType)) { // Primeros(<EncabezadoMetodo>)
            encabezadoMetodo();
            match(semicolon);
            listaEncabezados();
        } else {

        }
    }

    private void miembro() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_public || currentToken.tokenType == r_private) {
            atributo();
        } else if ( currentToken.tokenType == r_static || currentToken.tokenType == r_boolean || currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == classID){
            metodo();
        } else throw new SyntacticException();
    }

    private void atributo() throws SyntacticException, LexicalException, SourceFileReaderException {
        visibilidad();
        tipo();
        listaDecAtrs();
        match(semicolon);
    }

    private void metodo() throws LexicalException, SourceFileReaderException, SyntacticException {
        encabezadoMetodo();
        bloque();
    }

    private void encabezadoMetodo() throws LexicalException, SourceFileReaderException, SyntacticException {
        TokenType[] primerosEncabezadoMet = {r_static, r_boolean, r_int, r_char, classID};
        if (Arrays.asList(primerosEncabezadoMet).contains(currentToken.tokenType)) { // Primeros(<EncabezadoMetodo>)
            estaticoOpt();
            tipoMetodo();
            match(mvID);
            argsFormales();
        } else throw new SyntacticException();
    }

    private void visibilidad() throws SyntacticException, LexicalException, SourceFileReaderException {
        if (currentToken.tokenType == r_public){
            match(r_public);
        } else if (currentToken.tokenType == r_private) {
            match(r_private);
        } else throw new SyntacticException();
    }
    private void tipo() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == r_boolean) {
            tipoPrimitivo();
        } else if (currentToken.tokenType == classID) {
            match(classID);
        } else throw new SyntacticException();
    }
    private void tipoPrimitivo() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == r_boolean){
            match(currentToken.tokenType);
        } else throw new SyntacticException();
    }

    private void listaDecAtrs() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(mvID);
        listaDecAtrsFactorizada();
    }
    private void listaDecAtrsFactorizada() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == comma) {
            match(comma);
            listaDecAtrs();
        } else {}
    }

    private void estaticoOpt() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_static) {
            match(r_static);
        }
    }
    private void tipoMetodo() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == r_boolean || currentToken.tokenType == classID) {
            tipo();
        } else if (currentToken.tokenType == r_void){
            match(r_void);
        } else throw new SyntacticException();
    }

    private void argsFormales() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(openBr);
        listaArgsFormalesOpt();
        match(closeBr);
    }
    private void listaArgsFormalesOpt() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_int || currentToken.tokenType == r_char || currentToken.tokenType == r_boolean || currentToken.tokenType == classID){
            listaArgsFormales();
        } else {}
    }
    private void listaArgsFormales() throws LexicalException, SourceFileReaderException, SyntacticException {
        argFormal();
        listaArgsFormalesFactorizada();
    }
    private void listaArgsFormalesFactorizada() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == comma) {
            match(comma);
            listaArgsFormales();
        } else {}
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
        } else {}
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
        }
    }
    private void asignacionOLlamada() throws LexicalException, SourceFileReaderException, SyntacticException {
        acceso();
        asignacionOLlamadaFactorizada();
    }
    private void asignacionOLlamadaFactorizada() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == assign || currentToken.tokenType == addAssign || currentToken.tokenType == subAssign) {
            tipoDeAsignacion();
            expresion();
        } else {}
    }

    private void tipoDeAsignacion() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == assign){
            match(assign);
        } else if (currentToken.tokenType == addAssign) {
            match(addAssign);
        } else if (currentToken.tokenType == subAssign) {
            match(subAssign);
        } else throw new SyntacticException();
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
        } else {}
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
        } else {}
    }

    private void operadorBinario() throws SyntacticException, LexicalException, SourceFileReaderException {
        TokenType[] primerosOperadorBinario = {orOP, andOP, equals, notEquals, less, greater, lessOrEquals, greaterOrEquals, addOP, subOP, multOP, divOP, modOP};
        if (Arrays.asList(primerosOperadorBinario).contains(currentToken.tokenType)) {
            match(currentToken.tokenType);
        } else throw new SyntacticException();
//        if (currentToken.tokenType == orOP) {
//            match(orOP);
//        } else if (currentToken.tokenType == andOP) {
//
//        } else if (currentToken.tokenType == equals) {
//
//        } else if (currentToken.tokenType == notEquals) {
//
//        } else if (currentToken.tokenType == less) {
//
//        } else if (currentToken.tokenType == greater) {
//
//        } else if (currentToken.tokenType == lessOrEquals){
//
//        } else if (currentToken.tokenType == greaterOrEquals){
//
//        } else if (currentToken.tokenType == addOP){
//
//        } else if (currentToken.tokenType == subOP){
//
//        } else if (currentToken.tokenType == multOP){
//
//        } else if (currentToken.tokenType == divOP) {
//
//        } else if (currentToken.tokenType == modOP){
//
//        } else throw new SyntacticException();
    }
    private void expresionUnaria() throws SyntacticException, LexicalException, SourceFileReaderException {
        TokenType[] primerosOperadorUn = {addOP, subOP, not};
        TokenType[] primerosOperando = {r_null, r_true, r_false, intLit, charLit, strLit, r_this, mvID, r_new, openBr, classID};
        if (Arrays.asList(primerosOperadorUn).contains(currentToken.tokenType)){
            operadorUnario();
            operando();
        } else if (Arrays.asList(primerosOperando).contains(currentToken.tokenType)) {
            operando();
        } else throw new SyntacticException();
    }
    private void operadorUnario() throws SyntacticException, LexicalException, SourceFileReaderException {
        if (currentToken.tokenType == addOP){
            match(addOP);
        } else if (currentToken.tokenType == subOP){
            match(subOP);
        } else if (currentToken.tokenType == not){
            match(not);
        } else throw new SyntacticException();
    }
    private void operando() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == r_null || currentToken.tokenType == r_true || currentToken.tokenType == r_false || currentToken.tokenType == intLit || currentToken.tokenType == charLit || currentToken.tokenType == strLit ){
            literal();
        } else if (currentToken.tokenType == r_this || currentToken.tokenType == mvID || currentToken.tokenType == r_new || currentToken.tokenType == openBr || currentToken.tokenType == classID){
            acceso();
        } else throw new SyntacticException();
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
        } else throw new SyntacticException();

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
        } else throw new SyntacticException();
    }

    private void accesoVarMet() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(mvID);
        accesoVarMetFactorizado();
    }
    private void accesoVarMetFactorizado() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == openBr) {
            argsActuales();
        } else {}
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
        } else {}
    }
    private void listaExps() throws LexicalException, SourceFileReaderException, SyntacticException {
        expresion();
        listaExpsFactorizada();
    }
    private void listaExpsFactorizada() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == comma){
            match(comma);
            listaExps();
        } else {}
    }

    private void encadenadoOpt() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == dot) {
            varOMetEncadenado();
        } else {

        }
    }
    private void varOMetEncadenado() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(dot);
        match(mvID);
        varOMetEncadenadoFactorizado();
    }
    private void varOMetEncadenadoFactorizado() throws LexicalException, SourceFileReaderException, SyntacticException {
        if (currentToken.tokenType == openBr) {
            argsActuales();
            encadenadoOpt();
        } else {

        }
    }
}
