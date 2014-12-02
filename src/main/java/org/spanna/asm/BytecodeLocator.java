package org.spanna.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public abstract class BytecodeLocator extends ClassVisitor {

    // Class
    private String currentClassName;

    // Method
    private String currentMethodName;
    private String currentMethodDesc;

    // Field
    private String currentFieldName;
    private String currentFieldDesc;

    // Found Class
    private String foundClassName;

    // Found Method
    private String foundMethodName;
    private String foundMethodDesc;

    // Found Field
    private String foundFieldName;
    private String foundFieldDesc;

    public BytecodeLocator() {
        super(Opcodes.ASM5);
    }

    public BytecodeLocator(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    public boolean found() {
        return foundClassName != null || foundMethodName != null || foundFieldName != null;
    }

    public String getFoundClassName() {
        return foundClassName;
    }

    public String getFoundMethodName() {
        return foundMethodName;
    }

    public String getFoundMethodDesc() {
        return foundMethodDesc;
    }

    public String getFoundFieldName() {
        return foundFieldName;
    }

    public String getFoundFieldDesc() {
        return foundFieldDesc;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.currentClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        this.currentMethodName = name;
        this.currentMethodDesc = desc;
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        this.currentFieldName = name;
        this.currentFieldDesc = desc;
        return super.visitField(access, name, desc, signature, value);
    }

    protected void setFound() {
        this.foundClassName = currentClassName;
        this.foundMethodName = currentMethodName;
        this.foundMethodDesc = currentMethodDesc;
        this.foundFieldName = currentFieldName;
        this.foundFieldDesc = currentFieldDesc;
    }
    
    public void reset() {
        this.foundClassName = null;
        this.foundMethodName = null;
        this.foundMethodDesc = null;
        this.foundFieldName = null;
        this.foundFieldDesc = null;
    }

    public void setClassVisitor(ClassVisitor cv) {
        this.cv = cv;
    }
}
