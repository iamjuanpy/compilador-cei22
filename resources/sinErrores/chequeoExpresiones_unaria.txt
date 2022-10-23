class A {

    private int a,b;

    boolean m1(){
        a = 1;
        b = +a;
        b = -a;
        a = +5;
        a = -5 + -b; // chequeo binaria tmb

        var x = new A().m1();
        x = m1();
        x = x;
        x = !x;

        return !true && !m1() || !false || m1(); // idem
    }

    static void main(){

    }

}