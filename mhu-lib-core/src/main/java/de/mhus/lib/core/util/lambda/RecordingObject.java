package de.mhus.lib.core.util.lambda;


import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Adapted from https://github.com/benjiman/benjiql
 *
 * @author mikehummel
 * @version $Id: $Id
 * @since 3.3.0
 */
public class RecordingObject implements MethodInterceptor {

    private String currentPropertyName = "";
    private Recorder<?> currentMock = null;

    /**
     * <p>create.</p>
     *
     * @param cls a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a {@link de.mhus.lib.core.util.lambda.Recorder} object.
     */
    @SuppressWarnings("unchecked")
    public static <T> Recorder<T> create(Class<T> cls) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        final RecordingObject recordingObject = new RecordingObject();

        enhancer.setCallback(recordingObject);
        return new Recorder((T) enhancer.create(), recordingObject);
    }

    /** {@inheritDoc} */
    public Object intercept(Object o, Method method, Object[] os, MethodProxy mp) throws Throwable {
        if (method.getName().equals("getCurrentPropertyName")) {
            return getCurrentMethodName();
        }
        currentPropertyName = method.getName();
        try {
            currentMock = create(method.getReturnType());
            return currentMock.getObject();
        } catch (IllegalArgumentException e) {
            return DefaultValues.getDefault(method.getReturnType());
        }
    }

    /**
     * <p>getCurrentMethodName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCurrentMethodName() {
        return currentPropertyName + (currentMock == null ? "" : ("." + currentMock.getCurrentMethodName()));
    }
}
