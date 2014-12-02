package org.spanna.reflect;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

public interface ReflectiveObject {

    public boolean locate(ClassReader cr);

    public boolean initializeReference();

    public String getName();

    public String getObfuscatedName();

    public boolean isInitialized();

    public boolean isFound();

}
