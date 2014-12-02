package org.spanna.util;

import org.spanna.Main;
import org.spanna.reflect.ReflectiveClass;
import java.util.ArrayList;
import java.util.List;

//Very Important part of Spanna!
public class Util {

    /**
     * Converts a class's file name to a binary name.
     *
     * @param fileName class file name
     * @return class binary name
     */
    public static String toBinaryClassName(String fileName) {
        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);
        }
        if (fileName.endsWith(".class")) {
            fileName = fileName.substring(0, fileName.length() - 6);
        }
        fileName = fileName.replace("/", ".");
        return fileName;
    }

    public static Class[] signatureToArray(String sig) {
        List<Class> list = new ArrayList<>();
        int index;
        for (index = 0; index < sig.length(); index++) {
            if (sig.charAt(index) == '(') {
                // Just the start of a method signature, continue
                continue;
            }

            Class clazz = signatureIdentifierToClass(sig.charAt(index));
            if (clazz == null) {
                // End of parameter declaration, break (next is return type)
                break;
            } else if (clazz == Class[].class) {
                // Fully-qualified Java type
                StringBuilder sb = new StringBuilder();
                for (; index < sig.length(); index++) {
                    if (sig.charAt(index) == ';') {
                        break;
                    }

                    sb.append(sig.charAt(index));
                }

                try {
                    list.add(Class.forName(sb.toString(), true, Main.getClassLoader()));
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                // Normal type, continue
                list.add(clazz);
            }
        }

        return list.toArray(new Class[0]);
    }

    public static String arrayToSignature(Object returnType, Object[] args) {
        StringBuilder sb = new StringBuilder();
        if (args != null) {
            // Only for methods
            sb.append('(');
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof ReflectiveClass) {
                    sb.append('L');
                    sb.append(((ReflectiveClass) args[i]).getObfuscatedName().replace(".", "/"));
                    sb.append(';');
                } else {
                    sb.append(classToSignatureIdentifier((Class) args[i]));
                }
            }
            sb.append(')');
        }
        
        if (returnType instanceof ReflectiveClass) {
            sb.append('L');
            sb.append(((ReflectiveClass) returnType).getObfuscatedName().replace(".", "/"));
            sb.append(';');
        } else {
            sb.append(classToSignatureIdentifier((Class) returnType));
        }
        return sb.toString();
    }

    private static Class signatureIdentifierToClass(char ident) {
        switch (ident) {
            case 'Z':
                return boolean.class;
            case 'B':
                return byte.class;
            case 'C':
                return char.class;
            case 'S':
                return short.class;
            case 'I':
                return int.class;
            case 'J':
                return long.class;
            case 'F':
                return float.class;
            case 'D':
                return double.class;
            case 'V':
                return void.class;
            case 'L':
                return Class.class;
            case '[':
                return Class[].class;
            case ')':
                return null;
            default:
                throw new RuntimeException("Unknown signature identifier " + ident);
        }
    }

    private static String classToSignatureIdentifier(Class clazz) {
        if (clazz == null) {
            return "";
        } else if (clazz == boolean.class) {
            return (clazz.isArray() ? "[" : "") + "Z";
        } else if (clazz == byte.class) {
            return (clazz.isArray() ? "[" : "") + "B";
        } else if (clazz == char.class) {
            return (clazz.isArray() ? "[" : "") + "C";
        } else if (clazz == short.class) {
            return (clazz.isArray() ? "[" : "") + "S";
        } else if (clazz == int.class) {
            return (clazz.isArray() ? "[" : "") + "I";
        } else if (clazz == float.class) {
            return (clazz.isArray() ? "[" : "") + "F";
        } else if (clazz == double.class) {
            return (clazz.isArray() ? "[" : "") + "D";
        } else if (clazz == void.class) {
            return (clazz.isArray() ? "[" : "") + "V";
        } else {
            return (clazz.isArray() ? "[" : "") + "L" + clazz.getName().replace(".", "/") + ";";
        }
    }

}
