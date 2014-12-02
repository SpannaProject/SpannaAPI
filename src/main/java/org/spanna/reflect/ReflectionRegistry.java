package org.spanna.reflect;

import org.spanna.asm.*;
import static org.spanna.reflect.ReflectiveClass.get;
import static org.spanna.reflect.ReflectiveClass.register;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.objectweb.asm.ClassReader;

public class ReflectionRegistry {

    private static boolean initialized = false;
    private static final Map<String, ReflectiveClass> reflectiveClasses = new LinkedHashMap<>();

    public static void registerAll() {
        register("GameProfile", new ClassNameBytecodeLocator("com/mojang/authlib/GameProfile"));
        register("DedicatedServer", new StringBytecodeLocator("Starting Spanna along Minecraft server version", StringBytecodeLocator.SearchMode.STARTS_WITH));
        {
            // public boolean init()
            get("DedicatedServer").addMethod(new MethodDescription("init", boolean.class),
                    new StringBytecodeLocator("Starting Spanna along Minecraft server version", StringBytecodeLocator.SearchMode.STARTS_WITH));
        }
        register("PlayerList", new StringBytecodeLocator(" has logged in with entity id ", StringBytecodeLocator.SearchMode.CONTAINS));
        {
            // public final List players
            get("PlayerList").addField("players", new FieldTypeBytecodeLocator(java.util.List.class));
        }
        register("MinecraftServer", new ClassNameBytecodeLocator("net/minecraft/server/MinecraftServer"));
        {
            get("MinecraftServer").addField("playerList", new FieldClassBytecodeLocator("PlayerList"));
        }
        register("World", new StringBytecodeLocator("Coordinates of biome request"));
        register("DedicatedPlayerList", new StringBytecodeLocator("view-distance"));
        register("Entity", new StringBytecodeLocator("entityBaseTick"));
        register("EntityHuman", new StringBytecodeLocator("game.player.swim"));
        {
            get("EntityHuman").addField("profile", new FieldClassBytecodeLocator("GameProfile"));
        }
        register("EntityPlayer", new StringBytecodeLocator("playerGameType"));
    }

    public static void initializeReferences() {
        if (initialized) {
            throw new RuntimeException("ReflectionRegistry is already initialized");
        }

        for (Entry<String, ReflectiveClass> entry : reflectiveClasses.entrySet()) {
            entry.getValue().initializeAllReferences();
        }

        initialized = true;
    }

    public static void locateClassesUsingBytecode(ClassReader cr) {
        for (Entry<String, ReflectiveClass> entry : reflectiveClasses.entrySet()) {
            entry.getValue().locate(cr);
        }
    }

    public static Map<String, ReflectiveClass> getReflectiveClasses() {
        return reflectiveClasses;
    }

    public static ReflectiveClass getReflectiveClass(String name) {
        return reflectiveClasses.get(name);
    }

    static void registerReflectiveClass(String name, ReflectiveClass object) {
        if (initialized) {
            throw new RuntimeException("ReflectiveRegistry is no longer accepting registrations");
        }

        if (reflectiveClasses.containsKey(name)) {
            throw new RuntimeException("ReflectiveClass " + name + " is already registered");
        }

        reflectiveClasses.put(name, object);
    }
}
