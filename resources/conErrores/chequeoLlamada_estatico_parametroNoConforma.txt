///[Error:m1|9]

class B{

}

class A {
    static void main(){
        A.m1((new B()));
    }

    static boolean m1(A a){

    }
}