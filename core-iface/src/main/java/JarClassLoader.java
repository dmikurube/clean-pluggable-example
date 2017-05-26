// Using a modified version of: http://qdolan.blogspot.jp/2008/10/embedded-jar-classloader-in-under-100.html

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarClassLoader extends URLClassLoader {
    public JarClassLoader(URL[] urls, ClassLoader parent) {
        super(new URL[0], parent);
        try {
            ProtectionDomain protectionDomain = getClass().getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            URL rootJarUrl = codeSource.getLocation();
            String rootJarName = rootJarUrl.getFile();
            if (isJar(rootJarName)) {
                addJarResource(new File(rootJarUrl.getPath()), true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException {
        try {
            Class<?> clazz = findLoadedClass(name);
            if (clazz == null) {
                clazz = findClass(name);
                if (resolve) {
                    resolveClass(clazz);
                }
            }
            return clazz;
        } catch (ClassNotFoundException e) {
            return super.loadClass(name, resolve);
        }
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isJar(String fileName) {
        return fileName != null &&
            (fileName.toLowerCase().endsWith(".jar.embed") ||
             fileName.toLowerCase().endsWith(".jar"));
    }

    private static File jarEntryAsFile(JarFile jarFile, JarEntry jarEntry) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            String name = jarEntry.getName().replace('/', '_').replace(".jar.embed", ".jar");
            int i = name.lastIndexOf(".");
            String extension = i > -1 ? name.substring(i) : "";
            File file = File.createTempFile(name.substring(0, name.length() - extension.length()) + ".", extension);
            file.deleteOnExit();
            input = jarFile.getInputStream(jarEntry);
            output = new FileOutputStream(file);
            int readCount;
            byte[] buffer = new byte[4096];
            while ((readCount = input.read(buffer)) != -1) {
                output.write(buffer, 0, readCount);
            }
            return file;
        } finally {
            close(input);
            close(output);
        }
    }

    private void addJarResource(File file, boolean root) throws IOException {
        JarFile jarFile = new JarFile(file);
        if (!root) {
            System.out.println(file.toURL().toString());
            addURL(file.toURL());
        }
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            if (!jarEntry.isDirectory() && isJar(jarEntry.getName())) {
                addJarResource(jarEntryAsFile(jarFile, jarEntry), false);
            }
        }
    }
}
