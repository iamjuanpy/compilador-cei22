package minijavaCompiler.semantics.types.primitives;

import minijavaCompiler.lexical.Token;
import minijavaCompiler.semantics.types.PrimitiveType;
import minijavaCompiler.semantics.types.ReferenceType;
import minijavaCompiler.semantics.types.Type;

public class CharType extends PrimitiveType {
    public CharType(){
        super("char");
    }

    public boolean isSubtypeOf(Type type) {
        if (type.equals(new IntType())) // Coercion char a int
            return true;
        else if (type.getTypeName().equals("String")) // Coercion char a string
            return true;
        else return super.isSubtypeOf(type); // Chequea si ambos son char
    }
}
