package org.spanna.reflect;

import org.spanna.asm.BytecodeLocator;
import org.spanna.util.Util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.objectweb.asm.ClassReader;

public class ReflectiveMethod implements ReflectiveObject {

    private final ReflectiveClass reflectiveClass;
    private final MethodDescription desc;
    private final BytecodeLocator locator;
    private String obfuscatedName;
    private Method method;

    ReflectiveMethod(ReflectiveClass reflectiveClass, MethodDescription desc, BytecodeLocator locator) {
        this.reflectiveClass = reflectiveClass;
        this.desc = desc;
        this.locator = locator;
    }

    @Override
    public String toString() {
        return "ReflectiveMethod{" + "name=" + desc.name + ", obfuscatedName=" + obfuscatedName + '}';
    }

    @Override
    public boolean locate(ClassReader cr) {
        if (isFound()) {
            return true;
        }

        locator.reset();
        cr.accept(locator, 0);

        if (locator.found()) {
            if (!desc.signature.equals(locator.getFoundMethodDesc())) {
                System.out.println("[SIG] Signature does not match for " + this + " - expected " + desc.signature + ", got " + locator.getFoundMethodDesc() + ".");
                return false;
            }

            obfuscatedName = locator.getFoundMethodName();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean initializeReference() {
        if (obfuscatedName == null) {
            return false;
        }

        Class<?> cls = reflectiveClass.getReflectedClass();
        if (cls == null) {
            return false;
        }

        try {
            method = cls.getDeclaredMethod(obfuscatedName, Util.signatureToArray(desc.signature));
            return true;
        } catch (ReflectiveOperationException ex) {
            System.err.println("Error while loading " + this);
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public String getName() {
        return desc.name;
    }

    public String getDescription() {
        return desc.signature;
    }

    @Override
    public String getObfuscatedName() {
        return obfuscatedName;
    }

    @Override
    public boolean isInitialized() {
        return method != null;
    }

    @Override
    public boolean isFound() {
        return obfuscatedName != null;
    }

    public Method getReflectedMethod() {
        return method;
    }

    public Object invoke(Object obj, Object... args) {
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex); // Sneaky!
        }
    }
}
