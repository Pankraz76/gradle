/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.gradle.util.internal.CollectionUtils.join;

@FunctionalInterface
public interface TypeStringFormatter {

    static String format(Type type) {
        if (type instanceof Class) {
            Class<?> aClass = (Class) type;
            Class<?> enclosingClass = aClass.getEnclosingClass();
            if (enclosingClass != null) {
                String ownName = aClass.isAnonymousClass() ? "<anonymous>" : aClass.getSimpleName();
                return format(enclosingClass) + "$" + ownName;
            } else {
                return aClass.getSimpleName();
            }
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            StringBuilder builder = new StringBuilder();
            builder.append(format(parameterizedType.getRawType()));
            builder.append("<");
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for (int i = 0; i < actualTypeArguments.length; i++) {
                Type typeParam = actualTypeArguments[i];
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(format(typeParam));
            }
            builder.append(">");
            return builder.toString();
        }

        return type.toString();
    }

    default String format(String qualifier, List<? extends Type> types) {
        if (types.size() == 1) {
            return qualifier + " " + formatTypes(types);
        } else {
            return qualifier + "s " + formatTypes(types);
        }
    }

    default String formatTypes(List<? extends Type> types) {
        if (types.size() == 1) {
            return format(types.get(0));
        } else {
            return join(", ", types, this::format);
        }
    }

    // Functional interface method - can be used as a method reference
    String formatType(Type type);
}
