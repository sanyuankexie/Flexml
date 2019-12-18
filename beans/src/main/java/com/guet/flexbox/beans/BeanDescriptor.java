package com.guet.flexbox.beans;

import java.lang.ref.WeakReference;

public final class BeanDescriptor extends FeatureDescriptor {

    private final WeakReference<Class<?>> _beanClass;

    BeanDescriptor(Class<?> beanClass) {
        super.setName(beanClass.getSimpleName());
        _beanClass = new WeakReference<Class<?>>(beanClass);
    }

    public Class<?> getBeanClass() {
        return _beanClass.get();
    }
}
