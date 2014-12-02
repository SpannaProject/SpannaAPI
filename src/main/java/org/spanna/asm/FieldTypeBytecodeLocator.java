package org.spanna.asm;

import org.spanna.util.Util;
import org.objectweb.asm.FieldVisitor;

public class FieldTypeBytecodeLocator extends BytecodeLocator {

    private final String fieldType;

    public FieldTypeBytecodeLocator(Class<?> fieldType) {
        super();
        this.fieldType = Util.arrayToSignature(fieldType, null);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        FieldVisitor fv = super.visitField(access, name, desc, signature, value);
        if (desc.equals(fieldType)) {
            setFound();
        }

        return fv;
    }
}
