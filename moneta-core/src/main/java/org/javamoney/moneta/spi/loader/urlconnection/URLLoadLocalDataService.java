/*
  Copyright (c) 2012, 2015, Credit Suisse (Anatole Tresch), Werner Keil and others by the @author tag.

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
package org.javamoney.moneta.spi.loader.urlconnection;

import org.javamoney.moneta.spi.loader.ConnectionLoaderListener;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

class URLLoadLocalDataService {

	private static final Logger LOG = Logger.getLogger(URLLoadLocalDataService.class.getName());

	private final Map<String, LoadableURLResource> resources;

	private final ConnectionLoaderListener listener;


	 URLLoadLocalDataService(Map<String, LoadableURLResource> resources,
							 ConnectionLoaderListener listener) {
		this.resources = resources;
		this.listener = listener;
	}


	public boolean execute(String resourceId) {
	        LoadableURLResource load = this.resources.get(resourceId);
	        if (Objects.nonNull(load)) {
	            try {
	                if (load.loadFallback()) {
	                	listener.trigger(resourceId, load);
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
		return URLLoadLocalDataService.class.getName() + '{' + " resources: "
				+ resources + ", defaultLoaderListener: " + listener + '}';
	}
}
