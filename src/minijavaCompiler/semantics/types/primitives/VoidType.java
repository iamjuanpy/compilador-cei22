package minijavaCompiler.semantics.types.primitives;

import minijavaCompiler.semantics.types.PrimitiveType;
import minijavaCompiler.semantics.types.Type;

public class VoidType extends PrimitiveType {
    public VoidType(){
        super("void");
    }

    public boolean isSubtypeOf(Type type) {return false;}
}
