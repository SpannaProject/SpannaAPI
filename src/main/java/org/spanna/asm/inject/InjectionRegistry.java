package org.spanna.asm.inject;

import org.spanna.reflect.MethodDescription;
import org.spanna.reflect.ReflectiveClass;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class InjectionRegistry {

    private static final Map<String, ClassInjector> classInjectors = new HashMap<>();
    private static final Map<String, Map<MethodDescription, MethodInjector>> methodInjectors = new HashMap<>();

    public static void registerAll() {
        registerClass("DedicatedServer", new DummyClassInjector());
        {
            registerMethod("DedicatedServer", new MethodDescription("init", boolean.class), new MethodInjector() {
                private boolean injected = false;

                @Override
                public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                    if (opcode == Opcodes.GETSTATIC && !injected) {
                        System.out.println("Hooking DedicatedServer.init");
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "org/spanna/hook/DedicatedServer", "init", "()V", false);
                        injected = true;
                    }

                    super.visitFieldInsn(opcode, owner, name, desc);
                }
            });
        }

        registerClass("EntityHuman", new ClassInjector() {
            @Override
            public void visitEnd() {
                super.visitField(Opcodes.ACC_PUBLIC, "spannaPlayer", "Lorg/spanna/entity/RTPlayer;", null, null).visitEnd();
                super.visitEnd();
            }
        });
        {
            registerMethod("EntityHuman", new MethodDescription("<init>", void.class, ReflectiveClass.get("World"), ReflectiveClass.get("GameProfile")), new MethodInjector() {
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.RETURN) {
                        System.out.println("Hooking EntityHuman.<init>");
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitTypeInsn(Opcodes.NEW, "org/spanna/entity/RTPlayer");
                        super.visitInsn(Opcodes.DUP);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "org/spanna/entity/RTPlayer", "<init>", "(Ljava/lang/Object;)V", false);
                        super.visitFieldInsn(Opcodes.PUTFIELD, ReflectiveClass.get("EntityHuman").getObfuscatedName(), "spannaPlayer", "Lorg/spanna/entity/RTPlayer;");
                    }
                    
                    super.visitInsn(opcode);
                }
            });
        }
    }

    public static void registerClass(String reflectiveClassName, ClassInjector injector) {
        if (classInjectors.containsKey(reflectiveClassName)) {
            throw new RuntimeException("InjectionRegistry already has an Injector for " + reflectiveClassName);
        }

        classInjectors.put(reflectiveClassName, injector);
    }

    public static void registerMethod(String reflectiveClassName, MethodDescription desc, MethodInjector injector) {
        if (!classInjectors.containsKey(reflectiveClassName)) {
            throw new RuntimeException("InjectionRegistry does not have an Injector for " + reflectiveClassName);
        }

        Map<MethodDescription, MethodInjector> injectorMap = methodInjectors.get(reflectiveClassName);
        if (injectorMap == null) {
            injectorMap = new HashMap<>();
            methodInjectors.put(reflectiveClassName, injectorMap);
        }

        if (injectorMap.containsKey(desc)) {
            throw new RuntimeException("InjectionRegistry already has a MethodInjector for " + reflectiveClassName + "/" + desc);
        }

        injectorMap.put(desc, injector);
    }

    public static void injectClass(String reflectiveClassName, ClassReader classReader, ClassWriter classWriter) throws ReflectiveOperationException {
        if (!classInjectors.containsKey(reflectiveClassName)) {
            return;
        }

        ClassInjector injector = classInjectors.get(reflectiveClassName);

        if (methodInjectors.containsKey(reflectiveClassName)) {
            Map<MethodDescription, MethodInjector> injectorMap = new HashMap<>();
            ReflectiveClass owningClass = ReflectiveClass.get(reflectiveClassName);

            if (owningClass != null) {
                for (Entry<MethodDescription, MethodInjector> entry : methodInjectors.get(reflectiveClassName).entrySet()) {
                    // We need to search through the obfuscated names, so obfuscate them here
                    MethodDescription unobfDesc = entry.getKey();
                    if (unobfDesc.name.equals("<init>")) {
                        // The constructor is a special case, put it in as is
                        injectorMap.put(unobfDesc, entry.getValue());
                    } else {
                        MethodDescription obfDesc = new MethodDescription(owningClass.getMethod(unobfDesc).getObfuscatedName(), unobfDesc.signature);
                        injectorMap.put(obfDesc, entry.getValue());
                    }
                }
            }

            injector.setMethodInjectorMap(injectorMap);
        }

        injector.setClassVisitor(classWriter);
        classReader.accept(injector, 0);
    }

    public static Set<String> getReflectiveClassNames() {
        return classInjectors.keySet();
    }
}
