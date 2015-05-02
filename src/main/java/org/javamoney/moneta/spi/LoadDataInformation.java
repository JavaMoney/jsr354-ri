package org.javamoney.moneta.spi;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

import org.javamoney.moneta.spi.LoaderService.LoaderListener;
import org.javamoney.moneta.spi.LoaderService.UpdatePolicy;

/**
 * To create this instance
 * @see {@link LoadDataInformationBuilder}
 * @author otaviojava
 */
public class LoadDataInformation {

	private final String resourceId;

	private final UpdatePolicy updatePolicy;

    private final Map<String, String> properties;

    private final LoaderListener loaderListener;

    private final URI backupResource;

    private final URI[] resourceLocations;

	LoadDataInformation(String resourceId, UpdatePolicy updatePolicy,
			Map<String, String> properties, LoaderListener loaderListener,
			URI backupResource, URI[] resourceLocations) {
		this.resourceId = resourceId;
		this.updatePolicy = updatePolicy;
		this.properties = properties;
		this.loaderListener = loaderListener;
		this.backupResource = backupResource;
		this.resourceLocations = resourceLocations;
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

	public LoaderListener getLoaderListener() {
		return loaderListener;
	}

	public URI getBackupResource() {
		return backupResource;
	}

	public URI[] getResourceLocations() {
		return resourceLocations;
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
		StringBuilder sb = new StringBuilder();
		sb.append(LoadDataInformation.class.getName()).append('{')
		.append(" resourceId: ").append(resourceId).append(',')
		.append(" updatePolicy: ").append(updatePolicy).append(',')
		.append(" properties: ").append(properties).append(',')
		.append(" LoaderListener: ").append(loaderListener).append(',')
		.append(" backupResource: ").append(backupResource).append(',')
		.append(" resourceLocations: ").append(resourceLocations).append('}');
		return sb.toString();
	}

}
