package minijavaCompiler.syntax.exceptions;

import minijavaCompiler.lexical.TokenType;

import java.util.HashMap;
import java.util.Map;

import static minijavaCompiler.lexical.TokenType.*;


public class SyntaxErrorNamesUtil {

    private static Map<TokenType,String> map;

    public static Map<TokenType,String> getTokenNamesMap() {
        if (map == null) {
            map = new HashMap<>();
            map.put(classID,"identificador de Clase");
            map.put(mvID,"identificador de Variable o Método");

            map.put(r_class,"palabra reservada class");
            map.put(r_public,"palabra reservada public");
            map.put(r_void,"palabra reservada void");
            map.put(r_if,"palabra reservada if");
            map.put(r_this,"palabra reservada this");
            map.put(r_new,"palabra reservada new");
            map.put(r_else,"palabra reservada else");
            map.put(r_boolean,"palabra reservada boolean");
            map.put(r_private,"palabra reservada private");
            map.put(r_interface,"palabra reservada interface");
            map.put(r_extends,"palabra reservada extends");
            map.put(r_static,"palabra reservada static");
            map.put(r_char,"palabra reservada char");
            map.put(r_while,"palabra reservada while");
            map.put(r_null,"palabra reservada null");
            map.put(r_implements,"palabra reservada implements");
            map.put(r_int,"palabra reservada int");
            map.put(r_return,"palabra reservada return");
            map.put(r_var,"palabra reservada var");
            map.put(r_true,"palabra reservada true");
            map.put(r_false,"palabra reservada false");

            map.put(intLit,"literal entero");
            map.put(charLit,"literal char");
            map.put(strLit,"literal string");

            map.put(openBr,"(");
            map.put(closeBr,")");
            map.put(openCurly,"{");
            map.put(closeCurly,"}");
            map.put(semicolon,";");
            map.put(comma,",");
            map.put(dot,".");

            map.put(greater,">");
            map.put(less,"<");
            map.put(not,"!");
            map.put(equals,"==");
            map.put(greaterOrEquals,">=");
            map.put(lessOrEquals,"<=");
            map.put(notEquals,"!=");
            map.put(addOP,"+");
            map.put(subOP,"-");
            map.put(multOP,"*");
            map.put(divOP,"/");
            map.put(andOP,"&&");
            map.put(orOP,"||");
            map.put(modOP,"%");

            map.put(assign,"=");
            map.put(addAssign,"+=");
            map.put(subAssign,"-=");

            map.put(eof,"el fin del archivo");
        }
        return map;
    }
}
