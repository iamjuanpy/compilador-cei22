
class Variables {

    static void main(){
        // tipo clase
        Variables a, b = 5, c = 3;
        // llamada a met estatico
        Variables.main();
        Variables.main().v;
        Variables.main().m1();
        Variables.main().m2(123,"asd");
        Variables.main() += Variables.main().m2(123,"asd");
        Variables.main().v -= 2;
        Variables.main().m2() = null;
        // tipo primitivo
        int a;
        int b = m(), c, d = obj.v * m2() + 5;
        boolean b = obj.v;
        char c = 'a';
    }

}