/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.guet.flexbox.el;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @since EL 3.0
 */
public class ImportHandler {

    private static final Map<String,Set<String>> standardPackages = new HashMap<>();

    static {
        Set<String> javaLangClassNames = new HashSet<>();
        // Taken from Java 14 EA27 Javadoc
        // Interfaces
        javaLangClassNames.add("Appendable");
        javaLangClassNames.add("AutoCloseable");
        javaLangClassNames.add("CharSequence");
        javaLangClassNames.add("Cloneable");
        javaLangClassNames.add("Comparable");
        javaLangClassNames.add("Iterable");
        javaLangClassNames.add("ProcessHandle");
        javaLangClassNames.add("ProcessHandle.Info");
        javaLangClassNames.add("Readable");
        javaLangClassNames.add("Runnable");
        javaLangClassNames.add("StackWalker.StackFrame");
        javaLangClassNames.add("System.Logger");
        javaLangClassNames.add("Thread.UncaughtExceptionHandler");
        //Classes
        javaLangClassNames.add("Boolean");
        javaLangClassNames.add("Byte");
        javaLangClassNames.add("Character");
        javaLangClassNames.add("Character.Subset");
        javaLangClassNames.add("Character.UnicodeBlock");
        javaLangClassNames.add("Class");
        javaLangClassNames.add("ClassLoader");
        javaLangClassNames.add("ClassValue");
        javaLangClassNames.add("Compiler");
        javaLangClassNames.add("Double");
        javaLangClassNames.add("Enum");
        javaLangClassNames.add("Enum.EnumDesc");
        javaLangClassNames.add("Float");
        javaLangClassNames.add("InheritableThreadLocal");
        javaLangClassNames.add("Integer");
        javaLangClassNames.add("Long");
        javaLangClassNames.add("Math");
        javaLangClassNames.add("Module");
        javaLangClassNames.add("ModuleLayer");
        javaLangClassNames.add("ModuleLayer.Controller");
        javaLangClassNames.add("Number");
        javaLangClassNames.add("Object");
        javaLangClassNames.add("Package");
        javaLangClassNames.add("Process");
        javaLangClassNames.add("ProcessBuilder");
        javaLangClassNames.add("ProcessBuilder.Redirect");
        javaLangClassNames.add("Record");
        javaLangClassNames.add("Runtime");
        javaLangClassNames.add("Runtime.Version");
        javaLangClassNames.add("RuntimePermission");
        javaLangClassNames.add("SecurityManager");
        javaLangClassNames.add("Short");
        javaLangClassNames.add("StackTraceElement");
        javaLangClassNames.add("StackWalker");
        javaLangClassNames.add("StrictMath");
        javaLangClassNames.add("String");
        javaLangClassNames.add("StringBuffer");
        javaLangClassNames.add("StringBuilder");
        javaLangClassNames.add("System");
        javaLangClassNames.add("System.LoggerFinder");
        javaLangClassNames.add("Thread");
        javaLangClassNames.add("ThreadGroup");
        javaLangClassNames.add("ThreadLocal");
        javaLangClassNames.add("Throwable");
        javaLangClassNames.add("Void");
        //Enums
        javaLangClassNames.add("Character.UnicodeScript");
        javaLangClassNames.add("ProcessBuilder.Redirect.Type");
        javaLangClassNames.add("StackWalker.Option");
        javaLangClassNames.add("System.Logger.Level");
        javaLangClassNames.add("Thread.State");
        //Exceptions
        javaLangClassNames.add("ArithmeticException");
        javaLangClassNames.add("ArrayIndexOutOfBoundsException");
        javaLangClassNames.add("ArrayStoreException");
        javaLangClassNames.add("ClassCastException");
        javaLangClassNames.add("ClassNotFoundException");
        javaLangClassNames.add("CloneNotSupportedException");
        javaLangClassNames.add("EnumConstantNotPresentException");
        javaLangClassNames.add("Exception");
        javaLangClassNames.add("IllegalAccessException");
        javaLangClassNames.add("IllegalArgumentException");
        javaLangClassNames.add("IllegalCallerException");
        javaLangClassNames.add("IllegalMonitorStateException");
        javaLangClassNames.add("IllegalStateException");
        javaLangClassNames.add("IllegalThreadStateException");
        javaLangClassNames.add("IndexOutOfBoundsException");
        javaLangClassNames.add("InstantiationException");
        javaLangClassNames.add("InterruptedException");
        javaLangClassNames.add("LayerInstantiationException");
        javaLangClassNames.add("NegativeArraySizeException");
        javaLangClassNames.add("NoSuchFieldException");
        javaLangClassNames.add("NoSuchMethodException");
        javaLangClassNames.add("NullPointerException");
        javaLangClassNames.add("NumberFormatException");
        javaLangClassNames.add("ReflectiveOperationException");
        javaLangClassNames.add("RuntimeException");
        javaLangClassNames.add("SecurityException");
        javaLangClassNames.add("StringIndexOutOfBoundsException");
        javaLangClassNames.add("TypeNotPresentException");
        javaLangClassNames.add("UnsupportedOperationException");
        //Errors
        javaLangClassNames.add("AbstractMethodError");
        javaLangClassNames.add("AssertionError");
        javaLangClassNames.add("BootstrapMethodError");
        javaLangClassNames.add("ClassCircularityError");
        javaLangClassNames.add("ClassFormatError");
        javaLangClassNames.add("Error");
        javaLangClassNames.add("ExceptionInInitializerError");
        javaLangClassNames.add("IllegalAccessError");
        javaLangClassNames.add("IncompatibleClassChangeError");
        javaLangClassNames.add("InstantiationError");
        javaLangClassNames.add("InternalError");
        javaLangClassNames.add("LinkageError");
        javaLangClassNames.add("NoClassDefFoundError");
        javaLangClassNames.add("NoSuchFieldError");
        javaLangClassNames.add("NoSuchMethodError");
        javaLangClassNames.add("OutOfMemoryError");
        javaLangClassNames.add("StackOverflowError");
        javaLangClassNames.add("ThreadDeath");
        javaLangClassNames.add("UnknownError");
        javaLangClassNames.add("UnsatisfiedLinkError");
        javaLangClassNames.add("UnsupportedClassVersionError");
        javaLangClassNames.add("VerifyError");
        javaLangClassNames.add("VirtualMachineError");
        //Annotation Types
        javaLangClassNames.add("Deprecated");
        javaLangClassNames.add("FunctionalInterface");
        javaLangClassNames.add("Override");
        javaLangClassNames.add("SafeVarargs");
        javaLangClassNames.add("SuppressWarnings");
        standardPackages.put("java.lang", javaLangClassNames);

    }

    private Map<String,Set<String>> packageNames = new ConcurrentHashMap<>();
    private Map<String,String> classNames = new ConcurrentHashMap<>();
    private Map<String,Class<?>> clazzes = new ConcurrentHashMap<>();
    private Map<String,Class<?>> statics = new ConcurrentHashMap<>();


    public ImportHandler() {
        importPackage("java.lang");
    }


    public void importStatic(String name) throws ELException {
        int lastPeriod = name.lastIndexOf('.');

        if (lastPeriod < 0) {
            throw new ELException(Util.message(
                    null, "importHandler.invalidStaticName", name));
        }

        String className = name.substring(0, lastPeriod);
        String fieldOrMethodName = name.substring(lastPeriod + 1);

        Class<?> clazz = findClass(className, true);

        if (clazz == null) {
            throw new ELException(Util.message(
                    null, "importHandler.invalidClassNameForStatic",
                    className, name));
        }

        boolean found = false;

        for (Field field : clazz.getFields()) {
            if (field.getName().equals(fieldOrMethodName)) {
                int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers) &&
                        Modifier.isPublic(modifiers)) {
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(fieldOrMethodName)) {
                    int modifiers = method.getModifiers();
                    if (Modifier.isStatic(modifiers) &&
                            Modifier.isPublic(modifiers)) {
                        found = true;
                        break;
                    }
                }
            }
        }

        if (!found) {
            throw new ELException(Util.message(null,
                    "importHandler.staticNotFound", fieldOrMethodName,
                    className, name));
        }

        Class<?> conflict = statics.get(fieldOrMethodName);
        if (conflict != null) {
            throw new ELException(Util.message(null,
                    "importHandler.ambiguousStaticImport", name,
                    conflict.getName() + '.' +  fieldOrMethodName));
        }

        statics.put(fieldOrMethodName, clazz);
    }


    public void importClass(String name) throws ELException {
        int lastPeriodIndex = name.lastIndexOf('.');

        if (lastPeriodIndex < 0) {
            throw new ELException(Util.message(
                    null, "importHandler.invalidClassName", name));
        }

        String unqualifiedName = name.substring(lastPeriodIndex + 1);
        String currentName = Util.putIfAbsent(classNames,unqualifiedName, name);

        if (currentName != null && !currentName.equals(name)) {
            // Conflict. Same unqualifiedName, different fully qualified names
            throw new ELException(Util.message(null,
                    "importHandler.ambiguousImport", name, currentName));
        }
    }


    public void importPackage(String name) {
        // Import ambiguity is handled at resolution, not at import
        // Whether the package exists is not checked,
        // a) for sake of performance when used in JSPs (BZ 57142),
        // b) java.lang.Package.getPackage(name) is not reliable (BZ 57574),
        // c) such check is not required by specification.
        Set<String> preloaded = standardPackages.get(name);
        if (preloaded == null) {
            packageNames.put(name, Collections.<String>emptySet());
        } else {
            packageNames.put(name, preloaded);
        }
    }


    public Class<?> resolveClass(String name) {
        if (name == null || name.contains(".")) {
            return null;
        }

        // Has it been previously resolved?
        Class<?> result = clazzes.get(name);

        if (result != null) {
            if (NotFound.class.equals(result)) {
                return null;
            } else {
                return result;
            }
        }

        // Search the class imports
        String className = classNames.get(name);
        if (className != null) {
            Class<?> clazz = findClass(className, true);
            if (clazz != null) {
                clazzes.put(name, clazz);
                return clazz;
            }
        }

        // Search the package imports - note there may be multiple matches
        // (which correctly triggers an error)
        for (Map.Entry<String,Set<String>> entry : packageNames.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                // Standard package where we know all the class names
                if (!entry.getValue().contains(name)) {
                    // Requested name isn't in the list so it isn't in this
                    // package so move on to next package. This allows the
                    // class loader look-up to be skipped.
                    continue;
                }
            }
            className = entry.getKey() + '.' + name;
            Class<?> clazz = findClass(className, false);
            if (clazz != null) {
                if (result != null) {
                    throw new ELException(Util.message(null,
                            "importHandler.ambiguousImport", className,
                            result.getName()));
                }
                result = clazz;
            }
        }
        if (result == null) {
            // Cache NotFound results to save repeated calls to findClass()
            // which is relatively slow
            clazzes.put(name, NotFound.class);
        } else {
            clazzes.put(name, result);
        }

        return result;
    }


    public Class<?> resolveStatic(String name) {
        return statics.get(name);
    }


    private Class<?> findClass(String name, boolean throwException) {
        Class<?> clazz;
        ClassLoader cl = Util.getContextClassLoader();
        String path = name.replace('.', '/') + ".class";
        try {
            /* Given that findClass() has to be called for every imported
             * package and that getResource() is a lot faster then loadClass()
             * for resources that don't exist, the overhead of the getResource()
             * for the case where the class does exist is a lot less than the
             * overhead we save by not calling loadClass().
             */
            if (cl.getResource(path) == null) {
                return null;
            }
        } catch (ClassCircularityError cce) {
            // May happen under a security manager. Ignore it and try loading
            // the class normally.
        }
        try {
            clazz = cl.loadClass(name);
        } catch (ClassNotFoundException e) {
            return null;
        }

        // Class must be public, non-abstract, not an interface and (for
        // Java 9+) in an exported package
        JreCompat jreCompat = JreCompat.getInstance();
        int modifiers = clazz.getModifiers();
        if (!Modifier.isPublic(modifiers) || Modifier.isAbstract(modifiers) ||
                Modifier.isInterface(modifiers) || !jreCompat.isExported(clazz)) {
            if (throwException) {
                throw new ELException(Util.message(
                        null, "importHandler.invalidClass", name));
            } else {
                return null;
            }
        }

        return clazz;
    }


    /*
     * Marker class used because null values are not permitted in a
     * ConcurrentHashMap.
     */
    private static class NotFound {
    }
}
