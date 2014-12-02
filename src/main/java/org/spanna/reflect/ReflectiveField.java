package org.spanna.reflect;

import org.spanna.asm.BytecodeLocator;
import java.lang.reflect.Field;
import org.objectweb.asm.ClassReader;

public class ReflectiveField implements ReflectiveObject {

    private final ReflectiveClass reflectiveClass;
    private final String name;
    private final BytecodeLocator locator;
    private String obfuscatedName;
    private Field field;

    ReflectiveField(ReflectiveClass reflectiveClass, String name, BytecodeLocator locator) {
        this.reflectiveClass = reflectiveClass;
        this.name = name;
        this.locator = locator;
    }

    @Override
    public String toString() {
        return "ReflectiveField{" + "name=" + name + ", obfuscatedName=" + obfuscatedName + '}';
    }

    @Override
    public boolean locate(ClassReader cr) {
        if (isFound()) {
            return true;
        }

        locator.reset();
        cr.accept(locator, 0);

        if (locator.found()) {
            obfuscatedName = locator.getFoundFieldName();
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
            field = cls.getDeclaredField(obfuscatedName);
            return true;
        } catch (ReflectiveOperationException ex) {
            System.err.println("Error while loading " + this);
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getObfuscatedName() {
        return obfuscatedName;
    }

    @Override
    public boolean isInitialized() {
        return field != null;
    }

    @Override
    public boolean isFound() {
        return obfuscatedName != null;
    }

    public Field getReflectedField() {
        return field;
    }

    public Object get(Object obj) throws IllegalArgumentException, IllegalAccessException {
        try {
            return field.get(obj);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException(ex); // Sneaky!
        }
    }

}
