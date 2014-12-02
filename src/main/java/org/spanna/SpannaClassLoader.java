package org.spanna;

//Very Important part of Spanna!
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SpannaClassLoader extends URLClassLoader {

    private final Map<String, Class<?>> classes = new HashMap<>();

    public SpannaClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void defineClass0(String name, byte[] data, int off, int len) {
        this.defineClass(name, data, off, len);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> result = classes.get(name);

        if (result == null) {
            result = super.findClass(name);

            classes.put(name, result);
        }

        return result;
    }

    public Set<String> getClasses() {
        return classes.keySet();
    }

}
