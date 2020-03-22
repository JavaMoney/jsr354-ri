/*
  Copyright (c) 2012, 2019, Anatole Tresch, Werner Keil and others by the @author tag.

  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain a copy of
  the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  License for the specific language governing permissions and limitations under
  the License.
 */
package org.javamoney.moneta.spi;

import javax.annotation.Priority;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator implementation for ordering services loaded based on their increasing priority values.
 */
public class PriorityServiceComparator implements Comparator<Object>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final PriorityServiceComparator INSTANCE = new PriorityServiceComparator();

    /** Singleton constructor. */
    private PriorityServiceComparator(){}

    /**
     * Get the shared instance of the comparator.
     * @return the shared instance, never null.
     */
    public static PriorityServiceComparator getInstance(){
        return INSTANCE;
    }

    @Override
    public int compare(Object o1, Object o2) {
        int prio = getPriority(o1) - getPriority(o2);
        if (prio < 0) {
            return 1;
        } else if (prio > 0) {
            return -1;
        } else {
            return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
        }
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
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static int getPriority(Class type) {
        int prio = 1;
		Priority priority = (Priority)type.getAnnotation(Priority.class);
        if (priority != null) {
            prio = priority.value();
        }
        return prio;
    }
}
