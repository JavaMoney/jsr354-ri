package org.javamoney.moneta.internal.loader;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

class LoadRemoteDataLoaderService {

	private static final Logger LOG = Logger.getLogger(DefaultLoaderListener.class.getName());

	private final DefaultLoaderListener listener;

	LoadRemoteDataLoaderService(DefaultLoaderListener listener) {
		this.listener = listener;
	}

	public boolean execute(String resourceId,
			Map<String, LoadableResource> resources) {

		LoadableResource load = resources.get(resourceId);
		if (Objects.nonNull(load)) {
			try {
				load.readCache();
				listener.trigger(resourceId, load.getDataStream());
				load.loadRemote();
				listener.trigger(resourceId, load.getDataStream());
				LOG.info("The exchange rate with resourceId " + resourceId + " was started remotely");
				return true;
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

	@Override
	public String toString() {
		return LoadRemoteDataLoaderService.class.getName() + '{' + " listener: " + listener + '}';
	}
}
