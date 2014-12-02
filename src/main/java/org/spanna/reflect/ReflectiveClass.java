package org.spanna.reflect;

import org.spanna.Main;
import org.spanna.asm.BytecodeLocator;
import org.spanna.util.Util;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.ClassReader;

public class ReflectiveClass implements ReflectiveObject {

    private final String name;
    private final BytecodeLocator locator;
    private final Map<String, ReflectiveField> fields = new HashMap<>();
    private final Map<MethodDescription, ReflectiveMethod> methods = new HashMap<>();
    private String obfuscatedName;
    private Class clazz;

    private ReflectiveClass(String name, BytecodeLocator locator) {
        this.name = name;
        this.locator = locator;
        ReflectionRegistry.registerReflectiveClass(name, this);
    }

    public ReflectiveClass addField(String name, BytecodeLocator locator) {
        if (isInitialized()) {
            throw new RuntimeException("Can not add field to a ReflectiveClass post-init!");
        }

        if (fields.containsKey(name)) {
            throw new RuntimeException(this + " already has a field like " + name);
        }

        fields.put(name, new ReflectiveField(this, name, locator));
        return this;
    }

    public ReflectiveClass addMethod(MethodDescription desc, BytecodeLocator locator) {
        if (isInitialized()) {
            throw new RuntimeException("Can not add method to a ReflectiveClass post-init!");
        }

        if (methods.containsKey(desc)) {
            throw new RuntimeException(this + " already has a method like " + name);
        }

        methods.put(desc, new ReflectiveMethod(this, desc, locator));
        return this;
    }

    @Override
    public boolean locate(ClassReader cr) {
        locator.reset();
        cr.accept(locator, 0);

        if (locator.found()) {
            obfuscatedName = Util.toBinaryClassName(locator.getFoundClassName());

            for (ReflectiveField field : fields.values()) {
                if (!field.isFound()) {
                    field.locate(cr);
                }
            }

            for (ReflectiveMethod method : methods.values()) {
                if (!method.isFound()) {
                    method.locate(cr);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public void initializeAllReferences() {
        if (!initializeReference()) {
            return;
        }

        for (ReflectiveField field : fields.values()) {
            field.initializeReference();
        }

        for (ReflectiveMethod method : methods.values()) {
            method.initializeReference();
        }
    }

    @Override
    public boolean initializeReference() {
        if (clazz != null) {
            throw new RuntimeException("Reference already initialized for " + this);
        }

        if (obfuscatedName == null) {
            System.err.println("obfuscatedName is null for " + this + ". Not initializing reference.");
            return false;
        }

        try {
            clazz = Class.forName(obfuscatedName, true, Main.getClassLoader());
            return true;
        } catch (ReflectiveOperationException ex) {
            System.err.println("Error while loading " + this);
            ex.printStackTrace();
            return false;
        }
    }

    public ReflectiveField getField(String name) {
        return fields.get(name);
    }

    public ReflectiveMethod getMethod(String name, Class<?> returnType, Class<?>... args) {
        return methods.get(new MethodDescription(name, returnType, args));
    }

    public ReflectiveMethod getMethod(MethodDescription desc) {
        return methods.get(desc);
    }

    public boolean isObfuscationEqual(String obfuscatedName) {
        return obfuscatedName != null && obfuscatedName.equals(getObfuscatedName());
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
        return clazz != null;
    }

    @Override
    public boolean isFound() {
        return obfuscatedName != null;
    }

    public Class getReflectedClass() {
        return clazz;
    }
    
    public Object getRawField(String field, Object instance) {
        try {
            return clazz.getField(field).get(instance);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new RuntimeException(ex); // Sneaky!
        }
    }

    @Override
    public String toString() {
        return "ReflectiveClass{" + "name=" + name + ", obfuscatedName=" + obfuscatedName + ", fields=" + fields + ", methods=" + methods + '}';
    }

    public static ReflectiveClass register(String name, BytecodeLocator locator) {
        return new ReflectiveClass(name, locator);
    }

    public static ReflectiveClass get(String name) {
        return ReflectionRegistry.getReflectiveClass(name);
    }
}
