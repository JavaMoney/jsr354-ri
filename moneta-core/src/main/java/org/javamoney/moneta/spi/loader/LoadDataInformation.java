/*
  Copyright (c) 2012, 2024, Werner Keil and others by the @author tag.

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
package org.javamoney.moneta.spi.loader;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.javamoney.moneta.spi.loader.LoaderService.Listener;
import org.javamoney.moneta.spi.loader.LoaderService.UpdatePolicy;

/**
 * To create this instance
 * @see LoadDataInformationBuilder
 * @author otaviojava
 */
public class LoadDataInformation {

	private final String resourceId;

	private final UpdatePolicy updatePolicy;

    private final Map<String, String> properties;

    private final Listener listener;

    private final URI backupResource;

    private final URI[] resourceLocations;

    private final boolean startRemote;

	LoadDataInformation(String resourceId, UpdatePolicy updatePolicy,
			Map<String, String> properties, LoaderService.Listener listener,
			URI backupResource, URI[] resourceLocations, boolean startRemote) {
		this.resourceId = resourceId;
		this.updatePolicy = updatePolicy;
		this.properties = properties;
		this.listener = listener;
		this.backupResource = backupResource;
		this.resourceLocations = resourceLocations;
		this.startRemote = startRemote;
	}

	public String getResourceId() {
		return resourceId;
	}

	public UpdatePolicy getUpdatePolicy() {
		return updatePolicy;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public LoaderService.Listener getLoaderListener() {
		return listener;
	}

	public URI getBackupResource() {
		return backupResource;
	}

	public URI[] getResourceLocations() {
		return resourceLocations;
	}

	public boolean isStartRemote() {
		return startRemote;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(resourceId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (LoadDataInformation.class.isInstance(obj)) {
			LoadDataInformation other = LoadDataInformation.class.cast(obj);
			return Objects.equals(other.resourceId, resourceId);
		}
		return false;
	}

	@Override
	public String toString() {
        return LoadDataInformation.class.getName() + '{' +
                " resourceId: " + resourceId + ',' +
                " updatePolicy: " + updatePolicy + ',' +
                " properties: " + properties + ',' +
                " Listener: " + listener + ',' +
                " backupResource: " + backupResource + ',' +
                " resourceLocations: " + Arrays.toString(resourceLocations) + '}';
	}

}
