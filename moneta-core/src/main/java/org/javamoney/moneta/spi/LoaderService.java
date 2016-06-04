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
package org.javamoney.moneta.spi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * This interface defines an updatable/reloadable data cache for providing data
 * sources that are updatable by any remote {@link URI}s. Initial version are
 * loaded from the classpath, or other fallback URL.
 * <p>
 * This class is used for managing/updating/reloading of data sources, e.g. data
 * streams for exchange rates, additional currency data, historical currency
 * data and so on.
 * <p>
 * Note: this class is implementation specific and not part of the official
 * JSR's API.
 *
 * @author Anatole Tresch
 */
public interface LoaderService {

    /**
     * Platform RI: The update policy defines how and when the
     * {@link LoaderService} tries to update the local cache with newest version of
     * the registered data resources, accessing the configured remote
     * {@link URI}s. By default no remote connections are done (
     * {@link UpdatePolicy#NEVER} ).
     *
     * @author Anatole Tresch
     */
    public enum UpdatePolicy {
        /**
         * The resource will never be updated from remote, only the fallback URL
         * will be evaluated.
         */
        NEVER,
        /**
         * The resource will be loaded automatically from remote only once on
         * startup.
         */
        ONSTARTUP,
        /**
         * The resource will be loaded automatically from remote only once, when
         * accessed the first time.
         */
        LAZY,
        /**
         * The resource should be regularly reloaded based on a schedule.
         */
        SCHEDULED
    }

    /**
     * Callback that can be registered to be informed, when a data item was
     * loaded/updated or resetToFallback.
     *
     * @author Anatole Tresch
     * @see #resetData(String)
     * @see #loadData(String)
     */
    interface LoaderListener {
        /**
         * Callback called from the {@link LoaderService}, when new data was
         * read for a given data item.
         *
         * @param resourceId the resource id
         * @param is         the input stream for accessing the data
         */
        void newDataLoaded(String resourceId, InputStream is);
    }

    /**
     * Programmatically registers a remote resource {@code resourceLocation},
     * backed up by a classpath resource {@code backupResource}, reachable as
     * {@code dataId}.
     *
     *resourceId        The unique identifier of the resource that must also be used
     *                          for accessing the resource, not {@code null}.
     *resourceLocations The remote resource locations, not {@code null}.
     *backupResource    The backup resource location in the classpath, not
     *                          {@code null}.
     *loaderListener    An (optional) LoaderListener to be registered.
     */
    void registerData(LoadDataInformation loadDataInformation);

    /**
     * Programmatically registers a remote resource {@code resourceLocation},
     * backed up by a classpath resource {@code backupResource}, reachable as
     * {@code dataId} and (synchronously) loads the data.
     *
     *resourceId        The unique identifier of the resource that must also be used
     *                          for accessing the resource, not {@code null}.
     * resourceLocations The remote resource locations, not {@code null}.
     *backupResource    The backup resource location in the classpath, not
     *                          {@code null}.
     *loaderListener    An (optional) LoaderListener to be registered.
     */
    void registerAndLoadData(LoadDataInformation loadDataInformation);

    @Deprecated
    void registerAndLoadData(String resourceId, UpdatePolicy updatePolicy,
                             Map<String, String> properties, LoaderListener loaderListener,
                             URI backupResource,
                             URI... resourceLocations);

    @Deprecated
    void registerData(String resourceId, UpdatePolicy updatePolicy,
                      Map<String, String> properties, LoaderListener loaderListener,
                      URI backupResource,
                      URI... resourceLocations);

    /**
     * Get the {@link UpdatePolicy} in place for the given dataId.
     *
     * @param resourceId the resource's id, not {@code null}
     * @return the {@link UpdatePolicy}, not {@code null}
     * @throws IllegalArgumentException if no such dataId is available.
     */
    UpdatePolicy getUpdatePolicy(String resourceId);

    /**
     * Get the update configuration for the given dataId.
     *
     * @param resourceId the dataId, not {@code null}
     * @return the update configuration properties, not {@code null}
     * @throws IllegalArgumentException if no such dataId is available.
     */
    Map<String, String> getUpdateConfiguration(String resourceId);

    /**
     * Add a {@link LoaderListener} callback that is informed when a data
     * resource was update from remote, or resetToFallback. Passing an empty String or
     * {@code null} sa {@code dataId} allows to register a listener for all data
     * resources registered. {@link #loadData(String)}
     * {@link #resetData(String)}
     *
     * @param resourceIds The unique identifiers of the resource, not {@code null}.
     * @param l           The listener to be added
     * @see #removeLoaderListener(LoaderListener, String...)
     */
    void addLoaderListener(LoaderListener l, String... resourceIds);

    /**
     * Remove a registered {@link LoaderListener} callback.
     *
     * @param resourceIds The unique identifier of the resource, not {@code null}.
     * @param l           The listener to be removed
     * @see #addLoaderListener(LoaderListener, String...)
     */
    void removeLoaderListener(LoaderListener l, String... resourceIds);

    /**
     * Allows to check if a data resource with the given dataId is registered.
     *
     * @param resourceId The unique identifier of the resource, not {@code null}.
     * @return {@code true}, if such a data resource is registered.
     */
    boolean isResourceRegistered(String resourceId);

    /**
     * Get a {@link Set} of all registered data resource identifiers.
     *
     * @return a {@link Set} of all registered data resource identifiers, never
     * {@code null}.
     */
    Set<String> getResourceIds();

    /**
     * Access the input stream of the given data resource.
     * <p>This method is called by the modules that depend on the given data
     * item. The method always returns the most current data, either from the
     * classpath or the local cache, depending which flavors are available
     * and recently updated.</p><p>
     * The method must be thread safe and can be accessed in parallel. Hereby it
     * is possible that, when an intermediate update of the data by update
     * occurs, that different input stream content is returned.
     *
     * @param resourceId The unique identifier of the resource, not {@code null}.
     * @return The {@link InputStream} for reading the data.
     * @throws IOException if a problem occurred.
     */
    InputStream getData(String resourceId) throws IOException;

    /**
     * Explicitly triggers the loading of the registered data, regardless of its
     * current {@link UpdatePolicy} configured, from the fallback/local
     * resource.
     *
     * @param resourceId The unique identifier of the resource, not {@code null}.
     * @return true if load was successful.
     */
    boolean loadDataLocal(String resourceId);

    /**
     * Explicitly triggers the remote loading of the registered data, regardless
     * of its current {@link UpdatePolicy} configured.
     *
     * @param resourceId The unique identifier of the resource, not {@code null}.
     * @return true if load was successful.
     * @throws IOException if a problem occurred.
     */
    boolean loadData(String resourceId) throws IOException;

    /**
     * Explicitly asynchronously triggers the remote loading of the registered
     * data, regardless of its current {@link UpdatePolicy} configured.
     *
     * @param resourceId The unique identifier of the resource, not {@code null}.
     * @return the Future of the load task started, returns Boolean.TRUE if the
     * load was successful (either from remote or from the fallback
     * resource).
     */
    Future<Boolean> loadDataAsync(String resourceId);

    /**
     * Explicitly triggers the resetToFallback (loading of the registered data from the
     * classpath backup resource).
     *
     * @param resourceId The unique identifier of the resource, not {@code null}.
     * @throws IOException if a problem occurred.
     */
    void resetData(String resourceId) throws IOException;

}
