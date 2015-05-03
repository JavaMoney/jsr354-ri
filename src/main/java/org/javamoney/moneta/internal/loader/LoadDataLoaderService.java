package org.javamoney.moneta.internal.loader;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadDataLoaderService {

	private static final Logger LOG = Logger.getLogger(DefaultLoaderListener.class.getName());

	private final DefaultLoaderListener listener;

   LoadDataLoaderService(DefaultLoaderListener listener) {
		this.listener = listener;
	}

	public boolean execute(String resourceId,
			Map<String, LoadableResource> resources) {

		LoadableResource load = resources.get(resourceId);
		if (Objects.nonNull(load)) {
			try {
				if (load.load()) {
					listener.trigger(resourceId, load.getDataStream());
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

	@Override
	public String toString() {
		return LoadDataLoaderService.class.getName() + '{' + " listener: " + listener + '}';
	}

}
