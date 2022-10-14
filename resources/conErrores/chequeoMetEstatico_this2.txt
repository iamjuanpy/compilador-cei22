///[Error:this|12]

class B {

    public int b;

}

class A extends B {

    static void main(){
        suma(this.b, "test"); // suma se puede llamar porque es estatico
    }

    static int suma(int a, String b) {
        return 0;
    }
}