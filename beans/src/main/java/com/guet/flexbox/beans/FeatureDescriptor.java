package com.guet.flexbox.beans;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Common base class for Descriptors.
 */
public class FeatureDescriptor {

    private Map<String, Object> values;

    String name;

    /**
     * <p>
     * Constructs an instance.
     * </p>
     */
    public FeatureDescriptor() {
        this.values = new HashMap<String, Object>();
    }

    /**
     * <p>
     * Sets the value for the named attribute.
     * </p>
     *
     * @param attributeName
     *            The name of the attribute to set a value with.
     * @param value
     *            The value to set.
     */
    public void setValue(String attributeName, Object value) {
        if (attributeName == null || value == null) {
            throw new NullPointerException();
        }
        values.put(attributeName, value);
    }

    /**
     * <p>
     * Gets the value associated with the named attribute.
     * </p>
     *
     * @param attributeName
     *            The name of the attribute to get a value for.
     * @return The attribute's value.
     */
    public Object getValue(String attributeName) {
        if (attributeName != null) {
            return values.get(attributeName);
        }
        return null;
    }

    /**
     * <p>
     * Enumerates the attribute names.
     * </p>
     *
     * @return An instance of {@link Enumeration}.
     */
    public Enumeration<String> attributeNames() {
        // Create a new list, so that the references are copied
        return Collections.enumeration(new LinkedList<String>(values.keySet()));
    }

    /**
     * <p>
     * Sets the name.
     * </p>
     *
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>
     * Gets the name.
     * </p>
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

}
