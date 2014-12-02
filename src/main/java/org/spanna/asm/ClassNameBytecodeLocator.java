package org.spanna.asm;

/**
 * Only really used for net.minecraft.server.MinecraftServer (NMSM) and a few other
 * deobfuscated classes.
 */
public class ClassNameBytecodeLocator extends BytecodeLocator {

    private final String name;

    public ClassNameBytecodeLocator(String name) {
        super();
        this.name = name;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        if (name.equals(this.name)) {
            setFound();
        }
    }

}
