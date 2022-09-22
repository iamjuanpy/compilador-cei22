
class D implements A{

    static void main(){}

    void m1 (boolean a, int b){}
}

interface B extends C {

    void m1(int a, boolean b);
}

interface A extends B,C {

    void m1(int a, boolean b);
}

interface C {

}
