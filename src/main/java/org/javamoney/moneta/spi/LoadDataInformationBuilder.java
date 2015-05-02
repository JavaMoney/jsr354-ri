package org.javamoney.moneta.spi;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

import org.javamoney.moneta.spi.LoaderService.LoaderListener;
import org.javamoney.moneta.spi.LoaderService.UpdatePolicy;

/**
 * Builder to {@link LoadDataInformation}
 * Programmatically registers a remote resource {@code resourceLocation},
 * backed up by a classpath resource {@code backupResource}, reachable as
 * {@code dataId}.
 *
 * @param resourceId        The unique identifier of the resource that must also be used
 *                          for accessing the resource, not {@code null}.
 * @param resourceLocations The remote resource locations, not {@code null}.
 * @param backupResource    The backup resource location in the classpath, not
 *                          {@code null}.
 * @param loaderListener    An (optional) LoaderListener to be registered.
 */
public class LoadDataInformationBuilder {

	private String resourceId;

	private UpdatePolicy updatePolicy;

    private Map<String, String> properties;

    private LoaderListener loaderListener;

    private URI backupResource;

    private URI[] resourceLocations;

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

	public LoadDataInformationBuilder withLoaderListener(LoaderListener loaderListener) {
		this.loaderListener = loaderListener;
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
				loaderListener, backupResource, resourceLocations);
	}


}
