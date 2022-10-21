///[Error:m1|8]

class B{}

class A {

    static void main(){
        m1(new B());
    }

    static boolean m1(boolean b){
        return b;
    }

}