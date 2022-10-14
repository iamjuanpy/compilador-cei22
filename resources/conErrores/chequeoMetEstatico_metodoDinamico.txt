///[Error:m1|12]

class B {
    void m1(String string){
        System.printS(string);
    }
}

class A extends B {

    static void main(){
        m1("hola");
    }
}