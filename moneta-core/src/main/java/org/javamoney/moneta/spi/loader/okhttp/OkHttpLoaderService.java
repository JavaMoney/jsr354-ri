/*
  Copyright (c) 2023, 2024, Werner Keil and others by the @author tag.

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
package org.javamoney.moneta.spi.loader.okhttp;

import org.javamoney.moneta.spi.loader.*;

import javax.money.spi.Bootstrap;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides a mechanism to register resources, that may be updated
 * regularly. The implementation, based on the {@link UpdatePolicy}
 * loads/updates the resources from arbitrary locations via {@link OkHttpClient} and stores them to the
 * format file cache. Default loading tasks can be configured within the <code>javamoney.properties</code>
 * file.
 * @see LoaderConfigurator
 * @see okhttp3.OkHttpClient
 * @author Werner Keil
 */
@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
public class OkHttpLoaderService implements LoaderService {
    /**
     * Logger used.
     */
    private static final Logger LOG = Logger.getLogger(OkHttpLoaderService.class.getName());
    /**
     * The data resources managed by this instance.
     */
    private final Map<String, LoadableHttpResource> resources = new ConcurrentHashMap<>();
    /**
     * The registered {@link LoaderListener} instances.
     */
     private final ConnectionLoaderListener listener = new ConnectionLoaderListener();

    /**
     * The local resource cache, to allow keeping current data on the local
     * system.
     */
    private static final ResourceCache CACHE = loadResourceCache();
    /**
     * The thread pool used for loading of data, triggered by the timer.
     */
    private final ExecutorService executors = Executors.newCachedThreadPool(DaemonThreadFactory.INSTANCE);

    private OkHttpLoaderServiceFacade defaultLoaderServiceFacade;

    /**
     * The timer used for schedules.
     */
    private volatile Timer timer;

    /**
     * Constructor, initializing from config.
     */
    public OkHttpLoaderService() {
        initialize();
    }

    /**
     * This method reads initial loads from the javamoney.properties and installs the according timers.
     */
     void initialize() {
        // Cancel any running tasks
        Timer oldTimer = timer;
        timer = new Timer(true);
        if (Objects.nonNull(oldTimer)) {
            oldTimer.cancel();
        }
        // (re)initialize
        LoaderConfigurator configurator = LoaderConfigurator.of(this);
        defaultLoaderServiceFacade = new OkHttpLoaderServiceFacade(timer, listener, resources);
        configurator.load();
    }

    /**
     * Loads the cache to be used.
     *
     * @return the cache to be used, not null.
     */
    private static ResourceCache loadResourceCache() {
        try {
            return Optional.ofNullable(Bootstrap.getService(ResourceCache.class)).orElseGet(
                    OkHttpResourceCache::new);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error loading ResourceCache instance.", e);
            return new OkHttpResourceCache();
        }
    }

    /**
     * Get the resource cache loaded.
     *
     * @return the resource cache, not null.
     */
    static ResourceCache getResourceCache() {
        return OkHttpLoaderService.CACHE;
    }

    /**
     * Removes a resource managed.
     *
     * @param resourceId the resource id.
     */
    public void unload(String resourceId) {
        LoadableHttpResource res = this.resources.get(resourceId);
        if (Objects.nonNull(res)) {
            res.unload();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.javamoney.moneta.spi.loader.LoaderService#registerData(java.lang.String,
     * org.javamoney.moneta.spi.loader.LoaderService.UpdatePolicy, java.util.Map,
     * java.net.URL, java.net.URL[])
     */
    @Override
    public void registerData(LoadDataInformation loadDataInformation) {

        if (resources.containsKey(loadDataInformation.getResourceId())) {
            throw new IllegalArgumentException("Resource : " + loadDataInformation.getResourceId() + " already registered.");
        }

		LoadableHttpResource resource = new LoadableHttpResourceBuilder()
				.withCache(CACHE).withLoadDataInformation(loadDataInformation)
				.build();
        this.resources.put(loadDataInformation.getResourceId(), resource);

        if (loadDataInformation.getLoaderListener() != null) {
            this.addLoaderListener(loadDataInformation.getLoaderListener(), loadDataInformation.getResourceId());
        }

        if(loadDataInformation.isStartRemote()) {
        	defaultLoaderServiceFacade.loadDataRemote(loadDataInformation.getResourceId(), resources);
        }
        switch (loadDataInformation.getUpdatePolicy()) {
            case NEVER:
                loadDataLocal(loadDataInformation.getResourceId());
                break;
            case ONSTARTUP:
                loadDataAsync(loadDataInformation.getResourceId());
                break;
            case SCHEDULED:
            	defaultLoaderServiceFacade.scheduledData(resource);
                break;
            case LAZY:
            default:
                break;
        }
    }

    @Override
    public void registerAndLoadData(LoadDataInformation loadDataInformation) {
        registerData(loadDataInformation);
        loadData(loadDataInformation.getResourceId());
    }

    @Override
    public void registerAndLoadData(String resourceId, UpdatePolicy updatePolicy, Map<String, String> properties, LoaderListener loaderListener, URI backupResource, URI... resourceLocations) {
        registerAndLoadData(new LoadDataInformationBuilder()
                .withResourceId(resourceId)
                .withUpdatePolicy(updatePolicy)
                .withProperties(properties)
                .withLoaderListener(loaderListener)
                .withBackupResource(backupResource)
                .withResourceLocations(resourceLocations)
                .build());
    }

    @Override
    public void registerData(String resourceId, UpdatePolicy updatePolicy, Map<String, String> properties, LoaderListener loaderListener, URI backupResource, URI... resourceLocations) {
        if (resources.containsKey(resourceId)) {
            throw new IllegalArgumentException("Resource : " + resourceId + " already registered.");
        }
        LoadDataInformation loadInfo = new LoadDataInformationBuilder()
                .withResourceId(resourceId)
                .withUpdatePolicy(updatePolicy)
                .withProperties(properties)
                .withLoaderListener(loaderListener)
                .withBackupResource(backupResource)
                .withResourceLocations(resourceLocations)
                .build();

        LoadableHttpResource resource = new LoadableHttpResourceBuilder()
                .withCache(CACHE).withLoadDataInformation(loadInfo)
                .build();
        this.resources.put(loadInfo.getResourceId(), resource);

        if (loadInfo.getLoaderListener() != null) {
            this.addLoaderListener(loadInfo.getLoaderListener(), loadInfo.getResourceId());
        }

        switch (loadInfo.getUpdatePolicy()) {
            case SCHEDULED:
                defaultLoaderServiceFacade.scheduledData(resource);
                break;
            case LAZY:
            default:
                break;
        }
    }

    @Override
    public Map<String, String> getUpdateConfiguration(String resourceId) {
        LoadableHttpResource load = this.resources.get(resourceId);
        if (Objects.nonNull(load)) {
            return load.getProperties();
        }
        return null;
    }

    @Override
    public boolean isResourceRegistered(String resourceId) {
        return this.resources.containsKey(resourceId);
    }

    @Override
    public Set<String> getResourceIds() {
        return this.resources.keySet();
    }

    @Override
    public InputStream getData(String resourceId) throws IOException {
        LoadableHttpResource load = this.resources.get(resourceId);
        if (Objects.nonNull(load)) {
            return load.getDataStream();
        }
        throw new IllegalArgumentException("No such resource: " + resourceId);
    }

    @Override
    public boolean loadData(String resourceId) {
        return defaultLoaderServiceFacade.loadData(resourceId, resources);
    }

    @Override
    public Future<Boolean> loadDataAsync(final String resourceId) {
        return executors.submit(() -> defaultLoaderServiceFacade.loadData(resourceId, resources));
    }

    @Override
    public boolean loadDataLocal(String resourceId) {
    	return defaultLoaderServiceFacade.loadDataLocal(resourceId);
    }


    @Override
    public void resetData(String resourceId) throws IOException {
        LoadableHttpResource load = Optional.ofNullable(this.resources.get(resourceId))
                .orElseThrow(() -> new IllegalArgumentException("No such resource: " + resourceId));
        if (load.resetToFallback()) {
        	listener.trigger(resourceId, load);
        }
    }

    @Override
    public void addLoaderListener(LoaderListener l, String... resourceIds) {
        if (resourceIds.length == 0) {
            List<LoaderListener> listeners = listener.getListeners("");
            synchronized (listeners) {
                listeners.add(l);
            }
        } else {
            for (String dataId : resourceIds) {
                List<LoaderListener> listeners = listener.getListeners(dataId);
                synchronized (listeners) {
                    listeners.add(l);
                }
            }
        }
    }

    @Override
    public void removeLoaderListener(LoaderListener loadListener, String... resourceIds) {
        if (resourceIds.length == 0) {
            List<LoaderListener> listeners = listener.getListeners("");
            synchronized (listeners) {
                listeners.remove(loadListener);
            }
        } else {
            for (String dataId : resourceIds) {
                List<LoaderListener> listeners = listener.getListeners(dataId);
                synchronized (listeners) {
                    listeners.remove(loadListener);
                }
            }
        }
    }

    @Override
    public UpdatePolicy getUpdatePolicy(String resourceId) {
        LoadableHttpResource load = Optional.of(this.resources.get(resourceId))
                .orElseThrow(() -> new IllegalArgumentException("No such resource: " + resourceId));
        return load.getUpdatePolicy();
    }

    @Override
    public String toString() {
        return "URLConnectionLoaderService [resources=" + resources + ']';
    }
}
