package minijavaCompiler.semantics.types.primitives;

import minijavaCompiler.semantics.types.PrimitiveType;
import minijavaCompiler.semantics.types.Type;

public class IntType extends PrimitiveType {
    public IntType(){
        super("int");
    }

    @Override
    public boolean isSubtypeOf(Type type) {
        if (type.getTypeName().equals("String")) // Coercion de int a string
            return true;
        else return super.isSubtypeOf(type); // Chequea si ambos son int
    }
}
