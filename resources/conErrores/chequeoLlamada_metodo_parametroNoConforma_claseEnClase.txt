///[Error:m1|8]

class B{}

class A {

    static void main(){
        m1(new B());
    }

    static A m1(A b){
        return b;
    }

}