package org.spanna;

import org.spanna.asm.inject.InjectionRegistry;
import org.spanna.reflect.ReflectionRegistry;
import org.spanna.reflect.ReflectiveClass;
import org.spanna.util.Util;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
//Very Important part of Spanna!
public class Main {

    private static SpannaClassLoader classLoader;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Arguments: <vanilla JAR file>");
            return;
        }

        File jarFile = new File(args[0]);
        if (!jarFile.exists()) {
            System.err.println("[\/]Specified JAR file " + jarFile.getName() + " does not exist, exiting.");
            return;
        }

        classLoader = new RTClassLoader(new URL[]{jarFile.toURI().toURL()}, ClassLoader.getSystemClassLoader());
        
        System.out.println("Beginning startup process. This will most likely take a long time, please wait...");
        System.out.println("Initializing reflection registry...");
        ReflectionRegistry.registerAll();
        Map<String, byte[]> classes = new HashMap<>();
        try (ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile))) {
            System.out.println("Reading JAR file...");

            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().endsWith(".class")) {
                    System.out.println("Reading class " + Util.toBinaryClassName(entry.getName()) + "...");
                    byte[] clazz = ByteStreams.toByteArray(zip);
                    classes.put(Util.toBinaryClassName(entry.getName()), clazz);

                    ClassReader reader = new ClassReader(clazz);
                    ReflectionRegistry.locateClassesUsingBytecode(reader);
                } else if (entry.getName().equals("log4j2.xml")) {
                    System.out.println("Copying log4j2.xml...");
                    File file = new File("log4j2.xml");
                    FileOutputStream fos = new FileOutputStream(file);
                    ByteStreams.copy(zip, fos);
                    fos.flush();
                    fos.close();
                    System.setProperty("log4j.configurationFile", file.toURI().toURL().toString());
                }
            }
        }
        
        // TODO: Really don't want to have to do multiple location attempts,
        // but we need to unless we find a better solution for load order...
        System.out.println("Performing second location attempt for accuracy...");
        for (byte[] b : classes.values()) {
            ClassReader reader = new ClassReader(b);
            ReflectionRegistry.locateClassesUsingBytecode(reader);
        }

        System.out.println("Initializing injection registry...");
        InjectionRegistry.registerAll();
        System.out.println("Injecting classes...");
        List<String> injected = new ArrayList<>();
        for (String s : InjectionRegistry.getReflectiveClassNames()) {
            ReflectiveClass rc = ReflectiveClass.get(s);
            if (rc != null && rc.getObfuscatedName() != null && classes.containsKey(rc.getObfuscatedName())) {
                System.out.println("Injecting " + rc + "...");
                ClassReader cr = new ClassReader(classes.get(rc.getObfuscatedName()));
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                InjectionRegistry.injectClass(rc.getName(), cr, cw);

                System.out.println("Defining " + rc.getObfuscatedName() + "...");
                byte[] data = cw.toByteArray();
                classLoader.defineClass0(rc.getObfuscatedName(), data, 0, data.length);
                injected.add(rc.getObfuscatedName());
            }
        }

        System.out.println("Initializing reflective references...");
        ReflectionRegistry.initializeReferences();

        System.out.println("----------[ CLASS LOCATION RESULTS ]----------");
        for (ReflectiveClass rc : ReflectionRegistry.getReflectiveClasses().values()) {
            System.out.println(rc);
        }

        System.out.println("Starting Minecraft server running Spanna...");
        Class mainClass = Class.forName("net.minecraft.server.MinecraftServer", true, classLoader);
        Method mainMethod = mainClass.getMethod("main", new Class[]{String[].class});
        mainMethod.invoke(null, (Object) args);
    }

    public static SpannaClassLoader getClassLoader() {
        return classLoader;
    }
}
