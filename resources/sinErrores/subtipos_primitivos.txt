
class A {

    static void main(){
        var entero = 1;
        var booleano = true;
        var caracter = 'a';

        // asignacion conforma
        caracter = 'b';
        booleano = false;
        entero = 2;

        // test tipo bool
        var test = caracter != 'a'; // conforma expresion accesoVar con literal
        test = booleano != true;
        test = entero != 5;
    }


}