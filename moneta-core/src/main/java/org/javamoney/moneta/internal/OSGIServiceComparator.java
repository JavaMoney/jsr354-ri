/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.javamoney.moneta.internal;

import org.osgi.framework.ServiceReference;

import javax.annotation.Priority;
import java.util.Comparator;

/**
 * Comparator implementation for ordering services loaded based on their increasing priority values.
 */
@SuppressWarnings("rawtypes")
class OSGIServiceComparator implements Comparator<ServiceReference> {

    @Override
    public int compare(ServiceReference o1, ServiceReference o2) {
        int prio = getPriority(o1) - getPriority(o2);
        //o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
        return Integer.compare(0, prio);
    }

    /**
     * Checks the given instance for a @Priority annotation. If present the annotation's value is evaluated. If no such
     * annotation is present, a default priority {@code 1} is returned.
     *
     * @param o the instance, not {@code null}.
     * @return a priority, by default 1.
     */
    public static int getPriority(Object o) {
        return getPriority(o.getClass());
    }

    /**
     * Checks the given type optionally annotated with a @Priority. If present the annotation's value is evaluated.
     * If no such annotation is present, a default priority {@code 1} is returned.
     *
     * @param type the type, not {@code null}.
     * @return a priority, by default 1.
     */
    public static int getPriority(Class<?> type) {
        int prio = 1;
        Priority priority = type.getAnnotation(Priority.class);
        if (priority != null) {
            prio = priority.value();
        }
        return prio;
    }
}
