package org.spanna.asm;

import org.spanna.reflect.ReflectiveClass;
import org.objectweb.asm.FieldVisitor;

public class FieldClassBytecodeLocator extends BytecodeLocator {

    private final String reflectiveClassName;
    private String obfuscatedClassName;

    public FieldClassBytecodeLocator(String reflectiveClassName) {
        super();
        this.reflectiveClassName = reflectiveClassName;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        FieldVisitor fv = super.visitField(access, name, desc, signature, value);

        if (obfuscatedClassName == null) {
            ReflectiveClass rc = ReflectiveClass.get(reflectiveClassName);
            if (rc != null) {
                obfuscatedClassName = rc.getObfuscatedName();
                if (obfuscatedClassName != null) {
                    obfuscatedClassName = obfuscatedClassName.replace(".", "/");
                }
            }
        }

        if (desc.startsWith("L") && desc.endsWith(";")) {
            if (desc.substring(1, desc.length() - 1).equals(obfuscatedClassName)) {
                setFound();
            }
        }

        return fv;
    }
}
