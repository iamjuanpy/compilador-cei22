class A {

    static void main() {
        var variable = 0;
        variable = 1;
        variable += 1;
        variable -= 1;
        {
            var variable2 = new A();
        }

        var variable2 = new Object();

        var variable3 = variable;

        if (true) {
            var variable0 = 'a';
        }
        else {var variable0 = 'b';}
    }

}