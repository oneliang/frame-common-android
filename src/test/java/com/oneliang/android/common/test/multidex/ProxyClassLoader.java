package com.oneliang.android.common.test.multidex;

/**
 * Created by oneliang on 2017/11/23. replace the mClassLoader in mPackageInfo
 */
public class ProxyClassLoader extends ClassLoader {

    /**
     * dynamic set the last class loader,load the class begin last class loader
     */
    private ClassLoader lastClassLoader = null;

    /**
     * constructor
     * 
     * @param parentClassLoader
     */
    public ProxyClassLoader(ClassLoader parentClassLoader) {
        super(parentClassLoader);
    }

    /**
     * find class
     * 
     * @param className
     */
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        if (lastClassLoader == null) {
            throw new ClassNotFoundException(className);
        }
        return lastClassLoader.loadClass(className);
    }

    /**
     * set last class loader
     * 
     * @param classLoader
     */
    public void setLastClassLoader(ClassLoader classLoader) {
        this.lastClassLoader = classLoader;
    }

    /**
     * get last class loader
     * 
     * @return ClassLoader
     */
    public ClassLoader getLastClassLoader() {
        return this.lastClassLoader;
    }
}
