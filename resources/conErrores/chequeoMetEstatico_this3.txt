///[Error:this|16]

class B {

    public int b;

    int m1(){
        return 2;
    }

}

class A extends B {

    static void main(){
        suma(this.m1(), "test"); // suma se puede llamar porque es estatico
    }

    static int suma(int a, String b) {
        return 0;
    }
}