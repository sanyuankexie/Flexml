package com.guet.flexbox.beans;

import java.lang.ref.WeakReference;

public final class BeanDescriptor extends FeatureDescriptor {

    private final String _beanName;
    private final WeakReference<Class<?>> _beanClass;

    BeanDescriptor(Class<?> beanClass) {
        _beanName = beanClass.getSimpleName();
        _beanClass = new WeakReference<Class<?>>(beanClass);
    }

    public String getName() {
        return _beanName;
    }

    public Class<?> getBeanClass() {
        return _beanClass.get();
    }
}
