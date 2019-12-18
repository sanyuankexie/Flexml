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

package org.apache.harmony.beans.editors;

import com.guet.flexbox.beans.PropertyEditorSupport;

public final class BooleanEditor extends PropertyEditorSupport {

    public BooleanEditor(Object source) {
        super(source);
    }

    public BooleanEditor() {
        super();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if(text == null){
            throw new NullPointerException();
        }
        if ("true".equalsIgnoreCase(text) || "false".equalsIgnoreCase(text)) { //$NON-NLS-1$ //$NON-NLS-2$
            setValue(new Boolean(text));
        } else {
            throw new IllegalArgumentException(text);
        }
    }

    @Override
    public String getAsText() {
        Object value = getValue();
        if (value == null) {
            return null;
        }
        return Boolean.TRUE.equals(value) ? "True" : "False"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public String getJavaInitializationString() {
        return getValueAsString();
    }

    @Override
    public String[] getTags() {
        return new String[] { "True", "False" }; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Boolean) {
            super.setValue(value);
        }
    }

    private String getValueAsString() {
        Object value = getValue();
        if (value != null) {
            return ((Boolean) value).toString();
        }
        return null;
    }
}
