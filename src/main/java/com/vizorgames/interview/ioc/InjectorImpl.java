package com.vizorgames.interview.ioc;

import com.vizorgames.interview.exception.BindingNotFoundException;
import com.vizorgames.interview.exception.ConstructorAmbiguityException;
import com.vizorgames.interview.exception.NoSuitableConstructorException;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InjectorImpl implements Injector {

    /**
     * Map that contains base and implementation.
     */
    private final Map<Class, Class> instance = new ConcurrentHashMap<>();
    /**
     * Map with base and type of singleton.
     */
    private final Map<Class, Class> singleton = new ConcurrentHashMap<>();
    /**
     * Map with implementations singleton and their instance.
     */
    private final Map<Class, Object> singletonsInstances = new ConcurrentHashMap<>();

    /**
     * @param type of class for search in container.
     * @param <T>  type class.
     * @return If container contains given type, return instance functional
     * interface Provider.
     * If constructor with @Inject exists but class not found in container
     * throw BindingNotFoundException().
     * Otherwise return null.
     */
    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        Constructor constructor;
        if (instance.containsKey(type)) {
            constructor = findConstructor(instance.get(type));
            return () -> type.cast(injectInstance(constructor));
        } else if (singleton.containsKey(type)) {
            constructor = findConstructor(singleton.get(type));
            return () -> injectSingleton(constructor, type);
        } else if (!searchAnnotatedConstructors(type).isEmpty()) {
            throw new BindingNotFoundException();
        }
        return null;
    }

    @Override
    public <T> void bind(Class<T> base, Class<? extends T> impl) {
        instance.put(base, impl);
    }

    @Override
    public <T> void bindSingleton(Class<T> base, Class<? extends T> impl) {
        singleton.put(base, impl);
    }

    /**
     * @param constructor for instance of singleton.
     * @param typeClass   base class of singleton.
     * @param <T>         type of class.
     * @return instance of Singleton.
     */
    private <T> T injectSingleton(Constructor constructor, Class<T> typeClass) {
        List<Object> parameters = new ArrayList<>();
        if (singletonsInstances.containsKey(singleton.get(typeClass))) {
            return typeClass.cast(singletonsInstances.get(singleton.get(typeClass)));
        }
        for (Class<?> parameterType : constructor.getParameterTypes()) {
            parameters.add(injectParamsOfConstructor(parameterType));
        }
        return getInstanceOfSingleton(constructor, typeClass, parameters);
    }

    /**
     * @param typeClass of class for search in container.
     * @return injecting object;
     */
    private Object injectParamsOfConstructor(Class<?> typeClass) {
        if (instance.containsKey(typeClass)) {
            Class<?> impl = instance.get(typeClass);
            Constructor constructor = findConstructor(impl);
            return injectInstance(constructor);
        } else if (singleton.containsKey(typeClass)) {
            Class<?> impl = singleton.get(typeClass);
            Constructor constructor = findConstructor(impl);
            return injectSingleton(constructor, typeClass);
        } else {
            throw new BindingNotFoundException();
        }
    }


    /**
     * @param constructor for creating class.
     * @param classImpl   class that should be created..
     * @param parameters  list with created object of parameters.
     * @param <T>         type class.
     * @return new singletonInstance
     * if singletonInstances not contains object of impl.
     * otherwise object from singletonInstances.
     */
    private <T> T getInstanceOfSingleton(Constructor constructor, Class<T> classImpl, List<Object> parameters) {
        try {
            synchronized (singletonsInstances) {
                Object singletonInstance = singletonsInstances.get(singleton.get(classImpl));
                if (singletonInstance == null) {
                    singletonInstance = constructor.newInstance(parameters.toArray());
                    singletonsInstances.put(singleton.get(classImpl), singletonInstance);
                }
                return classImpl.cast(singletonInstance);
            }
        } catch (Exception exp) {
            throw new RuntimeException(exp);
        }
    }

    /**
     * Create a new instance of class.
     *
     * @param constructor for this class.
     * @return instance of class.
     */
    private Object injectInstance(Constructor constructor) {
        try {
            List<Object> parameters = new ArrayList<>();
            for (Class<?> parameterType : constructor.getParameterTypes()) {
                parameters.add(injectParamsOfConstructor(parameterType));
            }
            return constructor.newInstance(parameters.toArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * find right constructor for class.
     *
     * @param impl class which contains right constructor.
     * @return constructor.
     */
    private Constructor findConstructor(Class impl) {
        List<Constructor> listOfAnnotatedConstructors = searchAnnotatedConstructors(impl);
        Constructor<?> constructor = searchDefaultConstructor(impl);
        return checkDefaultConstructor(listOfAnnotatedConstructors, constructor);
    }

    /**
     * @param listOfAnnotatedConstructors list with constructors with @Inject.
     * @param defaultConstructor          default constructor.
     * @return constructor for class.
     */
    private Constructor checkDefaultConstructor(List<Constructor> listOfAnnotatedConstructors, Constructor defaultConstructor) {
        int numberOfInjectConstructors = 1;
        if (listOfAnnotatedConstructors.size() > numberOfInjectConstructors) {
            throw new ConstructorAmbiguityException();
        } else if (listOfAnnotatedConstructors.size() == numberOfInjectConstructors) {
            return listOfAnnotatedConstructors.get(0);
        } else if (defaultConstructor != null) {
            return defaultConstructor;
        } else {
            throw new NoSuitableConstructorException();
        }
    }

    /**
     * search all constructors with annotation @Inject.
     *
     * @param classWithConstructor class with constructor.
     * @return constructors with @Inject annotation.
     */
    private List<Constructor> searchAnnotatedConstructors(Class classWithConstructor) {
        List<Constructor> listOfAnnotatedConstructors = new ArrayList<>();
        for (Constructor constructor : classWithConstructor.getConstructors()) {
            for (Annotation annotation : constructor.getDeclaredAnnotations()) {
                if (annotation.annotationType().equals(Inject.class)) {
                    listOfAnnotatedConstructors.add(constructor);
                }
            }
        }
        return listOfAnnotatedConstructors;
    }

    /**
     * Search default constructor.
     *
     * @param classWithConstructor class with constructor.
     * @return default constructor.
     */
    private Constructor searchDefaultConstructor(Class classWithConstructor) {
        for (Constructor constructor : classWithConstructor.getConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
                return constructor;
            }
        }
        return null;
    }
}

