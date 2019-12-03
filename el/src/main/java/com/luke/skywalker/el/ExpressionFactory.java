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

package com.luke.skywalker.el;

import org.apache.el.ExpressionFactoryImpl;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * @since 2.1
 */
@SuppressWarnings("WeakerAccess")
public abstract class ExpressionFactory {

    /**
     * Create a new {@link ExpressionFactory}. The class to use is determined by
     * the following search order:
     * <ol>
     * <li>services API (META-INF/services/javax.el.ExpressionFactory)</li>
     * <li>$JRE_HOME/lib/el.properties - key javax.el.ExpressionFactory</li>
     * <li>javax.el.ExpressionFactory</li>
     * <li>Platform default implementation -
     *     org.apache.el.ExpressionFactoryImpl</li>
     * </ol>
     * @return the new ExpressionFactory
     */
    public static ExpressionFactory newInstance() {
        return new ExpressionFactoryImpl();
    }

    /**
     * Create a new value expression.
     *
     * @param context      The EL context for this evaluation
     * @param expression   The String representation of the value expression
     * @param expectedType The expected type of the result of evaluating the
     *                     expression
     *
     * @return A new value expression formed from the input parameters
     *
     * @throws NullPointerException
     *              If the expected type is <code>null</code>
     * @throws ELException
     *              If there are syntax errors in the provided expression
     */
    public abstract ValueExpression createValueExpression(ELContext context,
            String expression, Class<?> expectedType);

    public abstract ValueExpression createValueExpression(Object instance,
            Class<?> expectedType);

    /**
     * Create a new method expression instance.
     *
     * @param context            The EL context for this evaluation
     * @param expression         The String representation of the method
     *                           expression
     * @param expectedReturnType The expected type of the result of invoking the
     *                           method
     * @param expectedParamTypes The expected types of the input parameters
     *
     * @return A new method expression formed from the input parameters.
     *
     * @throws NullPointerException
     *              If the expected parameters types are <code>null</code>
     * @throws ELException
     *              If there are syntax errors in the provided expression
     */
    public abstract MethodExpression createMethodExpression(ELContext context,
            String expression, Class<?> expectedReturnType,
            Class<?>[] expectedParamTypes);

    /**
     * Coerce the supplied object to the requested type.
     *
     * @param obj          The object to be coerced
     * @param expectedType The type to which the object should be coerced
     *
     * @return An instance of the requested type.
     *
     * @throws ELException
     *              If the conversion fails
     */
    public abstract Object coerceToType(Object obj, Class<?> expectedType);

    /**
     * @return This default implementation returns null
     *
     * @since EL 3.0
     */
    public ELResolver getStreamELResolver() {
        return null;
    }

    /**
     * @return This default implementation returns null
     *
     * @since EL 3.0
     */
    public Map<String,Method> getInitFunctionMap() {
        return null;
    }

}
