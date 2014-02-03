/*
 *  Copyright (c) 2012, 2013, Credit Suisse (Anatole Tresch), Werner Keil.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.javamoney.moneta.loader.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.money.spi.Bootstrap;

import org.javamoney.moneta.spi.LoaderService;

/**
 * This class provides a mechanism to register resources, that may be updated
 * regularly. The implementation, based on the {@link UpdatePolicy}
 * loads/updates the resources from arbitrary locations and stores it to the
 * interal file cache.
 * 
 * @author Anatole Tresch
 */
public class DefaultLoaderService implements LoaderService {
	/** Logger used. */
	private static final Logger LOG = Logger
			.getLogger(DefaultLoaderService.class.getName());
	/** The data resources managed by this instance. */
	private Map<String, LoadableResource> resources = new ConcurrentHashMap<>();
	/** The registered {@link LoaderListener} instances. */
	private Map<String, List<LoaderListener>> listenersMap = new ConcurrentHashMap<>();
	/**
	 * The local resource cache, to allow keeping current data on the local
	 * system.
	 */
	private ResourceCache resourceCache = loadResourceCache();
	/** The thread pool used for loading of data, triggered by the timer. */
	private ExecutorService executors = Executors.newCachedThreadPool();
	/**
	 * The configurator reading the initial loads from the javamoney.properties.
	 */
	private LoaderConfigurator configurator = new LoaderConfigurator(this);
	/** The timer used for schedules. */
	private volatile Timer timer = new Timer();

	/**
	 * Constructor, initializing from config.
	 */
	public DefaultLoaderService() {
		// read config
		configurator.load();
	}

	/**
	 * Loads the cache to be used.
	 * 
	 * @return the cache to be used, not null.
	 */
	private ResourceCache loadResourceCache() {
		try {
			this.resourceCache = Bootstrap
					.getService(ResourceCache.class, null);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Error loading ResourceCache instance.", e);
		}
		if (this.resourceCache == null) {
			LOG.fine("No ResourceCache loaded, using default.");
			this.resourceCache = new DefaultResourceCache();
		}
		return this.resourceCache;
	}

	// /**
	// * Get the resource cache.
	// *
	// * @return the {@link ResourceCache} instance used.
	// */
	// public ResourceCache getResourceCache() {
	// return this.resourceCache;
	// }

	/**
	 * Removes a resource managed.
	 * 
	 * @param resourceId
	 *            the resource id.
	 */
	public void unload(String resourceId) {
		LoadableResource res = this.resources.get(resourceId);
		if (res != null) {
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
	public void registerData(String resourceId, UpdatePolicy updatePolicy,
			Map<String, String> properties, URL backupResource,
			URL... resourceLocations) {
		if (resources.containsKey(resourceId)) {
			throw new IllegalArgumentException("Resource : " + resourceId
					+ " already registered.");
		}
		LoadableResource res = new LoadableResource(resourceId, updatePolicy,
				backupResource, resourceLocations);
		this.resources.put(resourceId, res);
		switch (updatePolicy) {
		case NEVER:
			loadDataLocal(resourceId);
			break;
		case ONSTARTUP:
			loadDataAsynch(resourceId);
			break;
		case SCHEDULED:
			addScheduledLoad(res);
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
	 * org.javamoney.moneta.spi.LoaderService#getUpdateConfiguration(java.lang
	 * .String)
	 */
	@Override
	public Map<String, String> getUpdateConfiguration(String resourceId) {
		LoadableResource res = this.resources.get(resourceId);
		if (res != null) {
			return res.getUpdateConfig();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javamoney.moneta.spi.LoaderService#isDataRegistered(java.lang.String)
	 */
	@Override
	public boolean isDataRegistered(String dataId) {
		return this.resources.containsKey(dataId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javamoney.moneta.spi.LoaderService#getDataIds()
	 */
	@Override
	public Set<String> getDataIds() {
		return this.resources.keySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javamoney.moneta.spi.LoaderService#getData(java.lang.String)
	 */
	@Override
	public InputStream getData(String resourceId) throws IOException {
		LoadableResource res = this.resources.get(resourceId);
		if (res != null) {
			res.getDataStream();
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
		return loadDataSynch(resourceId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javamoney.moneta.spi.LoaderService#loadDataAsynch(java.lang.String)
	 */
	@Override
	public Future<Boolean> loadDataAsynch(final String resourceId) {
		return executors.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return loadDataSynch(resourceId);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javamoney.moneta.spi.LoaderService#loadDataLocal(java.lang.String)
	 */
	@Override
	public boolean loadDataLocal(String resourceId) {
		LoadableResource res = this.resources.get(resourceId);
		if (res != null) {
			try {
				if (res.loadFallback()) {
					triggerListeners(resourceId, res.getDataStream());
					return true;
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Failed to load resource locally: "
						+ resourceId, e);
			}
		} else {
			throw new IllegalArgumentException("No such resource: "
					+ resourceId);
		}
		return false;
	}

	/**
	 * Reload data for a resource synchronously.
	 * 
	 * @param resourceId
	 *            the resource id, not null.
	 * @return true, if loading succeeded.
	 */
	private boolean loadDataSynch(String resourceId) {
		LoadableResource res = this.resources.get(resourceId);
		if (res != null) {
			try {
				if (res.load()) {
					triggerListeners(resourceId, res.getDataStream());
					return true;
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Failed to load resource: " + resourceId,
						e);
			}
		} else {
			throw new IllegalArgumentException("No such resource: "
					+ resourceId);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javamoney.moneta.spi.LoaderService#resetData(java.lang.String)
	 */
	@Override
	public void resetData(String dataId) throws IOException {
		LoadableResource res = this.resources.get(dataId);
		if (res != null) {
			if (res.reset()) {
				triggerListeners(dataId, res.getDataStream());
			}
		} else {
			throw new IllegalArgumentException("No such resource: " + dataId);
		}
	}

	/**
	 * Trigger the listeners registered for the given dataId.
	 * 
	 * @param dataId
	 *            the data id, not null.
	 * @param is
	 *            the InputStream, containing the latest data.
	 */
	private void triggerListeners(String dataId, InputStream is) {
		List<LoaderListener> listeners = getListeners("");
		synchronized (listeners) {
			for (LoaderListener ll : listeners) {
				try {
					ll.newDataLoaded(dataId, is);
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "Error calling LoadListener: " + ll,
							e);
				}
			}
		}
		if (!(dataId == null || dataId.isEmpty())) {
			listeners = getListeners(dataId);
			synchronized (listeners) {
				for (LoaderListener ll : listeners) {
					try {
						ll.newDataLoaded(dataId, is);
					} catch (Exception e) {
						LOG.log(Level.SEVERE, "Error calling LoadListener: "
								+ ll, e);
					}
				}
			}
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
	public void addLoaderListener(LoaderListener l, String... dataIds) {
		if (dataIds.length == 0) {
			List<LoaderListener> listeners = getListeners("");
			synchronized (listeners) {
				listeners.add(l);
			}
		} else {
			for (String dataId : dataIds) {
				List<LoaderListener> listeners = getListeners(dataId);
				synchronized (listeners) {
					listeners.add(l);
				}
			}
		}
	}

	/**
	 * Evaluate the {@link LoaderListener} instances, listening fo a dataId
	 * given.
	 * 
	 * @param dataId
	 *            The dataId, not null
	 * @return the according listeners
	 */
	private List<LoaderListener> getListeners(String dataId) {
		if (dataId == null) {
			dataId = "";
		}
		List<LoaderListener> listeners = this.listenersMap.get(dataId);
		if (listeners == null) {
			synchronized (listenersMap) {
				listeners = this.listenersMap.get(dataId);
				if (listeners == null) {
					listeners = Collections
							.synchronizedList(new ArrayList<LoaderListener>());
					this.listenersMap.put(dataId, listeners);
				}
			}
		}
		return listeners;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javamoney.moneta.spi.LoaderService#removeLoaderListener(org.javamoney
	 * .moneta.spi.LoaderService.LoaderListener, java.lang.String[])
	 */
	@Override
	public void removeLoaderListener(LoaderListener l, String... dataIds) {
		if (dataIds.length == 0) {
			List<LoaderListener> listeners = getListeners("");
			synchronized (listeners) {
				listeners.remove(l);
			}
		} else {
			for (String dataId : dataIds) {
				List<LoaderListener> listeners = getListeners(dataId);
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
		LoadableResource res = this.resources.get(resourceId);
		if (res != null) {
			return res.getUpdatePolicy();
		}
		throw new IllegalArgumentException("No such resource: " + resourceId);
	}

	/**
	 * Create the schedule for the given {@link LoadableResource}.
	 * 
	 * @param loadableResource
	 */
	private void addScheduledLoad(final LoadableResource loadableResource) {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					loadableResource.load();
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "Failed to update remote resource: "
							+ loadableResource.getResourceId(), e);
				}
			}
		};
		Map<String, String> props = loadableResource.getUpdateConfig();
		if (props != null) {
			String value = props.get("period");
			long periodMS = parseDuration(value);
			value = props.get("delay");
			long delayMS = periodMS = parseDuration(value);
			if (periodMS > 0) {
				timer.scheduleAtFixedRate(task, delayMS, periodMS);
			}
			value = props.get("at");
			if (value != null) {
				List<GregorianCalendar> dates = parseDates(value);
				for (GregorianCalendar date : dates) {
					timer.schedule(task, date.getTime(), 3600000 * 24 /* daily */);
				}
			}
		}
	}

	/**
	 * Parse the dates of type HH:mm:ss:nnn, whereas minutes and smaller are
	 * optional.
	 * 
	 * @param value
	 *            the input text
	 * @return the parsed
	 */
	private List<GregorianCalendar> parseDates(String value) {
		String[] parts = value.split(",");
		List<GregorianCalendar> result = new ArrayList<>();
		for (String part : parts) {
			if (part.isEmpty()) {
				continue;
			}
			String[] subparts = part.split(":");
			GregorianCalendar cal = new GregorianCalendar();
			for (int i = 0; i < subparts.length; i++) {
				switch (i) {
				case 0:
					cal.set(GregorianCalendar.HOUR_OF_DAY,
							Integer.parseInt(subparts[i]));
					break;
				case 1:
					cal.set(GregorianCalendar.MINUTE,
							Integer.parseInt(subparts[i]));
					break;
				case 2:
					cal.set(GregorianCalendar.SECOND,
							Integer.parseInt(subparts[i]));
					break;
				case 3:
					cal.set(GregorianCalendar.MILLISECOND,
							Integer.parseInt(subparts[i]));
					break;
				}
			}
			result.add(cal);
		}
		return result;
	}

	/**
	 * Parse a duration of the form HH:mm:ss:nnn, whereas only hours are non
	 * optional.
	 * 
	 * @param value
	 *            the input value
	 * @return the duration in ms.
	 */
	protected long parseDuration(String value) {
		long periodMS = 0L;
		if (value != null) {
			String[] parts = value.split(":");
			for (int i = 0; i < parts.length; i++) {
				switch (i) {
				case 0: // hours
					periodMS += (Integer.parseInt(parts[i])) * 3600000L;
					break;
				case 1: // minutes
					periodMS += (Integer.parseInt(parts[i])) * 60000L;
					break;
				case 2: // seconds
					periodMS += (Integer.parseInt(parts[i])) * 1000L;
					break;
				case 3: // ms
					periodMS += (Integer.parseInt(parts[i]));
					break;
				default:
					break;
				}
			}
		}
		return periodMS;
	}

	@Override
	public String toString() {
		return "DefaultLoaderService [resources=" + resources
				+ ", resourceCache=" + resourceCache + "]";
	}
	
	

}
