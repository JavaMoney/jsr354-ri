package org.javamoney.moneta.internal.loader;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

class LoadDataLocalLoaderService {

	private static final Logger LOG = Logger.getLogger(LoadDataLocalLoaderService.class.getName());

	private final Map<String, LoadableResource> resources;

	private final DefaultLoaderListener listener;


	 LoadDataLocalLoaderService(Map<String, LoadableResource> resources,
			DefaultLoaderListener listener) {
		this.resources = resources;
		this.listener = listener;
	}


	public boolean execute(String resourceId) {
	        LoadableResource load = this.resources.get(resourceId);
	        if (Objects.nonNull(load)) {
	            try {
	                if (load.loadFallback()) {
	                	listener.trigger(resourceId, load.getDataStream());
	                    return true;
	                }
	            } catch (Exception e) {
	                LOG.log(Level.SEVERE, "Failed to load resource locally: " + resourceId, e);
	            }
	        } else {
	            throw new IllegalArgumentException("No such resource: " + resourceId);
	        }
	        return false;
	    }

	@Override
	public String toString() {
		return LoadDataLocalLoaderService.class.getName() + '{' + " resources: "
				+ resources + ", defaultLoaderListener: " + listener + '}';
	}
}
