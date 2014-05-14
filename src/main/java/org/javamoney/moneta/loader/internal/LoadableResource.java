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
package org.javamoney.moneta.loader.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.javamoney.moneta.spi.LoaderService.UpdatePolicy;

/**
 * This class represent a resource that automatically is reloaded, if needed.
 * 
 * @author Anatole Tresch
 */
public class LoadableResource {
	/** The logger used. */
	private static final Logger LOG = Logger.getLogger(LoadableResource.class
			.getName());
	/** Lock for this instance. */
	private final Object LOCK = new Object();
	/** resource id. */
	private String resourceId;
	/** The {@link UpdatePolicy}. */
	private UpdatePolicy updatePolicy;
	/** The remote URLs to be looked up (first wins). */
	private List<URL> remoteResources = new ArrayList<>();
	/** The fallback location (classpath). */
	private URL fallbackLocation;
	/** The cached resource URL. */
	private URL cachedResource;
	/** How many times this resource was successfully loaded. */
	private AtomicInteger loadCount = new AtomicInteger();
	/** How many times this resource was accessed. */
	private AtomicInteger accessCount = new AtomicInteger();
	/** The current data array. */
	private volatile byte[] data;
	/** THe timestamp of the last successful load. */
	private long lastLoaded;
	/** The registration config. */
	private Map<String, String> updateConfig;

	/**
	 * Create a new instance.
	 * 
	 * @param resourceId
	 *            The dataId.
	 * @param updatePolicy
	 *            The {@link UpdatePolicy}, not null.
	 * @param fallbackLocation
	 *            teh fallback ULR, not null.
	 * @param locations
	 *            the remote locations, not null (but may be empty!)
	 */
	public LoadableResource(String resourceId, UpdatePolicy updatePolicy,
			URL fallbackLocation, URL... locations) {
		Objects.requireNonNull(resourceId, "resourceId required");
		Objects.requireNonNull(fallbackLocation, "classpathDefault required");
		Objects.requireNonNull(updatePolicy, "UpdatePolicy required");
		this.resourceId = resourceId;
		this.fallbackLocation = fallbackLocation;
		this.remoteResources.addAll(Arrays.asList(locations));
		this.updatePolicy = updatePolicy;
	}

	/**
	 * Loads the resource, first from the remote resources, if that fails from
	 * the fallback location.
	 * 
	 * @return true, if load succeeded.
	 */
	public boolean load() {
		if (!loadRemote()) {
			return loadFallback();
		}
		return true;
	}

	/**
	 * Get the resourceId.
	 * 
	 * @return the resourceId
	 */
	public final String getResourceId() {
		return resourceId;
	}

	/**
	 * Get the {@link UpdatePolicy}.
	 * 
	 * @return the updatePolicy
	 */
	public UpdatePolicy getUpdatePolicy() {
		return updatePolicy;
	}

	/**
	 * Get the remote locations.
	 * 
	 * @return the remote locations, maybe empty.
	 */
	public final List<URL> getRemoteResources() {
		return Collections.unmodifiableList(remoteResources);
	}

	/**
	 * Return the fallback location.
	 * 
	 * @return the fallback location
	 */
	public final URL getFallbackResource() {
		return fallbackLocation;
	}

	/**
	 * Get the URL of the locally cached resource.
	 * 
	 * @return the cachedResource
	 */
	public final URL getCachedResource() {
		return cachedResource;
	}

	/**
	 * Get the number of active loads of this resource (InputStream).
	 * 
	 * @return the number of successful loads.
	 */
	public final int getLoadCount() {
		return loadCount.get();
	}

	/**
	 * Get the number of successful accesses.
	 * 
	 * @return the number of successful accesses.
	 */
	public final int getAccessCount() {
		return accessCount.get();
	}

	/**
	 * Get the resource data. This will trigger a full load, if the resource is
	 * not loaded, e.g. for LAZY resources.
	 * 
	 * @return the data to load.
	 */
	public final byte[] getData() {
		accessCount.incrementAndGet();
		if (this.data == null) {
			synchronized (LOCK) {
				if (this.data == null) {
					if (!loadRemote()) {
						loadFallback();
					}
				}
				if (this.data == null) {
					throw new IllegalStateException(
							"Failed to load remote as well as fallback resources for "
									+ this);
				}
			}
		}
		return data.clone();
	}

	/**
	 * Get the resource data as input stream.
	 * 
	 * @return the input stream.
	 */
	public InputStream getDataStream() {
		return new WrappedInputStream(new ByteArrayInputStream(getData()));
	}

	/**
	 * Get the timestamp of the last succesful load.
	 * 
	 * @return the lastLoaded
	 */
	public final long getLastLoaded() {
		return lastLoaded;
	}

	/**
	 * Try to load the resource from the remote locations.
	 * 
	 * @return true, on success.
	 */
	public boolean loadRemote() {
		for (URL itemToLoad : remoteResources) {
			try {
				load(itemToLoad, false);
				return true;
			} catch (Exception e) {
				LOG.log(Level.INFO, "Failed to load resource: " + itemToLoad, e);
			}
		}
		return false;
	}

	/**
	 * Try to load the resource from the faööback resources. This will override
	 * any remote data already loaded.
	 * 
	 * @return true, on success.
	 */
	public boolean loadFallback() {
		try {
			load(fallbackLocation, true);
			return true;
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to load fallback resource: "
					+ fallbackLocation, e);
		}
		return false;
	}

	/**
	 * Load the data.
	 * 
	 * @param itemToLoad
	 *            the target {@link URL}
	 * @param fallbackLoad
	 *            true, for a fallback URL.
	 * @throws IOException
	 *             if load fails.
	 */
	private void load(URL itemToLoad, boolean fallbackLoad) throws IOException {
		InputStream is = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			URLConnection conn = itemToLoad.openConnection();
			byte[] data = new byte[4096];
			is = conn.getInputStream();
			int read = is.read(data);
			while (read > 0) {
				bos.write(data, 0, read);
				read = is.read(data);
			}
			this.data = bos.toByteArray();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "Error closing resource input for "
							+ resourceId, e);
				}
			}
			if (bos != null) {
				bos.close();
			}
		}
		if (!fallbackLoad) {
			lastLoaded = System.currentTimeMillis();
			loadCount.incrementAndGet();
		}
	}

	/**
	 * Unloads the data.
	 */
	public void unload() {
		synchronized (LOCK) {
			int count = accessCount.decrementAndGet();
			if (count == 0) {
				this.data = null;
			}
		}
	}

	/**
	 * InputStream , that helps managing the load count.
	 * 
	 * @author Anatole
	 * 
	 */
	private final class WrappedInputStream extends InputStream {

		private InputStream wrapped;

		public WrappedInputStream(InputStream wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public int read() throws IOException {
			return wrapped.read();
		}

		@Override
		public void close() throws IOException {
			try {
				wrapped.close();
				super.close();
			} finally {
				unload();
			}
		}

	}

	/**
	 * Explcitly override the resource wih the fallback context and resets the
	 * load counter.
	 * 
	 * @return true on success.
	 * @throws IOException
	 */
	public boolean reset() throws IOException {
		if (loadFallback()) {
			loadCount.set(0);
			return true;
		}
		return false;
	}

	/**
	 * Access the registration config.
	 * 
	 * @return the config, not null.
	 */
	public Map<String, String> getUpdateConfig() {
		return this.updateConfig;
	}

	@Override
	public String toString() {
		return "LoadableResource [resourceId=" + resourceId + ", updatePolicy="
				+ updatePolicy + ", fallbackLocation=" + fallbackLocation
				+ ", remoteResources=" + remoteResources + ", cachedResource="
				+ cachedResource + ", loadCount=" + loadCount
				+ ", accessCount=" + accessCount + ", lastLoaded=" + lastLoaded
				+ ", updateConfig=" + updateConfig + "]";
	}
	
	

}
