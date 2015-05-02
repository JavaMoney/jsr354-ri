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
package org.javamoney.moneta.internal.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.spi.Bootstrap;

import org.javamoney.moneta.spi.LoadDataInformation;
import org.javamoney.moneta.spi.LoaderService;

/**
 * This class provides a mechanism to register resources, that may be updated
 * regularly. The implementation, based on the {@link UpdatePolicy}
 * loads/updates the resources from arbitrary locations and stores it to the
 * format file cache. Default loading tasks can be configured within the javamoney.properties
 * file, @see org.javamoney.moneta.loader.format.LoaderConfigurator .
 * <p>
 * @author Anatole Tresch
 */
public class DefaultLoaderService implements LoaderService {
    /**
     * Logger used.
     */
    private static final Logger LOG = Logger.getLogger(DefaultLoaderService.class.getName());
    /**
     * The data resources managed by this instance.
     */
    private final Map<String, LoadableResource> resources = new ConcurrentHashMap<>();
    /**
     * The registered {@link LoaderListener} instances.
     */
     private final DefaultLoaderListener listener = new DefaultLoaderListener();

    /**
     * The local resource cache, to allow keeping current data on the local
     * system.
     */
    private static final ResourceCache CACHE = loadResourceCache();
    /**
     * The thread pool used for loading of data, triggered by the timer.
     */
    private final ExecutorService executors = Executors.newCachedThreadPool(DaemonThreadFactory.INSTANCE);

    private final DefaultLoaderServiceFacade defaultLoaderServiceFacade;

    /**
     * The timer used for schedules.
     */
    private volatile Timer timer;

    /**
     * Constructor, initializing from config.
     */
    public DefaultLoaderService() {
        initialize();
        defaultLoaderServiceFacade = new DefaultLoaderServiceFacade(timer, listener, resources);
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
        LoaderConfigurator configurator = new LoaderConfigurator(this);
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
                    DefaultResourceCache::new);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error loading ResourceCache instance.", e);
            return new DefaultResourceCache();
        }
    }

    /**
     * Get the resource cache loaded.
     *
     * @return the resource cache, not null.
     */
    static ResourceCache getResourceCache() {
        return DefaultLoaderService.CACHE;
    }

    /**
     * Removes a resource managed.
     *
     * @param resourceId the resource id.
     */
    public void unload(String resourceId) {
        LoadableResource res = this.resources.get(resourceId);
        if (Objects.nonNull(res)) {
            res.unload();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.javamoney.moneta.spi.LoaderService#registerData(java.lang.String,
     * org.javamoney.moneta.spi.LoaderService.UpdatePolicy, java.util.Map,
     * java.net.URL, java.net.URL[])
     */
    @Override
    public void registerData(LoadDataInformation loadDataInformation) {

        if (resources.containsKey(loadDataInformation.getResourceId())) {
            throw new IllegalArgumentException("Resource : " + loadDataInformation.getResourceId() + " already registered.");
        }

		LoadableResource resource = new LoadableResourceBuilder()
				.withCache(CACHE).withLoadDataInformation(loadDataInformation)
				.build();
        this.resources.put(loadDataInformation.getResourceId(), resource);

        if (loadDataInformation.getLoaderListener() != null) {
            this.addLoaderListener(loadDataInformation.getLoaderListener(), loadDataInformation.getResourceId());
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

    /*
    * (non-Javadoc)
    *
    * @see
    * org.javamoney.moneta.spi.LoaderService#registerAndLoadData(java.lang.String,
    * org.javamoney.moneta.spi.LoaderService.UpdatePolicy, java.util.Map,
    * java.net.URL, java.net.URL[])
    */
    @Override
    public void registerAndLoadData(LoadDataInformation loadDataInformation) {

        if (resources.containsKey(loadDataInformation.getResourceId())) {
            throw new IllegalArgumentException("Resource : " + loadDataInformation.getResourceId() + " already registered.");
        }
		LoadableResource resource = new LoadableResourceBuilder()
				.withCache(CACHE).withLoadDataInformation(loadDataInformation)
				.build();
        this.resources.put(loadDataInformation.getResourceId(), resource);


        if (loadDataInformation.getLoaderListener() != null) {
            this.addLoaderListener(loadDataInformation.getLoaderListener(), loadDataInformation.getResourceId());
        }

        switch (loadDataInformation.getUpdatePolicy()) {
            case SCHEDULED:
            	defaultLoaderServiceFacade.scheduledData(resource);
                break;
            case LAZY:
            default:
                break;
        }
        loadData(loadDataInformation.getResourceId());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.javamoney.moneta.spi.LoaderService#getUpdateConfiguration(java.lang
     * .String)
     */
    @Override
    public Map<String, String> getUpdateConfiguration(String resourceId) {
        LoadableResource load = this.resources.get(resourceId);
        if (Objects.nonNull(load)) {
            return load.getProperties();
        }
        return null;
    }
    /*
     * (non-Javadoc)
     *
     * @see
     * org.javamoney.moneta.spi.LoaderService#isResourceRegistered(java.lang.String)
     */
    @Override
    public boolean isResourceRegistered(String resourceId) {
        return this.resources.containsKey(resourceId);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.javamoney.moneta.spi.LoaderService#getResourceIds()
     */
    @Override
    public Set<String> getResourceIds() {
        return this.resources.keySet();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.javamoney.moneta.spi.LoaderService#getData(java.lang.String)
     */
    @Override
    public InputStream getData(String resourceId) throws IOException {
        LoadableResource load = this.resources.get(resourceId);
        if (Objects.nonNull(load)) {
            load.getDataStream();
        }
        throw new IllegalArgumentException("No such resource: " + resourceId);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.javamoney.moneta.spi.LoaderService#loadData(java.lang.String)
     */
    @Override
    public boolean loadData(String resourceId) {
        return defaultLoaderServiceFacade.loadRetome(resourceId, resources);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.javamoney.moneta.spi.LoaderService#loadDataAsync(java.lang.String)
     */
    @Override
    public Future<Boolean> loadDataAsync(final String resourceId) {
        return executors.submit(() -> defaultLoaderServiceFacade.loadRetome(resourceId, resources));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.javamoney.moneta.spi.LoaderService#loadDataLocal(java.lang.String)
     */
    @Override
    public boolean loadDataLocal(String resourceId) {
    	return defaultLoaderServiceFacade.loadDataLocal(resourceId);
    }


    /*
     * (non-Javadoc)
     *
     * @see org.javamoney.moneta.spi.LoaderService#resetData(java.lang.String)
     */
    @Override
    public void resetData(String resourceId) throws IOException {
        LoadableResource load = Optional.ofNullable(this.resources.get(resourceId))
                .orElseThrow(() -> new IllegalArgumentException("No such resource: " + resourceId));
        if (load.resetToFallback()) {
        	listener.trigger(resourceId, load.getDataStream());
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see
     * org.javamoney.moneta.spi.LoaderService#addLoaderListener(org.javamoney
     * .moneta.spi.LoaderService.LoaderListener, java.lang.String[])
     */
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

    /*
     * (non-Javadoc)
     *
     * @see
     * org.javamoney.moneta.spi.LoaderService#removeLoaderListener(org.javamoney
     * .moneta.spi.LoaderService.LoaderListener, java.lang.String[])
     */
    @Override
    public void removeLoaderListener(LoaderListener l, String... resourceIds) {
        if (resourceIds.length == 0) {
            List<LoaderListener> listeners = listener.getListeners("");
            synchronized (listeners) {
                listeners.remove(l);
            }
        } else {
            for (String dataId : resourceIds) {
                List<LoaderListener> listeners = listener.getListeners(dataId);
                synchronized (listeners) {
                    listeners.remove(l);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.javamoney.moneta.spi.LoaderService#getUpdatePolicy(java.lang.String)
     */
    @Override
    public UpdatePolicy getUpdatePolicy(String resourceId) {
        LoadableResource load = Optional.of(this.resources.get(resourceId))
                .orElseThrow(() -> new IllegalArgumentException("No such resource: " + resourceId));
        return load.getUpdatePolicy();
    }

    @Override
    public String toString() {
        return "DefaultLoaderService [resources=" + resources + ']';
    }
}
