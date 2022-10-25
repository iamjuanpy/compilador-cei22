package minijavaCompiler.semantics.types.primitives;

import minijavaCompiler.semantics.types.PrimitiveType;
import minijavaCompiler.semantics.types.Type;

public class IntType extends PrimitiveType {
    public IntType(){
        super("int");
    }

    public boolean isSubtypeOf(Type type) {
        if (type.equals(new CharType())) // Coercion int a char
            return true;
        else return super.isSubtypeOf(type); // Chequea si ambos son char
    }
}
