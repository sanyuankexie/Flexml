package com.luke.skywalker.beans;

public final class GenericBeanInfo implements BeanInfo {

    protected final BeanDescriptor _bean;
    protected final PropertyDescriptor[] _properties;

    GenericBeanInfo(BeanDescriptor bean, PropertyDescriptor[] properties) {
        _bean = bean;
        _properties = properties;
    }

    public BeanDescriptor getBeanDescriptor() {
        return _bean;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
        return _properties;
    }
}
