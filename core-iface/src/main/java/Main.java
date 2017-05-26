import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class Main {
    public static void main(String[] args) {
        final Timestamp timestampNow = createTimestamp();
        System.out.println(timestampNow.getEpoch());

        for (String arg : args) {
            final URL pluginUrl;
            try {
                pluginUrl = (new File(arg)).toURL();
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                return;
            }

            PluginSpi plugin = loadPlugin(pluginUrl);
            System.out.println(plugin.formatTimestamp(timestampNow));
        }
    }

    private static PluginSpi loadPlugin(final URL pluginUrl) {
        URL[] pluginUrls = { pluginUrl };
        URLClassLoader loader = new URLClassLoader(pluginUrls, Thread.currentThread().getContextClassLoader());
        final Class<?> pluginClass;
        try {
            pluginClass = loader.loadClass("Plugin");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        System.out.println(pluginClass.toString());

        final Constructor pluginConstructor;
        try {
            pluginConstructor = pluginClass.getConstructor();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        System.out.println(pluginConstructor.toString());

        final PluginSpi plugin;
        try {
            plugin = (PluginSpi) pluginConstructor.newInstance();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

        return plugin;
    }

    private static Timestamp createTimestamp() {
        JarClassLoader loader = new JarClassLoader(
            ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs(),
            Thread.currentThread().getContextClassLoader());
        final Class<?> timestampImplClass;
        try {
            timestampImplClass = loader.loadClass("TimestampImpl");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        System.out.println(timestampImplClass.toString());

        final Class<?> jodaDateTimeClass;
        try {
            jodaDateTimeClass = loader.loadClass("org.joda.time.DateTime");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        System.out.println(jodaDateTimeClass.toString());

        final Method nowMethod;
        try {
            nowMethod = jodaDateTimeClass.getMethod("now");
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        System.out.println(nowMethod.toString());

        final Constructor timestampConstructor;
        try {
            timestampConstructor = timestampImplClass.getConstructor(jodaDateTimeClass);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        System.out.println(timestampConstructor.toString());

        final Timestamp timestamp;
        try {
            timestamp = (Timestamp) timestampConstructor.newInstance(nowMethod.invoke(null));
        } catch (InstantiationException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

        return timestamp;
    }
}
