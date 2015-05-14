/**
 * Copyright (c) 2012, 2014, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.javamoney.moneta.internal;

import javax.annotation.Priority;
import javax.money.spi.ServiceProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the (default) {@link javax.money.spi.ServiceProvider} interface and hereby uses the JDK
 * {@link java.util.ServiceLoader} to load the services required.
 *
 * @author Anatole Tresch
 */
public class PriorityAwareServiceProvider implements ServiceProvider {
    /**
     * List of services loaded, per class.
     */
    private final ConcurrentHashMap<Class<?>, List<Object>> servicesLoaded = new ConcurrentHashMap<>();

    /**
     * Returns a priority value of 10.
     *
     * @return 10, overriding the default provider.
     */
    @Override
    public int getPriority() {
        return 10;
    }

    /**
     * Loads and registers services.
     *
     * @param serviceType The service type.
     * @param <T>         the concrete type.
     * @return the items found, never {@code null}.
     */
    @Override
    public <T> List<T> getServices(final Class<T> serviceType) {
        @SuppressWarnings("unchecked")
        List<T> found = (List<T>) servicesLoaded.get(serviceType);
        if (found != null) {
            return found;
        }

        return loadServices(serviceType);
    }

    public static int compareServices(Object o1, Object o2) {
        int prio1 = 0;
        int prio2 = 0;
        Priority prio1Annot = o1.getClass().getAnnotation(Priority.class);
        if (prio1Annot != null) {
            prio1 = prio1Annot.value();
        }
        Priority prio2Annot = o2.getClass().getAnnotation(Priority.class);
        if (prio2Annot != null) {
            prio2 = prio2Annot.value();
        }
        if (prio1 < prio2) {
            return 1;
        }
        if (prio2 < prio1) {
            return -1;
        }
        return o2.getClass().getSimpleName().compareTo(o1.getClass().getSimpleName());
    }

    /**
     * Loads and registers services.
     *
     * @param serviceType The service type.
     * @param <T>         the concrete type.
     * @return the items found, never {@code null}.
     */
    private <T> List<T> loadServices(final Class<T> serviceType) {
        List<T> services = new ArrayList<>();
        try {
            for (T t : ServiceLoader.load(serviceType)) {
                services.add(t);
            }
            services.sort(PriorityAwareServiceProvider::compareServices);
            @SuppressWarnings("unchecked")
            final List<T> previousServices = (List<T>) servicesLoaded.putIfAbsent(serviceType, (List<Object>) services);
            return Collections.unmodifiableList(previousServices != null ? previousServices : services);
        } catch (Exception e) {
            Logger.getLogger(PriorityAwareServiceProvider.class.getName()).log(Level.WARNING,
                    "Error loading services of type " + serviceType, e);
            services.sort(PriorityAwareServiceProvider::compareServices);
            return services;
        }
    }

}