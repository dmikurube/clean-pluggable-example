import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Main {
    public static void main(String[] args) {
        JarClassLoader loader = new JarClassLoader(
            ((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs(),
            Thread.currentThread().getContextClassLoader());
        final Class<?> timestampImplClass;
        try {
            timestampImplClass = loader.loadClass("TimestampImpl");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return;
        }
        System.out.println(timestampImplClass.toString());

        final Class<?> jodaDateTimeClass;
        try {
            jodaDateTimeClass = loader.loadClass("org.joda.time.DateTime");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            return;
        }
        System.out.println(jodaDateTimeClass.toString());

        final Method nowMethod;
        try {
            nowMethod = jodaDateTimeClass.getMethod("now");
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            return;
        }
        System.out.println(nowMethod.toString());

        final Constructor timestampConstructor;
        try {
            timestampConstructor = timestampImplClass.getConstructor(jodaDateTimeClass);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
            return;
        }
        System.out.println(timestampConstructor.toString());

        final Timestamp timestamp;
        try {
            timestamp = (Timestamp) timestampConstructor.newInstance(nowMethod.invoke(null));
        } catch (InstantiationException ex) {
            ex.printStackTrace();
            return;
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            return;
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            return;
        }

        System.out.println(timestamp.getEpoch());
    }
}
