package com.guet.flexbox.beans;

import java.lang.reflect.Method;
import java.util.Objects;

public final class PropertyDescriptor extends FeatureDescriptor {

    private Method _readMethod;
    private Method _writeMethod;

    PropertyDescriptor(String propertyName) {
        super.setName(propertyName);
    }

    @Override
    public void setName(String name) {
    }

    public Class<?> getPropertyType() {
        Class<?> result = null;
        if (_readMethod != null) {
            result = _readMethod.getReturnType();
        } else if (_writeMethod != null) {
            Class<?>[] parameterTypes = _writeMethod.getParameterTypes();
            result = parameterTypes[0];
        }
        return result;
    }

    public Method getReadMethod() {
        return _readMethod;
    }

    void setReadMethod(Method readMethod) {
        this._readMethod = readMethod;
    }

    public Method getWriteMethod() {
        return _writeMethod;
    }

    void setWriteMethod(Method writeMethod) {
        this._writeMethod = writeMethod;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = object instanceof PropertyDescriptor;
        if (result) {
            PropertyDescriptor pd = (PropertyDescriptor) object;
            boolean gettersAreEqual = (this._readMethod == null)
                && (pd.getReadMethod() == null) || (this._readMethod != null)
                && (this._readMethod.equals(pd.getReadMethod()));
            boolean settersAreEqual = (this._writeMethod == null)
                && (pd.getWriteMethod() == null) || (this._writeMethod != null)
                && (this._writeMethod.equals(pd.getWriteMethod()));
            result = gettersAreEqual && settersAreEqual;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_readMethod, _writeMethod);
    }
}
