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

    void match(TokenType tokenName) throws LexicalException, SourceFileReaderException, SyntacticException {
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

    private void listaMiembros() {
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

    private void miembro() {
    }

    private void atributo() {
    }

    private void metodo() {
        encabezadoMetodo();
        bloque();
    }

    private void encabezadoMetodo() {
    }

    private void visibilidad(){}
    private void tipo(){}
    private void tipoPrimitivo(){}

    private void listaDecAtrs(){}
    private void listaDecAtrsFactorizada(){}

    private void estaticoOpt(){}
    private void tipoMetodo(){}

    private void argsFormales(){}
    private void listaArgsFormalesOpt(){}
    private void listaArgsFormales(){}
    private void listaArgsFormalesFactorizada(){}

    private void argFormal(){}

    private void bloque(){

    }

    private void listaSentencias(){}

    private void sentencia(){}
    private void asignacionOLlamada(){}
    private void asignacionOLlamadaFactorizada(){}

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

    private void expresionOpt(){}

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

    private void expresion(){}
    private void expresionRecursiva(){}

    private void operadorBinario() throws SyntacticException {
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
    private void expresionUnaria() throws SyntacticException {
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
        if (currentToken.tokenType == r_null){
            match(r_null);
        } else if (currentToken.tokenType == r_true){
            match(r_true);
        } else if (currentToken.tokenType == r_false){
            match(r_false);
        } else if (currentToken.tokenType == intLit){
            match(intLit);
        } else if (currentToken.tokenType == charLit){
            match(charLit);
        } else if (currentToken.tokenType == strLit){
            match(strLit);
        } else if (currentToken.tokenType == r_this){
            match(r_this);
        } else if (currentToken.tokenType == mvID){
            match(mvID);
        } else if (currentToken.tokenType == r_new){
            match(r_new);
        } else if (currentToken.tokenType == openBr){
            match(openBr);
        } else if (currentToken.tokenType == classID){
            match(classID);
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

    private void acceso(){}
    private void primario(){}

    private void accesoVarMet(){}
    private void accesoVarMetFactorizado(){}
    private void accesoMetodoEstatico(){}
    private void accesoThis() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(r_this);
    }
    private void accesoConstructor() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(r_new);
        match(classID);
        match(openBr);
        match(closeBr);
    }
    private void expresionParentizada(){}

    private void argsActuales() throws LexicalException, SourceFileReaderException, SyntacticException {
        match(openBr);
        listaExpsOpt();
        match(closeBr);
    }
    private void listaExpsOpt(){}
    private void listaExps(){}
    private void listaExpsFactorizada(){}

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
