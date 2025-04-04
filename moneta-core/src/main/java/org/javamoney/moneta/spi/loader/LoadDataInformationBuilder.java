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
import java.util.Map;
import java.util.Objects;

import org.javamoney.moneta.spi.loader.LoaderService.Listener;
import org.javamoney.moneta.spi.loader.LoaderService.UpdatePolicy;

/**
 * Builder to {@link LoadDataInformation}
 * Programmatically registers a remote resource {@code resourceLocation},
 * backed up by a classpath resource {@code backupResource}, reachable as
 * {@code dataId}.
 */
public class LoadDataInformationBuilder {
	/** The unique identifier of the resource that must also be used
	 *                          for accessing the resource, not {@code null}. */
	private String resourceId;

	private UpdatePolicy updatePolicy;

    private Map<String, String> properties;
	/** An (optional) Listener to be registered. */
    private Listener listener;
	/**
	 * The backup resource location in the classpath, not
	 *                          {@code null}.
	 */
    private URI backupResource;

    /** The remote resource locations, not {@code null}. */
    private URI[] resourceLocations;

    private boolean startRemote;

	public LoadDataInformationBuilder withResourceId(String resourceId) {
		this.resourceId = resourceId;
		return this;
	}

	public LoadDataInformationBuilder withUpdatePolicy(UpdatePolicy updatePolicy) {
		this.updatePolicy = updatePolicy;
		return this;
	}

	public LoadDataInformationBuilder withProperties(Map<String, String> properties) {
		this.properties = properties;
		return this;
	}

	public LoadDataInformationBuilder withLoaderListener(Listener listener) {
		this.listener = listener;
		return this;
	}

	public LoadDataInformationBuilder withBackupResource(URI backupResource) {
		this.backupResource = backupResource;
		return this;
	}

	public LoadDataInformationBuilder withResourceLocations(URI... resourceLocations) {
		this.resourceLocations = resourceLocations;
		return this;
	}

	public LoadDataInformationBuilder withStartRemote(boolean startRemote) {
		this.startRemote = startRemote;
		return this;
	}

	public LoadDataInformation build() {
		if(Objects.isNull(resourceId) || resourceId.isEmpty()) {
			throw new IllegalStateException("The resourceId should be informed");
		}
		else if (Objects.isNull(updatePolicy)) {
			throw new IllegalStateException("The updatePolicy should be informed");
		}
		else if (Objects.isNull(properties)) {
			throw new IllegalStateException("The properties should be informed");
		}
		else if (Objects.isNull(resourceLocations)) {
			throw new IllegalStateException("The properties should be informed");
		}
		return new LoadDataInformation(resourceId, updatePolicy, properties,
                listener, backupResource, resourceLocations, startRemote);
	}


}
