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
package org.javamoney.moneta;

import org.javamoney.moneta.internal.PriorityServiceComparator;
import org.osgi.framework.*;

import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ServiceContext implementation based on OSGI Service mechanisms.
 */
public final class OSGIServiceHelper {

    private static final Logger LOG = Logger.getLogger(OSGIServiceHelper.class.getName());

    private OSGIServiceHelper(){}

    public Enumeration<URL> getResources(BundleContext bundleContext, String resource) {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest("Loading resources: " + resource);
        }
        List<URL> result = new ArrayList<>();
        URL url = bundleContext.getBundle().getEntry(resource);
        if(url != null) {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Resource: " + resource + " found in unregistered bundle " + bundleContext.getBundle().getSymbolicName());
            }
            result.add(url);
        }
        for(Bundle bundle: bundleContext.getBundles()) {
            url = bundle.getEntry(resource);
            if (url != null && !result.contains(url)) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Resource: " + resource + " found in registered bundle " + bundle.getSymbolicName());
                }
                result.add(url);
            }
        }
        for(Bundle bundle: bundleContext.getBundles()) {
            url = bundle.getEntry(resource);
            if (url != null && !result.contains(url)) {
                if (LOG.isLoggable(Level.FINEST)) {
                    LOG.finest("Resource: " + resource + " found in unregistered bundle " + bundle.getSymbolicName());
                }
                result.add(url);
            }
        }
        return Collections.enumeration(result);
    }

    public static <T> void registerService(Bundle bundle, Class<T> serviceClass, Class<? extends T> implClass) {
        try {
            // Load the service class
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info("Loaded Service Factory (" + serviceClass.getName() + "): " + implClass.getName());
            }
            // Provide service properties
            Hashtable<String, String> props = new Hashtable<>();
            props.put(Constants.VERSION_ATTRIBUTE, bundle.getVersion().toString());
            String vendor = bundle.getHeaders().get(Constants.BUNDLE_VENDOR);
            props.put(Constants.SERVICE_VENDOR, (vendor != null ? vendor : "anonymous"));
            // Translate annotated @Priority into a service ranking
            props.put(Constants.SERVICE_RANKING,
                    String.valueOf(PriorityServiceComparator.getPriority(implClass)));

            // Register the service factory on behalf of the intercepted bundle
            JDKUtilServiceFactory<T> factory = new JDKUtilServiceFactory<T>(implClass);
            BundleContext bundleContext = bundle.getBundleContext();
            bundleContext.registerService(serviceClass.getName(), factory, props);
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info("Registered service class: " + implClass.getName() + "(" + serviceClass.getName() + ")");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to load service: " + implClass.getName(), e);
        }
    }

    public static <T> void unregisterService(Bundle bundle, Class<T> serviceClass, Class<? extends T> implClass) {
        try {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("Unloading Service (" + serviceClass.getName() + "): " + implClass.getName());
            }
            ServiceReference<?> ref = bundle.getBundleContext().getServiceReference(implClass);
            if (ref != null) {
                bundle.getBundleContext().ungetService(ref);
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to unload service: " + implClass.getName(), e);
        }
    }

    /**
     * Service factory simply instantiating the configured service.
     */
    static class JDKUtilServiceFactory<S> implements ServiceFactory<S> {
        private final Class<? extends S> serviceClass;

        public JDKUtilServiceFactory(Class<? extends S> serviceClass) {
            this.serviceClass = serviceClass;
        }

        @Override
        public S getService(Bundle bundle, ServiceRegistration<S> registration) {
            try {
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine("Creating Service...:" + serviceClass.getName());
                }
                return serviceClass.getConstructor().newInstance();
            } catch (Exception ex) {
                String message = "Failed to create service: " + serviceClass.getName();
                LOG.log(Level.SEVERE, message, ex);
                throw new IllegalStateException(message, ex);
            }
        }

        @Override
        public void ungetService(Bundle bundle, ServiceRegistration<S> registration, S service) {
          // nothing to do
        }
    }

}
