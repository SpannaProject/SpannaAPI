package org.spanna.asm.inject;

import org.spanna.reflect.MethodDescription;
import java.util.Map;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public abstract class ClassInjector extends ClassVisitor {

    private Map<MethodDescription, MethodInjector> methodInjectorMap;

    public ClassInjector() {
        super(Opcodes.ASM5);
    }

    public ClassInjector(ClassVisitor cv) {
        super(Opcodes.ASM5, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (methodInjectorMap != null) {
            for (Map.Entry<MethodDescription, MethodInjector> entry : methodInjectorMap.entrySet()) {
                MethodDescription mDesc = entry.getKey();
                MethodInjector mInject = entry.getValue();

                if (mDesc.name.equals(name) && mDesc.signature.equals(desc)) {
                    // We have a winner!
                    mInject.setMethodVisitor(super.visitMethod(access, name, desc, signature, exceptions));
                    return mInject;
                }
            }
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    public void setClassVisitor(ClassVisitor cv) {
        this.cv = cv;
    }

    public Map<MethodDescription, MethodInjector> getMethodInjectorMap() {
        return methodInjectorMap;
    }

    public void setMethodInjectorMap(Map<MethodDescription, MethodInjector> methodInjectorMap) {
        this.methodInjectorMap = methodInjectorMap;
    }

}
