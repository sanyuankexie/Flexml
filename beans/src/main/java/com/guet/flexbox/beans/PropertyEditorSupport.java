/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.guet.flexbox.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PropertyEditorSupport implements PropertyEditor {

    Object source = null;

    List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

    Object newValue = null;

    public PropertyEditorSupport(Object source) {
        if (source == null) {
            throw new NullPointerException("source is null"); //$NON-NLS-1$
        }
        this.source = source;
    }

    public PropertyEditorSupport() {
        source = this;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        if (newValue instanceof String) {
            setValue(text);
        } else {
            throw new IllegalArgumentException(text);
        }
    }

    public String[] getTags() {
        return null;
    }

    public String getJavaInitializationString() {
        return "???"; //$NON-NLS-1$
    }

    public String getAsText() {
        return newValue == null ? "null" : newValue.toString(); //$NON-NLS-1$
    }

    public void setValue(Object value) {
        this.newValue = value;
        firePropertyChange();
    }

    public Object getValue() {
        return newValue;
    }

    public void setSource(Object source) {        
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public synchronized void removePropertyChangeListener(
            PropertyChangeListener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public synchronized void addPropertyChangeListener(
            PropertyChangeListener listener) {
        listeners.add(listener);
    }

    public boolean supportsCustomEditor() {
        return false;
    }

    public boolean isPaintable() {
        return false;
    }

    public void firePropertyChange() {
        if (listeners.isEmpty()) {
            return;
        }

        List<PropertyChangeListener> copy = new ArrayList<PropertyChangeListener>(
                listeners.size());
        synchronized (listeners) {
            copy.addAll(listeners);
        }

        PropertyChangeEvent changeAllEvent = new PropertyChangeEvent(source,
                null, null, null);
        for (Iterator<PropertyChangeListener> listenersItr = copy.iterator(); listenersItr
                .hasNext();) {
            PropertyChangeListener listna = listenersItr.next();
            listna.propertyChange(changeAllEvent);
        }
    }
}
