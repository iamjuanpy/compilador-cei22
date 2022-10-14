class A {

    public A obj;

    static void main(){}

    void test() {
        m1(-5);
    }

    void m1(int a) {
        while (a > 0)
            a = a - 1;

        while (true) {
            System.printI(a);
            a += a;
        }
    }

}