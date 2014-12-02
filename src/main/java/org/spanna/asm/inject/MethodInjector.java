package org.spanna.asm.inject;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public abstract class MethodInjector extends MethodVisitor {

    public MethodInjector() {
        super(Opcodes.ASM5);
    }

    public MethodInjector(MethodVisitor mv) {
        super(Opcodes.ASM5, mv);
    }
    
    public void setMethodVisitor(MethodVisitor mv) {
        this.mv = mv;
    }
}
